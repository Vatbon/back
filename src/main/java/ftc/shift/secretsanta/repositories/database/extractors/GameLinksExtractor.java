package ftc.shift.secretsanta.repositories.database.extractors;

import ftc.shift.secretsanta.models.GameLinksQueryEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GameLinksExtractor implements ResultSetExtractor<List<GameLinksQueryEntity>> {
    @Override
    public List<GameLinksQueryEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<GameLinksQueryEntity> result = new ArrayList<>();
        while (rs.next()) {
            GameLinksQueryEntity gameLink = new GameLinksQueryEntity();
            gameLink.setUserIdFrom(rs.getString("USER_ID_FROM"));
            gameLink.setUserIdTo(rs.getString("USER_ID_TO"));
            result.add(gameLink);
        }
        return result;
    }
}
