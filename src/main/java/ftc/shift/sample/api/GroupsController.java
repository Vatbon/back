package ftc.shift.sample.api;

import ftc.shift.sample.models.Group;
import ftc.shift.sample.services.GroupService;
import ftc.shift.sample.util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class GroupsController {

    @Autowired
    private GroupService service;

    private static final String GROUPS_PATH = "/api/groups";


    @GetMapping(GROUPS_PATH)
    public ResponseEntity<Collection<Group>> listGroups(
            @RequestHeader("userId") String userId) {
        Collection<Group> groups = service.provideAllGroups(userId);
        Logger.log("GET " + GROUPS_PATH + " userId = " + userId);
        return ResponseEntity.ok(groups);
    }

    @GetMapping(GROUPS_PATH + "/{groupId}")
    public ResponseEntity<Group> getGroup(
            @RequestHeader("userId") String userId,
            @PathVariable String groupId) {
        Group result = service.provideGroup(userId, groupId);
        Logger.log("GET " + GROUPS_PATH + "/" + groupId + " userId = " + userId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(GROUPS_PATH + "/{groupId}")
    public ResponseEntity<?> deleteGroup(
            @RequestHeader("userId") String userId,
            @PathVariable String groupId) {
        service.deleteGroup(userId, groupId);
        Logger.log("DELETE " + GROUPS_PATH + "/" + groupId + " userId = " + userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(GROUPS_PATH)
    public ResponseEntity<Group> createGroup(
            @RequestHeader("userId") String userId,
            @RequestBody Group group) {
        Group result = service.createGroup(userId, group);
        Logger.log("POST " + GROUPS_PATH + " userId = " + userId + " groupName = " + group.getName());
        return ResponseEntity.ok(result);
    }

    @PatchMapping(GROUPS_PATH + "/{groupId}")
    public ResponseEntity<Group> patchGroup(
            @RequestHeader("userId") String userId,
            @RequestBody Group group) {
        Group result = service.updateGroup(userId, group);
        return ResponseEntity.ok(result);
    }
}
