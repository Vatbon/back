package ftc.shift.sample.services;

import ftc.shift.sample.models.Group;
import ftc.shift.sample.repositories.GroupRepository;
import ftc.shift.sample.util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class TimeService {

    private final GroupRepository groupRepository;
    private final GameService gameService;
    private static int checkInterval = 60000;//1 min
    private static String dateFormat = "dd/MM/yyyy";

    @Autowired
    public TimeService(GroupRepository groupRepository, GameService gameService) {
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
        for (Group group : groupRepository.getAllGroups()) {

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

            if (!startTimeDate.after(now))
                return false;
            if (!startTimeDate.before(endTimeDate))
                return false;
            return true;

        } catch (ParseException e) {
            return false;
        }
    }
}