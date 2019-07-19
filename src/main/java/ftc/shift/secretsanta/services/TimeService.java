package ftc.shift.secretsanta.services;

import ftc.shift.secretsanta.models.Group;
import ftc.shift.secretsanta.repositories.GroupRepository;
import ftc.shift.secretsanta.util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TimeService {

    private final GroupRepository groupRepository;
    private final GameService gameService;
    private static int checkInterval = 60000;//1 min
    long maxDurationInMillis = 8035200000L; //93 days
    private static String dateFormat = "dd.MM.yyyy";

    @Autowired
    public TimeService(@Qualifier("dataBaseGroupRepository") GroupRepository groupRepository, GameService gameService) {
        this.groupRepository = groupRepository;
        this.gameService = gameService;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkTime();
            }
        }, 0, checkInterval);
    }

    private void checkTime() {
        Date now = new Date();
        Collection<Group> groups = groupRepository.getAllGroups();
        synchronized (groups) {
            for (Group group : groups) {
                Date startTimeDate = null;
                Date endTimeDate = null;

                try {
                    startTimeDate = new SimpleDateFormat(dateFormat).parse(group.getStartTime());
                    endTimeDate = new SimpleDateFormat(dateFormat).parse(group.getEndTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (!group.isStarted()) {
                    if (startTimeDate != null && startTimeDate.before(now)) {
                        Logger.log("groupid = " + group.getId() + " started");
                        gameService.arrangeGame(group);
                        group.setStarted(true);
                    }
                }

                if (!group.isFinished()) {
                    if (endTimeDate != null && endTimeDate.before(now))
                        group.setFinished(true);
                }

            }
        }
    }

    boolean isDatesValid(Group group) {
        Date startTimeDate = null;
        Date endTimeDate = null;
        if (group.getStartTime() == null || group.getStartTime().equals(""))
            return false;
        if (group.getEndTime() == null || group.getEndTime().equals(""))
            return false;
        try {

            startTimeDate = new SimpleDateFormat(dateFormat).parse(group.getStartTime());
            endTimeDate = new SimpleDateFormat(dateFormat).parse(group.getEndTime());
            Date now = new Date();

            startTimeDate = zeroDate(startTimeDate);
            endTimeDate = zeroDate(endTimeDate);

            if (!startTimeDate.after(now))
                return false;
            if (!startTimeDate.before(endTimeDate))
                return false;
            if (endTimeDate.getTime() - startTimeDate.getTime() > maxDurationInMillis)
                return false;
            return true;

        } catch (ParseException e) {
            return false;
        }
    }

    private Date zeroDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        return cal.getTime();
    }
}
