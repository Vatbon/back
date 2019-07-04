package ftc.shift.sample.api;

import ftc.shift.sample.models.Group;
import ftc.shift.sample.models.User;
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

    private static final String GROUPS_PATH_V1 = "/api/v1/groups";


    @GetMapping(GROUPS_PATH_V1)
    public ResponseEntity<Collection<Group>> listGroups(
            @RequestHeader("userId") String userId) {
        Collection<Group> groups = service.provideAllGroups(userId);
        Logger.log("GET " + GROUPS_PATH_V1 + " userId = " + userId);
        return ResponseEntity.ok(groups);
    }

    @GetMapping(GROUPS_PATH_V1 + "/{groupId}")
    public ResponseEntity<Group> getGroup(
            @RequestHeader("userId") String userId,
            @PathVariable String groupId) {
        Group result = service.provideGroup(userId, groupId);
        Logger.log("GET " + GROUPS_PATH_V1 + "/" + groupId + " userId = " + userId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(GROUPS_PATH_V1 + "/{groupId}")
    public ResponseEntity<?> deleteGroup(
            @RequestHeader("userId") String userId,
            @PathVariable String groupId) {
        service.deleteGroup(userId, groupId);
        Logger.log("DELETE " + GROUPS_PATH_V1 + "/" + groupId + " userId = " + userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(GROUPS_PATH_V1)
    public ResponseEntity<Group> createGroup(
            @RequestHeader("userId") String userId,
            @RequestBody Group group) {
        Group result = service.createGroup(userId, group);
        Logger.log("POST " + GROUPS_PATH_V1 + " userId = " + userId + " groupName = " + group.getTitle());
        return ResponseEntity.ok(result);
    }

    @PatchMapping(GROUPS_PATH_V1 + "/{groupId}")
    public ResponseEntity<Group> patchGroup(
            @RequestHeader("userId") String userId,
            @RequestBody Group group) {
        Group result = service.updateGroup(userId, group);
        return ResponseEntity.ok(result);
    }

    @PostMapping(GROUPS_PATH_V1 + "/{groupId}/join")
    public ResponseEntity<?> joinGroup(
            @RequestHeader("userId") String userId,
            @PathVariable("groupId") String groupId,
            @RequestBody Prefer prefer) {
        service.joinGroup(groupId, userId, prefer.prefer);
        return ResponseEntity.ok().build();
    }

    @PutMapping(GROUPS_PATH_V1 + "/{groupId}/prefer")
    public ResponseEntity<?> changePrefer(
            @PathVariable("groupId") String groupId,
            @RequestHeader("userId") String userId,
            @RequestBody Prefer prefer
    ) {
        service.changePrefer(groupId, userId, prefer.prefer);
        return ResponseEntity.ok().build();
    }

    @PutMapping(GROUPS_PATH_V1 + "/{groupId}/gift/receive")
    public ResponseEntity<?> receiveGift(
            @PathVariable("groupId") String groupId,
            @RequestHeader("userId") String userId) {
        service.receiveGift(groupId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(GROUPS_PATH_V1 + "/{groupId}/gift")
    public ResponseEntity<ResponsePreferEntity> sendGiftInfo(
            @PathVariable("groupId") String groupId,
            @RequestHeader("userId") String userId) {
        //service.getGiftInfo(groupId, userId);
        return ResponseEntity.ok().build();
    }

    private class Prefer {
        String prefer;
    }

    private class ResponsePreferEntity {
        User user;
        String prefer;
        boolean received;
    }
}
