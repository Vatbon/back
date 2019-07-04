package ftc.shift.sample.services;

import ftc.shift.sample.models.Group;
import ftc.shift.sample.models.User;
import ftc.shift.sample.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserService userService;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserService userService) {
        this.groupRepository = groupRepository;
        this.userService = userService;
    }

    public Collection<Group> provideAllGroups(String userId) {
        Collection<Group> result = groupRepository.getAllGroups();
        for (Group group : result) {
            group.getAllParticipants().clear();
        }
        return result;
    }

    public Group provideGroup(String userId, String groupId) {
        return groupRepository.fetchGroup(groupId);
    }

    public Group createGroup(String userId, Group group) {
        Group result = groupRepository.createGroup(userId, group);
        result.addParticipant(userService.provideUser(userId), "");
        return result;
    }

    public Group updateGroup(String userId, Group group) {
        return groupRepository.updateGroup(userId, group);
    }

    public void deleteGroup(String userId, String groupId) {
        groupRepository.deleteGroup(groupId);
    }

    public Collection<Group> provideUsersGroups(String userId) {
        return groupRepository.getUsersGroups(userId);
    }

    public void joinGroup(String groupId, String userId, String prefer) {
        groupRepository.fetchGroup(groupId).addParticipant(userService.provideUser(userId), prefer);
    }

    public void changePrefer(String groupId, String userId, String prefer) {
        Group group = groupRepository.fetchGroup(groupId);
        User user = userService.provideUser(userId);
        group.deleteParticipant(user);
        group.addParticipant(user, prefer);
        groupRepository.updateGroup(groupId, group);
    }

    public void receiveGift(String groupId, String userId) {
        groupRepository.fetchGroup(groupId).deleteParticipant(userService.provideUser(userId));
    }
}
