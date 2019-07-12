package ftc.shift.secretsanta.repositories;

import ftc.shift.secretsanta.models.User;
import ftc.shift.secretsanta.util.IdFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

@Repository
@ConditionalOnProperty(name = "use.database", havingValue = "true")
public class DatabaseUserRepository implements UserRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private UserExtractor userExtractor;

    @PostConstruct
    public void initialize() {
        // Подразумевается, что H2 работает в in-memory режиме и таблицы необходимо создавать при каждом старте приложения
        // SQL запросы для создания таблиц

        String createUserTableSql = "create table USERS (" +
                "USER_ID  VARCHAR(64)," +
                "NAME     VARCHAR(64)" +
                ");";

        String createGroupPartTableSql = "create table USERS_PART(" +
                "USER_ID  VARCHAR(64)," +
                "GROUP_ID VARCHAR(64)" +
                ");";

        String createGroupHostTableSql = "create table USERS_HOST(" +
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
        String sql = "select USER_ID, NAME, PART, HOST " +
                "from USERS, USERS_PART, USERS_HOST " +
                "where USER_ID=:userId and USERS_PART.USER_ID=:userId and USERS_HOST.USER_ID=:userId;";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);

        List<User> users = jdbcTemplate.query(sql, params, userExtractor);

        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
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
        for (String s : user.getGroupsAsParticipant()) {
            String insertUserSql = "insert into USERS_PART (USER_ID, GROUP_ID) values (:userId, :groupId)";
            bookParams = new MapSqlParameterSource()
                    .addValue("userId", userId)
                    .addValue("groupId", s);
            jdbcTemplate.update(insertUserSql, bookParams);
        }


        for (String s : user.getGroupsAsHost()) {
            String insertUserSql = "insert into USERS_HOST (USER_ID, GROUP_ID) values (:userId, :groupId)";
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
        String sql = "select USER_ID, NAME, PART, HOST " +
                "from USERS, USERS_PART, USERS_HOST " +
                "where NAME=:name and USERS_PART.USER_ID=USERS.USER_ID and USERS_HOST=USERS.USER_ID";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", name);

        List<User> users = jdbcTemplate.query(sql, params, userExtractor);

        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }
}
