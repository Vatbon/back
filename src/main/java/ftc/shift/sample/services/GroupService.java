package ftc.shift.sample.services;

import ftc.shift.sample.exception.NotFoundException;
import ftc.shift.sample.models.Group;
import ftc.shift.sample.models.ResponsePreferEntity;
import ftc.shift.sample.models.User;
import ftc.shift.sample.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public GroupService(GroupRepository groupRepository, UserService userService, TimeService timeService, GameService gameService) {
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
        Group result = groupRepository.createGroup(userId, group);
        result.addParticipant(userService.provideUser(userId), "");
        return result;

    }

    private int checkRules(Group group) {
        if (!timeService.isDatesValid(group))
            return -1;
        if (group.getTitle() == null)
            return -1;
        if (group.getAmountLimit() < 3)
            return -1;
        if (group.getMinValue() > group.getMaxValue())
            return -1;
        return 0;
    }

    public Group updateGroup(String userId, String groupId, Group group) {
        if (!userService.isRegistered(userId))
            return null;
        return groupRepository.updateGroup(userId, groupId, group);
    }

    public int deleteGroup(String userId, String groupId) {
        if (!userService.isRegistered(userId))
            return -1;
        if (!userId.equals(groupRepository.fetchGroup(groupId).getHost().getId()))
            return -1;
        groupRepository.deleteGroup(groupId);
        return 0;
    }

    public Collection<Group> provideUsersGroups(String userId) {
        if (!userService.isRegistered(userId))
            return null;
        return groupRepository.getUsersGroups(userId);
    }

    public int joinGroup(String groupId, String userId, String prefer) {
        if (!userService.isRegistered(userId))
            return -1;
        Group group = groupRepository.fetchGroup(groupId);
        if (group.getAmount() + 1 > group.getAmountLimit())
            return -1;
        if (group.getAllParticipants().contains(userService.provideUser(userId)))
            return -1;
        group.addParticipant(userService.provideUser(userId), prefer);
        return 0;
    }

    public int changePrefer(String groupId, String userId, String prefer) {
        if (!userService.isRegistered(userId))
            return -1;
        Group group = groupRepository.fetchGroup(groupId);
        User user = userService.provideUser(userId);
        if (!group.getAllParticipants().contains(user))
            return -1;
        group.deleteParticipant(user);
        group.addParticipant(user, prefer);
        groupRepository.updateGroup(groupId, groupId, group);
        return 0;
    }

    public int receiveGift(String groupId, String userId) {
        if (!userService.isRegistered(userId))
            return -1;
        Group group = groupRepository.fetchGroup(groupId);
        User user = userService.provideUser(userId);
        if (!group.getAllParticipants().contains(user))
            return -1;
        group.receiveGift(user);
        return 0;
    }

    Collection<Group> _provideAllGroups() {
        return groupRepository.getAllGroups();
    }

    public int leaveGroup(String groupId, String userId) {
        if (!userService.isRegistered(userId))
            return -1;
        Group group = groupRepository.fetchGroup(groupId);
        User user = userService.provideUser(userId);
        if (group.isStarted())
            return -1;
        if (!group.getAllParticipants().contains(user))
            return -1;
        group.deleteParticipant(user);
        return 0;
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

    public ResponsePreferEntity getGiftInfo(String groupId, String userId) {
        if (!userService.isRegistered(userId))
            return null;
        Group group = groupRepository.fetchGroup(groupId);
        User user = userService.provideUser(userId);
        if (!group.getAllParticipants().contains(user))
            return null;
        if (!group.isStarted() || group.isFinished())
            return null;
        return gameService.getGiftInfo(groupId, userId);
    }

    public int presentGift(String groupId, String userId) {
        if (!userService.isRegistered(userId))
            return -1;
        Group group = groupRepository.fetchGroup(groupId);
        User user = userService.provideUser(userId);
        if (!group.getAllParticipants().contains(user))
            return -1;
        group.presentGift(user);
        return 0;
    }
}
