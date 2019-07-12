package ftc.shift.secretsanta.api;

import ftc.shift.secretsanta.models.ApiCreationGroupEntity;
import ftc.shift.secretsanta.models.Group;
import ftc.shift.secretsanta.models.Prefer;
import ftc.shift.secretsanta.models.ResponsePreferEntity;
import ftc.shift.secretsanta.services.GroupService;
import ftc.shift.secretsanta.util.Logger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Все аннотации, начинающиеся с @Api нужны только для построения <a href="http://localhost:8081/swagger-ui.html#/">swagger-документации</a>
 */
@RestController
@Api(description = "API для работы с группами")
public class GroupsController {

    @Autowired
    private GroupService service;

    private static final String GROUPS_PATH_V1 = "/api/v1/groups";
    private static final String GROUPS_PATH_V2 = "/api/v2/groups";


    @GetMapping(GROUPS_PATH_V1)
    @ApiOperation(value = "Показать все группы (Коллекция allParticipants пустая)")
    public ResponseEntity<Collection<Group>> listGroups(
            @ApiParam(value = "Уникальный идентификатор пользателя")
            @RequestHeader("userId") String userId) {
        Collection<Group> result = service.provideAllGroups(userId);
        Logger.log("GET " + GROUPS_PATH_V1 + " userId = " + userId);
        if (result == null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(result);
    }

    @GetMapping(GROUPS_PATH_V1 + "/{groupId}")
    @ApiOperation(value = "Получить полное тело группы по groupId")
    public ResponseEntity<Group> getGroup(
            @ApiParam(value = "Уникальный идентификатор пользателя")
            @RequestHeader("userId") String userId,
            @ApiParam(value = "Уникальный идентификатор группы")
            @PathVariable("groupId") String groupId) {
        Group result = service.provideGroup(userId, groupId);
        Logger.log("GET " + GROUPS_PATH_V1 + "/" + groupId + " userId = " + userId);
        if (result == null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(result);
    }

    @DeleteMapping(GROUPS_PATH_V1 + "/{groupId}")
    @ApiOperation(value = "Удаление существующей группы")
    public ResponseEntity<?> deleteGroup(
            @ApiParam(value = "Уникальный идентификатор пользателя")
            @RequestHeader("userId") String userId,
            @ApiParam(value = "Уникальный идентификатор группы")
            @PathVariable("groupId") String groupId) {
        int result = service.deleteGroup(userId, groupId);
        Logger.log("DELETE " + GROUPS_PATH_V1 + "/" + groupId + " userId = " + userId);
        if (result == -1)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @PostMapping(GROUPS_PATH_V1)
    @ApiOperation(value = "Создание новой группы")
    public ResponseEntity<Group> createGroup(
            @ApiParam(value = "Уникальный идентификатор пользателя")
            @RequestHeader("userId") String userId,
            @ApiParam(value = "Тело ApiCreationGroupEntity запроса")
            @RequestBody Group group) {
        Group result = service.createGroup(userId, group);
        Logger.log("POST " + GROUPS_PATH_V1 + " userId = " + userId + " groupName = " + group.getTitle());
        if (result == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(group);
    }

    @PostMapping(GROUPS_PATH_V2)
    @ApiOperation(value = "Создание новой группы с использованием ApiCreationGroupEntity")
    public ResponseEntity<ApiCreationGroupEntity> createGroup(
            @ApiParam(value = "Уникальный идентификатор пользателя")
            @RequestHeader("userId") String userId,
            @ApiParam(value = "Тело ApiCreationGroupEntity запроса")
            @RequestBody ApiCreationGroupEntity creationGroupEntity) {
        Group result = service.createGroup(userId, creationGroupEntity.getGroup());
        service.changePrefer(result.getId(), userId, creationGroupEntity.getPrefer().getPrefer());
        Logger.log("POST " + GROUPS_PATH_V2 + " userId = " + userId + " groupName = " + creationGroupEntity.getGroup().getTitle());
        if (result == null)
            return ResponseEntity.badRequest().build();
        creationGroupEntity.setGroup(result);
        return ResponseEntity.ok(creationGroupEntity);
    }

    @PutMapping(GROUPS_PATH_V1 + "/{groupId}")
    @ApiOperation(value = "Изменение существующей группы")
    public ResponseEntity<Group> patchGroup(
            @ApiParam(value = "Уникальный идентификатор пользателя")
            @RequestHeader("userId") String userId,
            @ApiParam(value = "Уникальный идентификатор группы")
            @PathVariable("groupId") String groupId,
            @RequestBody Group group) {
        Group result = service.updateGroup(userId, groupId, group);
        Logger.log("PUT " + GROUPS_PATH_V1 + " userId = " + userId + " groupId = " + groupId);
        if (result == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(result);
    }

    @PostMapping(GROUPS_PATH_V1 + "/{groupId}/join")
    @ApiOperation(value = "Присоединение к существующей группе как участник")
    public ResponseEntity<?> joinGroup(
            @ApiParam(value = "Уникальный идентификатор пользателя")
            @RequestHeader("userId") String userId,
            @ApiParam(value = "Уникальный идентификатор группы")
            @PathVariable("groupId") String groupId,
            @ApiParam(value = "Тело \"желания\" пользователя")
            @RequestBody Prefer prefer) {
        Logger.log("POST " + GROUPS_PATH_V1 + "/" + groupId + "/join" + " userId = " + userId);
        int result = service.joinGroup(groupId, userId, prefer.getPrefer());
        if (result == -1)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @PostMapping(GROUPS_PATH_V1 + "/{groupId}/leave")
    @ApiOperation(value = "Покинуть существующую группу")
    public ResponseEntity<?> leaveGroup(
            @ApiParam(value = "Уникальный идентификатор пользателя")
            @RequestHeader("userId") String userId,
            @ApiParam(value = "Уникальный идентификатор группы")
            @PathVariable("groupId") String groupId
    ) {
        int result = service.leaveGroup(groupId, userId);
        if (result == -1)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @PutMapping(GROUPS_PATH_V1 + "/{groupId}/prefer")
    @ApiOperation(value = "Изменение желания пользователя в группе, в которой он является участником")
    public ResponseEntity<?> changePrefer(
            @ApiParam(value = "Уникальный идентификатор пользателя")
            @RequestHeader("userId") String userId,
            @ApiParam(value = "Уникальный идентификатор группы")
            @PathVariable("groupId") String groupId,
            @ApiParam(value = "Тело \"желания\" пользователя")
            @RequestBody Prefer prefer
    ) {
        int result = service.changePrefer(groupId, userId, prefer.getPrefer());
        Logger.log("PUT " + GROUPS_PATH_V1 + "/" + groupId + "/prefer" + " userId = " + userId);
        if (result == -1)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @PutMapping(GROUPS_PATH_V1 + "/{groupId}/gift/receive")
    @ApiOperation(value = "Подтверждение принятия подарка участником группы")
    public ResponseEntity<?> receiveGift(
            @ApiParam(value = "Уникальный идентификатор пользателя")
            @RequestHeader("userId") String userId,
            @ApiParam(value = "Уникальный идентификатор группы")
            @PathVariable("groupId") String groupId) {
        int result = service.receiveGift(groupId, userId);
        if (result == -1)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @PutMapping(GROUPS_PATH_V1 + "/{groupId}/gift/presented")
    @ApiOperation(value = "Подвердить дарение подарка своему подопечному")
    public ResponseEntity<?> presentGift(
            @ApiParam(value = "Уникальный идентификатор пользателя")
            @RequestHeader("userId") String userId,
            @ApiParam(value = "Уникальный идентификатор группы")
            @PathVariable("groupId") String groupId
    ) {
        int result = service.presentGift(groupId, userId);
        if (result == -1)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @GetMapping(GROUPS_PATH_V1 + "/{groupId}/gift")
    @ApiOperation(value = "Получение информации о подарке после начала игры")
    public ResponseEntity<ResponsePreferEntity> sendGiftInfo(
            @ApiParam(value = "Уникальный идентификатор пользателя")
            @RequestHeader("userId") String userId,
            @ApiParam(value = "Уникальный идентификатор группы")
            @PathVariable("groupId") String groupId) {
        ResponsePreferEntity result = service.getGiftInfo(groupId, userId);
        if (result == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(result);
    }

    @Deprecated
    @PutMapping(GROUPS_PATH_V1 + "/{groupId}/start")
    @ApiOperation(value = "Принудительное начало события !Использовать только для тестирования!")
    public ResponseEntity<?> startGroup(
            @ApiParam(value = "Уникальный идентификатор группы")
            @PathVariable("groupId") String groupId
    ) {
        int result = service._startGroup(groupId);
        if (result == -1)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @Deprecated
    @PutMapping(GROUPS_PATH_V1 + "/{groupId}/finish")
    @ApiOperation(value = "Принудительное завершение события !Использовать только для тестирования!")
    public ResponseEntity<?> finishGroup(
            @ApiParam(value = "Уникальный идентификатор группы")
            @PathVariable("groupId") String groupId
    ) {
        int result = service._finishGroup(groupId);
        if (result == -1)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }
}
