package ftc.shift.secretsanta.repositories.dataBase.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserHostsExtractor implements ResultSetExtractor<List<String>> {
    @Override
    public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<String> result = new ArrayList<>();
        while (rs.next()) {
            String groupId = rs.getString("GROUP_ID");
            if (!result.contains(groupId)) {
                result.add(groupId);
            }
        }
        return result;
    }
}
