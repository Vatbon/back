package ftc.shift.secretsanta.repositories.dataBase.extractors;

import ftc.shift.secretsanta.models.Group;
import ftc.shift.secretsanta.models.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GroupExtractor implements ResultSetExtractor<List<Group>> {
    /**
     * Возвращает тело группы с пустыми participants и host, который содержит лишь id
     */
    @Override
    public List<Group> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, Group> result = new HashMap<>();
        while (rs.next()) {
            String groupId = rs.getString("GROUP_ID");
            Group group;
            if (!result.containsKey(groupId)) {
                String title = rs.getString("TITLE");
                String startTime = rs.getString("START_TIME");
                String endTime = rs.getString("END_TIME");
                boolean started = rs.getBoolean("STARTED");
                boolean finished = rs.getBoolean("FINISHED");
                int amountLimit = rs.getInt("AMOUNT_LIMIT");
                int amount = rs.getInt("AMOUNT");
                int minValue = rs.getInt("MIN_VALUE");
                int maxValue = rs.getInt("MAX_VALUE");
                String method = rs.getString("METHOD");
                String hostId = rs.getString("HOST_ID");
                User host = new User();
                host.setId(hostId);
                group = new Group(groupId, title, startTime, endTime, amount, amountLimit, minValue, maxValue, method, host, started, finished);
                result.put(groupId, group);
            }
        }
        return (ArrayList<Group>) result.values();
    }
}
