package ftc.shift.sample.repositories;

import ftc.shift.sample.models.User;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserRepository implements UserRepository {
    @Override
    public User getUser(String userId) {
        return null;
    }
}
