package ftc.shift.secretsanta.repositories;

import ftc.shift.secretsanta.models.Group;

import java.util.Collection;

public interface GroupRepository {
    Group fetchGroup(String groupId);

    Group updateGroup(String groupId, Group group);

    void deleteGroup(String groupId);

    Group createGroup(Group group);

    Collection<Group> getAllGroups();

    @Deprecated
    void _startGroup(String groupId);

    @Deprecated
    void _finishGroup(String groupId);
}
