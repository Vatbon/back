package ftc.shift.secretsanta.repositories.inMemory;

import ftc.shift.secretsanta.models.GameInfo;
import ftc.shift.secretsanta.repositories.GameRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryGameRepository implements GameRepository {

    private Map<String, GameInfo> GameCache = new HashMap<>();

    @Override
    public GameInfo createGame(GameInfo gameInfo) {
        GameCache.put(gameInfo.getGroup().getId(), gameInfo);
        return gameInfo;
    }

    @Override
    public GameInfo fetchGame(String groupId) {
        return GameCache.get(groupId);
    }

    @Override
    public GameInfo updateGame(GameInfo gameInfo) {
        /*Заглушка*/
        return GameCache.get(gameInfo.getGroup().getId());
    }
}
