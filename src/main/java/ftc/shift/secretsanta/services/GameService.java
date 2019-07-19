package ftc.shift.secretsanta.services;

import ftc.shift.secretsanta.models.GameInfo;
import ftc.shift.secretsanta.models.Group;
import ftc.shift.secretsanta.models.ResponsePreferEntity;
import ftc.shift.secretsanta.models.User;
import ftc.shift.secretsanta.repositories.GameRepository;
import ftc.shift.secretsanta.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final UserService userService;
    private final GroupRepository groupRepository;

    @Autowired
    public GameService(GameRepository gameRepository, UserService userService, @Qualifier("dataBaseGroupRepository") GroupRepository groupRepository) {
        this.gameRepository = gameRepository;
        this.userService = userService;
        this.groupRepository = groupRepository;
    }

    public void arrangeGame(Group group) {
        if (group.getAmount() < 3) {
            group.setFinished(true);
            return;
        }
        group = groupRepository.fetchGroup(group.getId());
        ArrayList<User> collection1 = (ArrayList<User>) group.getAllParticipants();
        GameInfo gameInfo = new GameInfo();
        synchronized (collection1) {
            ArrayList<Integer> collection2 = new ArrayList<>();
            for (int i = 0; i < collection1.size(); i++)
                collection2.add(i);

            boolean marker = false;
            do {
                Collections.shuffle(collection2);
                marker = false;
                for (Integer integer : collection2) {
                    if (integer.equals(collection2.get(integer))) {
                        marker = true;
                    }
                }
            } while (marker);
            gameInfo.setGroup(group);
            int i = 0;
            for (User user : collection1) {
                gameInfo.getLinks().put(user, collection1.get(collection2.get(i)));
                i++;
            }
        }
        gameRepository.createGame(gameInfo);
    }


    public ResponsePreferEntity getGiftInfo(String groupId, String userId) {
        GameInfo gameInfo = gameRepository.fetchGame(groupId);

        User receiver = gameInfo.getLinks().get(userService.provideUser(userId));
        String prefer = gameInfo.getGroup().getPrefer(receiver.getId());
        boolean received = gameInfo.getGroup().isReceived(receiver.getId());

        return new ResponsePreferEntity(receiver, prefer, received);
    }
}
