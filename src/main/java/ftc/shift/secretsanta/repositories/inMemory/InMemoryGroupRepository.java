package ftc.shift.secretsanta.repositories.inMemory;

import ftc.shift.secretsanta.exception.NotFoundException;
import ftc.shift.secretsanta.models.Group;
import ftc.shift.secretsanta.models.User;
import ftc.shift.secretsanta.repositories.GroupRepository;
import ftc.shift.secretsanta.services.UserService;
import ftc.shift.secretsanta.util.IdFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryGroupRepository implements GroupRepository {

    private Map<String, Group> groupCache = new HashMap<>();
    private final UserService userService;

    @Autowired
    public InMemoryGroupRepository(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Group fetchGroup(String groupId) {
        if (!groupCache.containsKey(groupId)) {
            throw new NotFoundException();
        }

        return groupCache.get(groupId);
    }

    @Override
    public Group updateGroup(String groupId, Group group) {
        if (!groupCache.containsKey(groupId)) {
            throw new NotFoundException();
        }
        Group oldGroup = null;
        oldGroup = groupCache.get(groupId);
        oldGroup.setTitle(group.getTitle());
        return oldGroup;
    }

    @Override
    public void deleteGroup(String groupId) {
        //TODO: decide what to actually do with deletion of groups
        if (!groupCache.containsKey(groupId)) {
            throw new NotFoundException();
        }
        groupCache.remove(groupId);
    }

    @Override
    public Group createGroup(Group group) {
        group.setId(String.valueOf(IdFactory.getNewId()));
        groupCache.put(group.getId(), group);
        return group;
    }

    @Override
    public Collection<Group> getAllGroups() {
        return groupCache.values();
    }

    @Override
    public Collection<Group> getUsersGroups(String userId) {
        Collection<Group> groups = groupCache.values();
        Collection<Group> result = new ArrayList<>();
        User user = userService.provideUser(userId);

        synchronized (groups) {
            for (Group group : groups) {
                if (group.getAllParticipants().contains(user) || group.getHost().equals(user))
                    result.add(group);
            }
        }
        return result;
    }

    @Override
    public void _startGroup(String groupId) {
        if (!groupCache.containsKey(groupId)) {
            throw new NotFoundException();
        }
        groupCache.get(groupId).setStarted(true);
    }

    @Override
    public void _finishGroup(String groupId) {
        if (!groupCache.containsKey(groupId)) {
            throw new NotFoundException();
        }
        groupCache.get(groupId).setFinished(true);
    }
}
