package ftc.shift.sample.repositories;

import ftc.shift.sample.models.GameInfo;

public interface GameRepository {
    GameInfo createGame();
    GameInfo fetchGame();
}
