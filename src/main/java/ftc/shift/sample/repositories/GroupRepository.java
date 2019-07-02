package ftc.shift.sample.repositories;

import ftc.shift.sample.models.Group;

import java.util.Collection;

public interface GroupRepository {

    Group fetchGroup(String groupId);

    Group updateGroup(String groupId, Group group);

    void deleteGroup(String groupId);

    Group createGroup(String userId, Group group);

    Collection<Group> getAllGroups();

    Collection<Group> getUsersGroups(String userId);
}
