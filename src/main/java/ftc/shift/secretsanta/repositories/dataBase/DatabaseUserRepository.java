package ftc.shift.secretsanta.repositories.dataBase;

import ftc.shift.secretsanta.models.Participant;
import ftc.shift.secretsanta.models.ParticipantQueryEntity;
import ftc.shift.secretsanta.models.User;
import ftc.shift.secretsanta.repositories.UserRepository;
import ftc.shift.secretsanta.repositories.dataBase.extractors.UserExtractor;
import ftc.shift.secretsanta.repositories.dataBase.extractors.UserHostsExtractor;
import ftc.shift.secretsanta.repositories.dataBase.extractors.UserParticipantsExtractor;
import ftc.shift.secretsanta.util.IdFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Repository
@ConditionalOnProperty(name = "use.database", havingValue = "true")
public class DatabaseUserRepository implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final UserExtractor userExtractor;
    private UserParticipantsExtractor userParticipantsExtractor;
    private UserHostsExtractor userHostsExtractor;

    @Autowired
    public DatabaseUserRepository(NamedParameterJdbcTemplate jdbcTemplate,
                                  UserExtractor userExtractor,
                                  UserParticipantsExtractor userParticipantsExtractor,
                                  UserHostsExtractor userHostsExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.userExtractor = userExtractor;
        this.userParticipantsExtractor = userParticipantsExtractor;
        this.userHostsExtractor = userHostsExtractor;
    }

    @PostConstruct
    public void initialize() {
        // Подразумевается, что H2 работает в in-memory режиме и таблицы необходимо создавать при каждом старте приложения
        // SQL запросы для создания таблиц

        String createUserTableSql = "create table USERS (" +
                "USER_ID  VARCHAR(64) PRIMARY KEY ," +
                "NAME     VARCHAR(64)" +
                ");";

        String createGroupPartTableSql = "create table USERS_PARTICIPANTS(" +
                "USER_ID  VARCHAR(64)," +
                "GROUP_ID VARCHAR(64)," +
                "PREFER VARCHAR(250)," +
                "RECEIVED BOOLEAN," +
                "PRESENTED BOOLEAN" +
                ");";

        String createGroupHostTableSql = "create table USERS_HOSTS(" +
                "USER_ID  VARCHAR(64)," +
                "GROUP_ID VARCHAR(64)" +
                ");";

        jdbcTemplate.update(createUserTableSql, new MapSqlParameterSource());
        jdbcTemplate.update(createGroupPartTableSql, new MapSqlParameterSource());
        jdbcTemplate.update(createGroupHostTableSql, new MapSqlParameterSource());

        // Заполним таблицы тестовыми данными
        this.createUser(new User("", "Анастасия"));
        this.createUser(new User("", "Владимир"));
        this.createUser(new User("", "Владислав"));
        this.createUser(new User("", "Данила"));
        this.createUser(new User("", "Ксения"));
        this.createUser(new User("", "Максим"));
        this.createUser(new User("", "Никита"));
    }

    @Override
    public User getUser(String userId) {
        String sqlUser = "select USER_ID, NAME " +
                "from USERS " +
                "where USER_ID=:userId;";

        String sqlParts = "select USER_ID, GROUP_ID, PRESENTED, RECEIVED, PREFER " +
                "from USERS_PARTICIPANTS " +
                "where USER_ID=:userId;";


        String sqlHosts = "select USER_ID, GROUP_ID " +
                "from USERS_HOSTS " +
                "where USER_ID=:userId;";

        /*Достаем пользователя*/
        MapSqlParameterSource paramsUser = new MapSqlParameterSource()
                .addValue("userId", userId);
        List<User> users = jdbcTemplate.query(sqlUser, paramsUser, userExtractor);

        /*Достаем все группы, где пользователь является участником*/
        MapSqlParameterSource paramsParts = new MapSqlParameterSource()
                .addValue("userId", userId);
        List<String> parts = getListOfGroupsFromParticipants(jdbcTemplate.query(sqlParts, paramsParts, userParticipantsExtractor));

        /*Достаем все группы, где пользователь является хозяином*/
        MapSqlParameterSource paramsHosts = new MapSqlParameterSource()
                .addValue("userId", userId);
        List<String> hosts = jdbcTemplate.query(sqlHosts, paramsHosts, userHostsExtractor);

        if (users.isEmpty()) {
            return null;
        }
        User user = users.get(0);

        /*Заполняем поля*/
        for (String part : parts) {
            user.addGroupAsParticipant(part);
        }
        for (String host : hosts) {
            user.addGroupAsHost(host);
        }
        return user;
    }

    private List<String> getListOfGroupsFromParticipants(List<ParticipantQueryEntity> query) {
        if (query == null)
            return null;
        List<String> result = new ArrayList<>();
        for (ParticipantQueryEntity participant : query) {
            result.add(participant.getGroupId());
        }
        return result;
    }

    @Override
    public User createUser(User user) {
        String insertBookSql = "insert into USERS (USER_ID, NAME) values (:userId, :name) ;";

        String userId = String.valueOf(IdFactory.getNewId());
        user.setId(userId);
        MapSqlParameterSource bookParams = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("name", user.getName());

        jdbcTemplate.update(insertBookSql, bookParams);

        /*Закидываем группы, где пользователь участник*/
        for (String s : user.getGroupsAsParticipant()) {
            String insertUserSql = "insert into USERS_PARTICIPANTS (USER_ID, GROUP_ID) values (:userId, :groupId)";
            bookParams = new MapSqlParameterSource()
                    .addValue("userId", userId)
                    .addValue("groupId", s);
            jdbcTemplate.update(insertUserSql, bookParams);
        }

        /*Закидываем группы, где пользователь хозяин*/
        for (String s : user.getGroupsAsHost()) {
            String insertUserSql = "insert into USERS_HOSTS (USER_ID, GROUP_ID) values (:userId, :groupId)";
            bookParams = new MapSqlParameterSource()
                    .addValue("userId", userId)
                    .addValue("groupId", s);
            jdbcTemplate.update(insertUserSql, bookParams);
        }

        return user;
    }

    @Override
    public boolean containsName(String name) {
        String sql = "select USER_ID, NAME " +
                "from USERS " +
                "where USERS.NAME=:name";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", name);

        List<User> users = jdbcTemplate.query(sql, params, userExtractor);
        System.out.println(!users.isEmpty());
        return !users.isEmpty();
    }

    @Override
    public User getUserByName(String name) {
        String sqlUser = "select USER_ID, NAME " +
                "from USERS " +
                "where NAME=:name;";

        /*Достаем пользователя*/
        MapSqlParameterSource paramsUser = new MapSqlParameterSource()
                .addValue("name", name);
        List<User> users = jdbcTemplate.query(sqlUser, paramsUser, userExtractor);

        if (users.isEmpty()) {
            return null;
        }
        User user = users.get(0);

        String userId = user.getId();

        String sqlParts = "select USER_ID, GROUP_ID, PRESENTED, RECEIVED " +
                "from USERS_PARTICIPANTS " +
                "where USER_ID=:userId;";

        String sqlHosts = "select USER_ID, GROUP_ID " +
                "from USERS_PARTICIPANTS " +
                "where USER_ID=:userId;";

        /*Достаем все группы, где пользователь является участником*/
        MapSqlParameterSource paramsParts = new MapSqlParameterSource()
                .addValue("userId", userId);
        List<String> parts = getListOfGroupsFromParticipants(jdbcTemplate.query(sqlParts, paramsParts, userParticipantsExtractor));

        /*Достаем все группы, где пользователь является хозяином*/
        MapSqlParameterSource paramsHosts = new MapSqlParameterSource()
                .addValue("userId", userId);
        List<String> hosts = jdbcTemplate.query(sqlHosts, paramsHosts, userHostsExtractor);


        /*Заполняем поля*/
        for (String part : parts) {
            user.addGroupAsParticipant(part);
        }
        for (String host : hosts) {
            user.addGroupAsHost(host);
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getName() == null)
            return null;
        if (user.getId() == null)
            return null;

        String sqlUser = "update USERS " +
                "SET NAME=:name " +
                "where USER_ID=:userId;";
        MapSqlParameterSource userParams = new MapSqlParameterSource()
                .addValue("userId", user.getId())
                .addValue("name", user.getName());
        jdbcTemplate.update(sqlUser, userParams);

        /*Обновляем группы, в которых пользователь является участником*/
        String sqlParts = "insert into USERS_PARTICIPANTS(USER_ID, GROUP_ID) values (:userId, :groupId);";

        for (String s : user.getGroupsAsParticipant()) {
            MapSqlParameterSource partsParams = new MapSqlParameterSource()
                    .addValue("userId", user.getId())
                    .addValue("groupId", s);
            jdbcTemplate.update(sqlParts, partsParams);
        }

        /*Обновляем группы, в которых пользователь является хозяином*/
        String sqlHosts = "insert into USERS_HOSTS(USER_ID, GROUP_ID) values (:userId, :groupId);";

        for (String s : user.getGroupsAsHost()) {
            MapSqlParameterSource hostsParams = new MapSqlParameterSource()
                    .addValue("userId", user.getId())
                    .addValue("groupId", s);
            jdbcTemplate.update(sqlHosts, hostsParams);
        }
        return user;
    }
}
