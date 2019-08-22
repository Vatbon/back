package ftc.shift.secretsanta.api;

import ftc.shift.secretsanta.models.User;
import ftc.shift.secretsanta.services.UserService;
import ftc.shift.secretsanta.util.Logger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.Console;

@RestController
@Api(description = "API для получения пользователя с расширенной информацией")
public class UserController {

    private static final String USER_PATH_V2 = "/api/v2/users";

    @Autowired
    private UserService service;

    @GetMapping(USER_PATH_V2)
    @ApiOperation(value = "Получить тело пользователя со всеми группами, где он является участником или хозяином")
    public ResponseEntity<User> getUser(
            @ApiParam(value = "Уникальный идентификатор пользателя")
            @RequestHeader("userId") String userId) {
        Logger.log(Logger.BLUE_BOLD + "GET " + Logger.RESET + USER_PATH_V2 + " userId = " + userId);
        User result = service.provideUser(userId);
        if (result == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(result);
    }
}
