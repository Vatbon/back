package ftc.shift.sample.api;

import ftc.shift.sample.models.Group;
import ftc.shift.sample.models.Prefer;
import ftc.shift.sample.models.ResponsePreferEntity;
import ftc.shift.sample.models.User;
import ftc.shift.sample.services.GroupService;
import ftc.shift.sample.util.Logger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

//feign

@RestController
public class GroupsController {

    @Autowired
    private GroupService service;

    private static final String GROUPS_PATH_V1 = "/api/v1/groups";


    @GetMapping(GROUPS_PATH_V1)
    public ResponseEntity<Collection<Group>> listGroups(
            @RequestHeader("userId") String userId) {
        Collection<Group> result = service.provideAllGroups(userId);
        Logger.log("GET " + GROUPS_PATH_V1 + " userId = " + userId);
        if (result == null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(result);
    }

    @GetMapping(GROUPS_PATH_V1 + "/{groupId}")
    public ResponseEntity<Group> getGroup(
            @RequestHeader("userId") String userId,
            @PathVariable String groupId) {
        Group result = service.provideGroup(userId, groupId);
        Logger.log("GET " + GROUPS_PATH_V1 + "/" + groupId + " userId = " + userId);
        if (result == null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(result);
    }

    @DeleteMapping(GROUPS_PATH_V1 + "/{groupId}")
    public ResponseEntity<?> deleteGroup(
            @RequestHeader("userId") String userId,
            @PathVariable String groupId) {
        int result = service.deleteGroup(userId, groupId);
        Logger.log("DELETE " + GROUPS_PATH_V1 + "/" + groupId + " userId = " + userId);
        if (result == -1)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @PostMapping(GROUPS_PATH_V1)
    public ResponseEntity<Group> createGroup(
            @RequestHeader("userId") String userId,
            @RequestBody Group group) {
        Group result = service.createGroup(userId, group);
        Logger.log("POST " + GROUPS_PATH_V1 + " userId = " + userId + " groupName = " + group.getTitle());
        if (result == null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(result);
    }

    @PutMapping(GROUPS_PATH_V1 + "/{groupId}")
    public ResponseEntity<Group> patchGroup(
            @RequestHeader("userId") String userId,
            @PathVariable("groupId") String groupId,
            @RequestBody Group group) {
        Group result = service.updateGroup(userId, groupId, group);
        Logger.log("PUT " + GROUPS_PATH_V1 + " userId = " + userId + " groupId = " + groupId);
        if (result == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(result);
    }

    @PostMapping(GROUPS_PATH_V1 + "/{groupId}/join")
    public ResponseEntity<?> joinGroup(
            @RequestHeader("userId") String userId,
            @PathVariable("groupId") String groupId,
            @RequestBody Prefer prefer) {
        Logger.log("POST " + GROUPS_PATH_V1 + "/" + groupId + "/join" + " userId = " + userId);
        int result = service.joinGroup(groupId, userId, prefer.getPrefer());
        if (result == -1)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @PutMapping(GROUPS_PATH_V1 + "/{groupId}/prefer")
    public ResponseEntity<?> changePrefer(
            @PathVariable("groupId") String groupId,
            @RequestHeader("userId") String userId,
            @RequestBody Prefer prefer
    ) {
        int result = service.changePrefer(groupId, userId, prefer.getPrefer());
        Logger.log("PUT " + GROUPS_PATH_V1 + "/" + groupId + "/prefer" + " userId = " + userId);
        if (result == -1)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @PutMapping(GROUPS_PATH_V1 + "/{groupId}/gift/receive")
    public ResponseEntity<?> receiveGift(
            @PathVariable("groupId") String groupId,
            @RequestHeader("userId") String userId) {
        int result = service.receiveGift(groupId, userId);
        if (result == -1)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @GetMapping(GROUPS_PATH_V1 + "/{groupId}/gift")
    public ResponseEntity<ResponsePreferEntity> sendGiftInfo(
            @PathVariable("groupId") String groupId,
            @RequestHeader("userId") String userId) {
        //service.getGiftInfo(groupId, userId);
        if (0 == -1)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }
}
