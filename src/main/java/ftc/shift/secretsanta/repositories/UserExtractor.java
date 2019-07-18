package ftc.shift.secretsanta.repositories;

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
public class UserExtractor implements ResultSetExtractor<List<User>> {
    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, User> users = new HashMap<>();

        while (rs.next()) {
            String userId = rs.getString("USER_ID");

            if (!users.containsKey(userId)) {
                User user = new User();

                user.setId(rs.getString("USER_ID"));
                user.setName(rs.getString("NAME"));

                users.put(userId, user);
            }
        }

        return new ArrayList<>(users.values());
    }
}