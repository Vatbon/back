package ftc.shift.secretsanta.services;

import ftc.shift.secretsanta.exception.NotFoundException;
import ftc.shift.secretsanta.models.Group;
import ftc.shift.secretsanta.models.Prefer;
import ftc.shift.secretsanta.models.ResponsePreferEntity;
import ftc.shift.secretsanta.models.User;
import ftc.shift.secretsanta.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserService userService;
    private final TimeService timeService;
    private final GameService gameService;

    @Autowired
    public GroupService(@Qualifier("dataBaseGroupRepository") GroupRepository groupRepository, UserService userService, TimeService timeService, GameService gameService) {
        this.groupRepository = groupRepository;
        this.userService = userService;
        this.timeService = timeService;
        this.gameService = gameService;
    }

    public Collection<Group> provideAllGroups(String userId) {
        if (!userService.isRegistered(userId))
            return null;
        Collection<Group> groups = groupRepository.getAllGroups();
        Collection<Group> result = new ArrayList<>();
        for (Group group : groups) {
            result.add(group.clone());
        }
        for (Group group : result) {
            group.getAllParticipants().clear();
        }
        return result;
    }

    public Group provideGroup(String userId, String groupId) {
        if (!userService.isRegistered(userId))
            return null;
        return groupRepository.fetchGroup(groupId);
    }

    public Group createGroup(String userId, Group group) {
        if (!userService.isRegistered(userId)) {
            return null;
        }
        if (checkRules(group) == -1)
            return null;
        group.setHost(userService.provideUser(userId));
        group.setStarted(false);
        group.setFinished(false);
        group.setAmount(0);
        group.getAllParticipants().clear();
        Group result = groupRepository.createGroup(group);
        User host = userService.provideUser(userId);
        result.addParticipant(host, "");
        host.addGroupAsParticipant(result.getId());
        host.addGroupAsHost(result.getId());
        userService.updateUser(host);
        groupRepository.updateGroup(result.getId(), result);
        group._getParticipants();
        return result;
    }

    private int checkRules(Group group) {
        if (!timeService.isDatesValid(group))
            return -1;

        if (group.getTitle().length() < 3 || group.getTitle().length() > 50)
            return -1;
        if (group.getTitle() == null)
            return -1;

        if (group.getMethod() == null)
            return -1;
        if (group.getMethod().length() > 200)
            return -1;

        if (group.getAmountLimit() != 0)
            if (group.getAmountLimit() < 3)
                return -1;

        if (group.getMinValue() < 0 || group.getMinValue() > group.getMaxValue() || group.getMaxValue() > 5000)
            return -1;
        return 0;
    }

    public Group updateGroup(String userId, String groupId, Group group) {
        if (!userService.isRegistered(userId))
            return null;
        Group old = groupRepository.fetchGroup(groupId);
        if (old == null)
            return null;
        if (!userId.equals(old.getHost().getId()))
            return null;
        if (checkRules(group) == -1)
            return null;

        if (old.getAmountLimit() != group.getAmountLimit())
            return null;
        if (old.getMaxValue() != group.getMaxValue())
            return null;
        if (old.getMinValue() != group.getMinValue())
            return null;
        if (!old.getStartTime().equals(group.getMethod()))
            return null;
        if (!old.getStartTime().equals(group.getStartTime()))
            return null;
        if (!old.getEndTime().equals(group.getEndTime()))
            return null;

        return groupRepository.updateGroup(groupId, group);
    }

    public int deleteGroup(String userId, String groupId) {
        if (!userService.isRegistered(userId))
            return -1;
        if (!userId.equals(groupRepository.fetchGroup(groupId).getHost().getId()))
            return -1;
        groupRepository.deleteGroup(groupId);
        return 0;
    }

    public int joinGroup(String groupId, String userId, String prefer) {
        if (!userService.isRegistered(userId))
            return -1;
        Group group = groupRepository.fetchGroup(groupId);
        if (group.getAmount() + 1 > group.getAmountLimit() & group.getAmountLimit() != 0)
            return -1;
        if (group.hasParticipantById(userService.provideUser(userId).getId()))
            return -1;
        if (prefer == null)
            return -1;
        if (prefer.length() > 200)
            return -1;
        group.addParticipant(userService.provideUser(userId), prefer);
        User user = userService.provideUser(userId);
        user.addGroupAsParticipant(groupId);
        userService.updateUser(user);
        groupRepository.updateGroup(groupId, group);
        return 0;
    }

    public int changePrefer(String groupId, String userId, String prefer) {
        if (!userService.isRegistered(userId))
            return -1;
        if (prefer.length() > 200)
            return -1;
        Group group = groupRepository.fetchGroup(groupId);
        User user = userService.provideUser(userId);
        if (!group.hasParticipantById(user.getId()))
            return -1;
        group.deleteParticipant(user);
        group.addParticipant(user, prefer);
        groupRepository.updateGroup(groupId, group);
        return 0;
    }

    public int receiveGift(String groupId, String userId) {
        if (!userService.isRegistered(userId))
            return -1;
        Group group = groupRepository.fetchGroup(groupId);
        User user = userService.provideUser(userId);
        if (!group.hasParticipantById(user.getId()))
            return -1;
        group.receiveGift(user);
        groupRepository.updateGroup(groupId, group);
        return 0;
    }

    public int leaveGroup(String groupId, String userId) {
        if (!userService.isRegistered(userId))
            return -1;
        Group group = groupRepository.fetchGroup(groupId);
        User user = userService.provideUser(userId);
        if (group.isStarted())
            return -1;
        if (!group.hasParticipantById(user.getId()))
            return -1;
        group.deleteParticipant(user);
        user.deleteGroupAsParticipant(groupId);
        userService.updateUser(user);
        groupRepository.updateGroup(groupId, group);
        return 0;
    }

    public ResponsePreferEntity getGiftInfo(String groupId, String userId) {
        if (!userService.isRegistered(userId))
            return null;
        Group group = groupRepository.fetchGroup(groupId);
        User user = userService.provideUser(userId);
        if (!group.hasParticipantById(user.getId()))
            return null;
        if (!group.isStarted() || group.isFinished())
            return null;
        groupRepository.updateGroup(groupId, group);
        return gameService.getGiftInfo(groupId, userId);
    }

    public int presentGift(String groupId, String userId) {
        if (!userService.isRegistered(userId))
            return -1;
        Group group = groupRepository.fetchGroup(groupId);
        User user = userService.provideUser(userId);
        if (!group.hasParticipantById(user.getId()))
            return -1;
        group.presentGift(user);
        groupRepository.updateGroup(groupId, group);
        return 0;
    }

    Collection<Group> _provideAllGroups() {
        return groupRepository.getAllGroups();
    }

    public Prefer getPrefer(String userId, String groupId) {
        if (!userService.isRegistered(userId))
            return null;
        Group group = groupRepository.fetchGroup(groupId);
        User user = userService.provideUser(userId);
        if (!group.hasParticipantById(user.getId()))
            return null;
        return new Prefer(group.getPrefer(userId));
    }

    @Deprecated
    public int _startGroup(String groupId) {
        try {
            groupRepository.fetchGroup(groupId);
        } catch (NotFoundException e) {
            return -1;
        }
        groupRepository._startGroup(groupId);
        gameService.arrangeGame(groupRepository.fetchGroup(groupId));
        return 0;
    }

    @Deprecated
    public int _finishGroup(String groupId) {
        try {
            groupRepository.fetchGroup(groupId);
        } catch (NotFoundException e) {
            return -1;
        }
        groupRepository._finishGroup(groupId);
        return 0;
    }
}
