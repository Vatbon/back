package ftc.shift.secretsanta.repositories;

import ftc.shift.secretsanta.models.GameInfo;

public interface GameRepository {
    GameInfo createGame(GameInfo gameInfo);

    GameInfo fetchGame(String groupId);
}
