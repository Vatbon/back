package ftc.shift.sample.repositories;

import ftc.shift.sample.exception.NotFoundException;
import ftc.shift.sample.models.User;
import ftc.shift.sample.util.IdFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private static final Map<String, User> userCache = new HashMap<>();

    @Override
    public User getUser(String userId) {
        if (!userCache.containsKey(userId)) {
            throw new NotFoundException();
        }
        return userCache.get(userId);
    }

    @Override
    public User createUser(User user) {
        user.setId(String.valueOf(IdFactory.getNewId()));
        userCache.put(user.getId(), user);
        return user;
    }


}
