package ftc.shift.secretsanta.repositories.dataBase.extractors;

import ftc.shift.secretsanta.models.ParticipantQueryEntity;
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
public class UserParticipantsExtractor implements ResultSetExtractor<List<ParticipantQueryEntity>> {
    /**
     * Возвращает тело user только с заполненным полем id
     */
    @Override
    public List<ParticipantQueryEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, ParticipantQueryEntity> result = new HashMap<>();
        while (rs.next()) {
            String userId = rs.getString("USER_ID");
            String groupId = rs.getString("GROUP_ID");
            String prefer = rs.getString("PREFER");
            boolean presented = rs.getBoolean("PRESENTED");
            boolean received = rs.getBoolean("RECEIVED");
            ParticipantQueryEntity participant = new ParticipantQueryEntity();
            participant.setUser(new User(userId, ""));
            participant.setGroupId(groupId);
            participant.setPrefer(prefer);
            participant.setPresented(presented);
            participant.setReceived(received);
            if (!result.containsKey(userId)) {
                result.put(userId, participant);
            }
        }
        return new ArrayList<>(result.values());
    }
}
