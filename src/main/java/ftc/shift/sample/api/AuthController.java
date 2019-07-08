package ftc.shift.sample.api;

import ftc.shift.sample.models.User;
import ftc.shift.sample.services.AuthService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final String AUTH_PATH_V1 = "/api/v1/auth";

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping(AUTH_PATH_V1 + "/{name}")
    @ApiOperation(value = "Авторизация в системе под уникальным именем")
    public ResponseEntity<User> authenticate(
            @PathVariable("name") String name
    ) {
        User result = authService.authUser(new User("", name));
        return ResponseEntity.ok(result);
    }
}
