package ftc.shift.sample.services;

import ftc.shift.sample.models.Group;
import ftc.shift.sample.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Collection<Group> provideAllGroups(String userId) {
        return groupRepository.getAllGroups();
    }

    public Group provideGroup(String userId, String groupId) {
        return groupRepository.fetchGroup(groupId);
    }

    public Group createGroup(String userId, Group group) {
        return groupRepository.createGroup(userId, group);
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
}
