package ftc.shift.sample.repositories;

import ftc.shift.sample.models.GameInfo;
import org.springframework.stereotype.Repository;

public interface GameRepository {
    GameInfo createGame(GameInfo gameInfo);

    GameInfo fetchGame(String groupId);
}
