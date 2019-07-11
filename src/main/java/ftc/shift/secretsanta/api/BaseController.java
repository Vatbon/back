package ftc.shift.secretsanta.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {
    @GetMapping("/")
    @ApiOperation(value = "Используется для проверки доступа к серверу (на данный момент)")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok().build();
    }
}
