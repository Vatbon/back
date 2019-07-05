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

    public InMemoryUserRepository() {
        this.createUser(new User("", "Vasya"));
        this.createUser(new User("", "Anton"));
    }

    @Override
    public User getUser(String userId) {
        if (userId == null)
            return null;
        if (!userCache.containsKey(userId)) {
            throw new NotFoundException();
        }
        return userCache.get(userId);
    }

    @Override
    public User createUser(User user) {
        if (user == null || user.getName() == null)
            return null;
        user.setId(String.valueOf(IdFactory.getNewId()));
        userCache.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean containsName(String name) {
        if (name == null)
            return false;
        for (User value : userCache.values()) {
            if (value.getName().equals(name))
                return true;
        }
        return false;
    }

    @Override
    public User getUserbyName(String name) {
        if (name == null)
            return null;
        for (User user : userCache.values()) {
            if (name.equals(user.getName())) {
                return user;
            }
        }
        return null;
    }


}
