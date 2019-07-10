package ftc.shift.sample.repositories;

import ftc.shift.sample.exception.NotFoundException;
import ftc.shift.sample.models.User;
import ftc.shift.sample.util.IdFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * В репозитории уже существуют 7 пользователей: "Анастасия","Владимир","Владислав","Данила","Ксения","Максим","Никита"
 */
@Repository
public class InMemoryUserRepository implements UserRepository {

    private static final Map<String, User> userCache = new HashMap<>();

    public InMemoryUserRepository() {
        this.createUser(new User("", "Анастасия"));
        this.createUser(new User("", "Владимир"));
        this.createUser(new User("", "Владислав"));
        this.createUser(new User("", "Данила"));
        this.createUser(new User("", "Ксения"));
        this.createUser(new User("", "Максим"));
        this.createUser(new User("", "Никита"));
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
        Pattern p = Pattern.compile("[^a-zA-Zа-яА-Я0-9]");
        if (p.matcher(user.getName()).find())
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

    public User getUserByName(String name) {
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
