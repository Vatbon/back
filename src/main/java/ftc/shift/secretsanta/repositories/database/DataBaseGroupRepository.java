package ftc.shift.secretsanta.repositories.database;

import ftc.shift.secretsanta.models.Group;
import ftc.shift.secretsanta.models.Participant;
import ftc.shift.secretsanta.models.ParticipantQueryEntity;
import ftc.shift.secretsanta.models.User;
import ftc.shift.secretsanta.repositories.GroupRepository;
import ftc.shift.secretsanta.repositories.UserRepository;
import ftc.shift.secretsanta.repositories.database.extractors.GroupExtractor;
import ftc.shift.secretsanta.repositories.database.extractors.UserExtractor;
import ftc.shift.secretsanta.repositories.database.extractors.UserParticipantsExtractor;
import ftc.shift.secretsanta.util.IdFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final UserRepository userRepository;

    @Autowired
    public DataBaseGroupRepository(NamedParameterJdbcTemplate jdbcTemplate,
                                   GroupExtractor groupExtractor,
                                   UserExtractor userExtractor,
                                   UserParticipantsExtractor userParticipantsExtractor,
                                   @Qualifier("databaseUserRepository") UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.groupExtractor = groupExtractor;
        this.userExtractor = userExtractor;
        this.userParticipantsExtractor = userParticipantsExtractor;
        this.userRepository = userRepository;
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
                "from USERS " +
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
                "from USERS_PARTICIPANTS " +
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
        group.setAmount(0);
        for (Participant participant : participants) {
            participant.setUser(userMap.get(participant.getUser().getId()));
            group.addParticipant(participant);
        }
        return group;
    }

    @Override
    public Group updateGroup(String groupId, Group group) {

        String sqlGroupFind = "select * from GROUPS " +
                "where GROUP_ID=:groupId;";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("groupId", groupId);

        List<Group> groups = jdbcTemplate.query(sqlGroupFind, params, groupExtractor);
        if (groups.isEmpty())
            return null;

        String sqlGroup = "update GROUPS " +
                "set TITLE=:title," +
                "START_TIME=:startTime," +
                "END_TIME=:endTime," +
                "STARTED=:started," +
                "FINISHED=:finished," +
                "AMOUNT_LIMIT=:amountLimit," +
                "AMOUNT=:amount," +
                "MIN_VALUE=:minValue," +
                "MAX_VALUE=:maxValue," +
                "METHOD=:method," +
                "HOST_ID=:hostId " +
                "where GROUP_ID=:groupId;";

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

        String sqlHostDelete = "delete from USERS_HOSTS where GROUP_ID=:groupId;";
        String sqlHost = "insert into USERS_HOSTS values(:userId, :groupId);";
        String sqlPartDelete = "delete from USERS_PARTICIPANTS where GROUP_ID=:groupId ;";
        String sqlPart = "insert into USERS_PARTICIPANTS values(:userId, :groupId, :prefer, :presented, :received);";

        jdbcTemplate.update(sqlHostDelete, new MapSqlParameterSource()
                .addValue("groupId", groupId));

        jdbcTemplate.update(sqlHost, new MapSqlParameterSource()
                .addValue("userId", group.getHost().getId())
                .addValue("groupId", groupId));

        jdbcTemplate.update(sqlPartDelete, new MapSqlParameterSource()
                .addValue("groupId", groupId));

        for (Participant participant : group._getParticipants()) {
            jdbcTemplate.update(sqlPart, new MapSqlParameterSource()
                    .addValue("userId", participant.getUser().getId())
                    .addValue("groupId", groupId)
                    .addValue("presented", participant.isPresented())
                    .addValue("received", participant.isReceived())
                    .addValue("prefer", participant.getPrefer()));
        }

        return group;
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
                ":amount, :minValue, :maxValue, :method, :hostId)";

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
        /*

         */
        /*Закидываем нового хозяина в таблицу хозяинов*//*


        String sqlHost = "insert into USERS_HOSTS " +
                "values(:userId, :groupId);";

        MapSqlParameterSource param1 = new MapSqlParameterSource()
                .addValue("userId", group.getHost().getId())
                .addValue("groupId", group.getId());

        jdbcTemplate.update(sqlHost, param1);
        */
        /*Закидываем нового участника в таблицу участников*//*


        String sqlPart = "insert into USERS_PARTICIPANTS(USER_ID, GROUP_ID) " +
                "values(:userId, :groupId);";

        MapSqlParameterSource param2 = new MapSqlParameterSource()
                .addValue("userId", group.getHost().getId())
                .addValue("groupId", group.getId());

        jdbcTemplate.update(sqlPart, param2);

*/
        return group;
    }

    @Override
    public Collection<Group> getAllGroups() {
        String sqlGroup = "select * " +
                "from GROUPS ";

        List<Group> groups = jdbcTemplate.query(sqlGroup, new MapSqlParameterSource(), groupExtractor);

        for (Group group : groups) {
            String groupId = group.getId();

            /*Достаем тело хозяина группы*/
            String sqlHost = "select USER_ID, NAME " +
                    "from USERS " +
                    "where USER_ID=:userId;";

            MapSqlParameterSource hostParams = new MapSqlParameterSource()
                    .addValue("userId", group.getHost().getId());

            List<User> users = jdbcTemplate.query(sqlHost, hostParams, userExtractor);

            if (users.isEmpty())
                return null;

            group.setHost(userRepository.getUser(users.get(0).getId()));

            /*Достаем тело всех участников группы*/
            group.getAllParticipants().clear();
            group.setAmount(0);

            String sqlParts = "select * " +
                    "from USERS_PARTICIPANTS " +
                    "where GROUP_ID=:groupId;";

            MapSqlParameterSource partsParams = new MapSqlParameterSource()
                    .addValue("groupId", groupId);

            List<ParticipantQueryEntity> participants = jdbcTemplate.query(sqlParts, partsParams, userParticipantsExtractor);

            if (participants.isEmpty())
                return null;

            /*Заполняем поле user в каждом participant*/

            for (ParticipantQueryEntity participant : participants) {
                group.addParticipant(userRepository.getUser(participant.getUser().getId()), participant.getPrefer());
            }


            /*
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
            }*/

        }
        return groups;
    }

    @Override
    public void _startGroup(String groupId) {
        String sqlStartGroup = "update GROUPS " +
                "set STARTED=TRUE " +
                "where GROUP_ID=:groupId;";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("groupId", groupId);

        jdbcTemplate.update(sqlStartGroup, param);
    }

    @Override
    public void _finishGroup(String groupId) {
        String sqlFinishGroup = "update GROUPS " +
                "set FINISHED=TRUE " +
                "where GROUP_ID=:groupId;";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("groupId", groupId);

        jdbcTemplate.update(sqlFinishGroup, param);
    }
}
