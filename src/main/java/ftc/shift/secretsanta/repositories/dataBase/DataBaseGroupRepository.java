package ftc.shift.secretsanta.repositories.dataBase;

import ftc.shift.secretsanta.models.Group;
import ftc.shift.secretsanta.models.Participant;
import ftc.shift.secretsanta.models.ParticipantQueryEntity;
import ftc.shift.secretsanta.models.User;
import ftc.shift.secretsanta.repositories.GroupRepository;
import ftc.shift.secretsanta.repositories.dataBase.extractors.GroupExtractor;
import ftc.shift.secretsanta.repositories.dataBase.extractors.UserExtractor;
import ftc.shift.secretsanta.repositories.dataBase.extractors.UserParticipantsExtractor;
import ftc.shift.secretsanta.util.IdFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DataBaseGroupRepository implements GroupRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final GroupExtractor groupExtractor;
    private final UserExtractor userExtractor;
    private final UserParticipantsExtractor userParticipantsExtractor;

    @Autowired
    public DataBaseGroupRepository(NamedParameterJdbcTemplate jdbcTemplate,
                                   GroupExtractor groupExtractor,
                                   UserExtractor userExtractor,
                                   UserParticipantsExtractor userParticipantsExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.groupExtractor = groupExtractor;
        this.userExtractor = userExtractor;
        this.userParticipantsExtractor = userParticipantsExtractor;
    }

    @PostConstruct
    public void initialize() {
        String sqlGroup = "create table GROUPS (" +
                "GROUP_ID     VARCHAR(64) PRIMARY KEY," +
                "TITLE        VARCHAR(64)," +
                "START_TIME   VARCHAR(12)," +
                "END_TIME     VARCHAR(12)," +
                "STARTED      BOOLEAN," +
                "FINISHED     BOOLEAN," +
                "AMOUNT_LIMIT INT," +
                "AMOUNT       INT," +
                "MIN_VALUE    INT," +
                "MAX_VALUE    INT," +
                "METHOD       VARCHAR(300)," +
                "HOST_ID      VARCHAR(64)" +
                ");";

        jdbcTemplate.update(sqlGroup, new MapSqlParameterSource());
    }

    @Override
    public Group fetchGroup(String groupId) {
        String sqlGroup = "select * " +
                "from GROUPS " +
                "where GROUP_ID=:groupId;";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("groupId", groupId);

        List<Group> groups = jdbcTemplate.query(sqlGroup, params, groupExtractor);
        if (groups.isEmpty())
            return null;
        Group group = groups.get(0);
        /*Достаем тело хозяина группы*/
        String sqlHost = "select USER_ID, NAME " +
                "from USERS" +
                "where USER_ID=:userId;";

        MapSqlParameterSource hostParams = new MapSqlParameterSource()
                .addValue("userId", group.getHost().getId());

        List<User> users = jdbcTemplate.query(sqlHost, hostParams, userExtractor);

        if (users.isEmpty())
            return null;

        User host = users.get(0);
        group.setHost(host);

        /*Достаем тело всех участников группы*/
        group.getAllParticipants().clear();

        String sqlParts = "select * " +
                "from USERS_PARTICIPANTS" +
                "where GROUP_ID=:groupId;";

        MapSqlParameterSource partsParams = new MapSqlParameterSource()
                .addValue("groupId", groupId);

        List<ParticipantQueryEntity> participants = jdbcTemplate.query(sqlParts, partsParams, userParticipantsExtractor);

        if (participants.isEmpty())
            return null;

        /*Заполняем поле user в каждом participant*/
        String sqlUsers = "select USERS.USER_ID as USER_ID, USERS.NAME as NAME " +
                "from USERS, USERS_PARTICIPANTS " +
                "where USERS.USER_ID=USERS_PARTICIPANTS.USER_ID and USERS_PARTICIPANTS.GROUP_ID=:groupId;";

        MapSqlParameterSource userParam = new MapSqlParameterSource()
                .addValue("groupId", groupId);

        List<User> userList = jdbcTemplate.query(sqlUsers, userParam, userExtractor);
        Map<String, User> userMap = new HashMap<>();
        if (userList == null)
            throw new AssertionError();
        for (User user : userList) {
            userMap.put(user.getId(), user);
        }

        for (Participant participant : participants) {
            participant.setUser(userMap.get(participant.getUser().getId()));
            group.addParticipant(participant);
        }
        return group;
    }

    @Override
    public Group updateGroup(String groupId, Group group) {
        return null;
    }

    @Override
    public void deleteGroup(String groupId) {
        String sqlGroup = "delete from GROUPS " +
                "where GROUP_ID=:groupId;";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("groupId", groupId);
        jdbcTemplate.update(sqlGroup, params);
    }

    @Override
    public Group createGroup(Group group) {
        group.setId(String.valueOf(IdFactory.getNewId()));

        String sqlGroup = "insert into GROUPS " +
                "values(:groupId, :title, :startTime, :endTime, :started, :finished, :amountLimit, " +
                ":amount, ;minValue, :maxValue, :method, :hostId)";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("groupId", group.getId())
                .addValue("title", group.getTitle())
                .addValue("startTime", group.getStartTime())
                .addValue("endTime", group.getEndTime())
                .addValue("started", group.isStarted())
                .addValue("finished", group.isFinished())
                .addValue("amountLimit", group.getAmountLimit())
                .addValue("amount", group.getAmount())
                .addValue("minValue", group.getMinValue())
                .addValue("maxValue", group.getMaxValue())
                .addValue("method", group.getMethod())
                .addValue("hostId", group.getHost().getId());

        jdbcTemplate.update(sqlGroup, param);

        /*Закидываем нового хозяина в таблицу хозяинов*/

        String sqlHost = "insert into USERS_HOSTS " +
                "values(:userId, :groupId);";

        MapSqlParameterSource param1 = new MapSqlParameterSource()
                .addValue("userId", group.getHost().getId())
                .addValue("groupId", group.getId());

        jdbcTemplate.update(sqlHost, param1);
        /*Закидываем нового участника в таблицу участников*/

        String sqlPart = "insert into USERS_PARTICIPANTS(USER_ID, GROUP_ID) " +
                "values(:userId, :groupId);";

        MapSqlParameterSource param2 = new MapSqlParameterSource()
                .addValue("userId", group.getHost().getId())
                .addValue("groupId", group.getId());

        jdbcTemplate.update(sqlPart, param2);

        return group;
    }

    @Override
    public Collection<Group> getAllGroups() {

        String sqlGroup = "select * " +
                "from GROUPS ";

        List<Group> groups = jdbcTemplate.query(sqlGroup, new MapSqlParameterSource(), groupExtractor);
        if (groups.isEmpty())
            return null;
        for (Group group : groups) {
            User host;
            String groupId = group.getId();
            /*Достаем тело хозяина группы*/
            String sqlHost = "select USER_ID, NAME " +
                    "from USERS" +
                    "where USER_ID=:userId;";

            MapSqlParameterSource hostParams = new MapSqlParameterSource()
                    .addValue("userId", group.getHost().getId());

            List<User> users = jdbcTemplate.query(sqlHost, hostParams, userExtractor);

            if (users.isEmpty())
                return null;

            /*Достаем тело всех участников группы*/
            group.getAllParticipants().clear();

            String sqlParts = "select * " +
                    "from USERS_PARTICIPANTS" +
                    "where GROUP_ID=:groupId;";

            MapSqlParameterSource partsParams = new MapSqlParameterSource()
                    .addValue("groupId", groupId);

            List<ParticipantQueryEntity> participants = jdbcTemplate.query(sqlParts, partsParams, userParticipantsExtractor);

            if (participants.isEmpty())
                return null;

            /*Заполняем поле user в каждом participant*/
            String sqlUsers = "select USERS.USER_ID as USER_ID, USERS.NAME as NAME " +
                    "from USERS, USERS_PARTICIPANTS " +
                    "where USERS.USER_ID=USERS_PARTICIPANTS.USER_ID and USERS_PARTICIPANTS.GROUP_ID=:groupId;";

            MapSqlParameterSource userParam = new MapSqlParameterSource()
                    .addValue("groupId", groupId);

            List<User> userList = jdbcTemplate.query(sqlUsers, userParam, userExtractor);
            Map<String, User> userMap = new HashMap<>();
            if (userList == null)
                throw new AssertionError();
            for (User user : userList) {
                userMap.put(user.getId(), user);
            }

            for (Participant participant : participants) {
                participant.setUser(userMap.get(participant.getUser().getId()));
                group.addParticipant(participant);
            }

        }
        return null;
    }

    @Override
    public Collection<Group> getUsersGroups(String userId) {
        return null;
    }

    @Override
    public void _startGroup(String groupId) {

    }

    @Override
    public void _finishGroup(String groupId) {

    }
}
