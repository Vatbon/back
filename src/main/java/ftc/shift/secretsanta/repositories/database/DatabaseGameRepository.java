package ftc.shift.secretsanta.repositories.database;

import ftc.shift.secretsanta.models.GameInfo;
import ftc.shift.secretsanta.models.GameLinksQueryEntity;
import ftc.shift.secretsanta.models.Group;
import ftc.shift.secretsanta.models.User;
import ftc.shift.secretsanta.repositories.GameRepository;
import ftc.shift.secretsanta.repositories.GroupRepository;
import ftc.shift.secretsanta.repositories.database.extractors.GameLinksExtractor;
import ftc.shift.secretsanta.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DatabaseGameRepository implements GameRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final GameLinksExtractor gameLinksExtractor;
    private final GroupRepository groupRepository;
    private final UserService userService;


    @Autowired
    public DatabaseGameRepository(NamedParameterJdbcTemplate jdbcTemplate, GameLinksExtractor gameLinksExtractor, @Qualifier("dataBaseGroupRepository") GroupRepository groupRepository, UserService userService) {
        this.jdbcTemplate = jdbcTemplate;
        this.gameLinksExtractor = gameLinksExtractor;
        this.groupRepository = groupRepository;
        this.userService = userService;
    }

    @PostConstruct
    private void initialize() {
        String sqlGame = "create table GAME_LINKS (" +
                "GROUP_ID VARCHAR(64)," +
                "USER_ID_FROM VARCHAR(64)," +
                "USER_ID_TO VARCHAR(64)" +
                ");";

        jdbcTemplate.update(sqlGame, new MapSqlParameterSource());
    }

    @Override
    public GameInfo createGame(GameInfo gameInfo) {
        String sqlLink = "insert into GAME_LINKS values(:groupId, :userIdFrom, :userIdTo);";

        for (Map.Entry<User, User> entry : gameInfo.getLinks().entrySet()) {
            MapSqlParameterSource linkParams = new MapSqlParameterSource()
                    .addValue("groupId", gameInfo.getGroup().getId())
                    .addValue("userIdFrom", entry.getKey().getId())
                    .addValue("userIdTo", entry.getValue().getId());

            jdbcTemplate.update(sqlLink, linkParams);
        }
        return gameInfo;
    }

    @Override
    public GameInfo fetchGame(String groupId) {
        Group group = groupRepository.fetchGroup(groupId);

        GameInfo gameInfo = new GameInfo();
        gameInfo.setGroup(group);

        String sqlLink = "select * " +
                "from GAME_LINKS " +
                "where GROUP_ID=:groupId";
        MapSqlParameterSource linkParam = new MapSqlParameterSource()
                .addValue("groupId", groupId);
        List<GameLinksQueryEntity> gameLinks = jdbcTemplate.query(sqlLink, linkParam, gameLinksExtractor);

        if (gameLinks.isEmpty())
            return null;

        Map<User, User> links = new HashMap<>();
        for (GameLinksQueryEntity gameLink : gameLinks) {
            links.put(userService.provideUser(gameLink.getUserIdFrom()),
                    userService.provideUser(gameLink.getUserIdTo()));
        }
        gameInfo.setLinks(links);

        return gameInfo;
    }
}
