package ftc.shift.sample.repositories;

import ftc.shift.sample.exception.NotFoundException;
import ftc.shift.sample.models.Group;
import ftc.shift.sample.models.User;
import ftc.shift.sample.services.UserService;
import ftc.shift.sample.util.IdFactory;
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
    public Group updateGroup(String userId, String groupId, Group group) {
        if (!groupCache.containsKey(groupId)) {
            throw new NotFoundException();
        }
        Group oldGroup = null;
        if (groupCache.get(groupId).getHost().getId().equals(userId)) {
            oldGroup = groupCache.get(groupId);
            oldGroup.setTitle(group.getTitle());
        }
        return oldGroup;
    }

    @Override
    public void deleteGroup(String groupId) {
        //TODO: decide what to actually do with deletion of groups
    }

    @Override
    public Group createGroup(String userId, Group group) {
        group.setId(String.valueOf(IdFactory.getNewId()));
        group.setHost(userService.provideUser(userId));
        group.setStarted(false);
        group.setFinished(false);
        group.getAllParticipants().clear();
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

        for (Group group : groups) {
            if (group.getAllParticipants().contains(user) || group.getHost().equals(user))
                result.add(group);
        }

        return result;
    }

    public void saveState() {
    }
}
