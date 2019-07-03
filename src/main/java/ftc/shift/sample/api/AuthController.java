package ftc.shift.sample.api;

import ftc.shift.sample.models.User;
import ftc.shift.sample.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    //GET /api/auth and i get {"name":"string";} i must give back full body of User
    private final String AUTH_PATH = "/api/auth";

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(AUTH_PATH)
    public ResponseEntity<User> authenticate(
            @RequestParam("name") String name
    ) {
        User result = authService.authUser(new User("", name));
        return ResponseEntity.ok(result);
    }
}
