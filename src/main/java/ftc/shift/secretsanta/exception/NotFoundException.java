package ftc.shift.secretsanta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Если при обработке пользовательского запроса будет выброшено это исключение,
 * то пользователю вернётся ответ со статусом 404 (NOT_FOUND)
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
}
