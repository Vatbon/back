package ftc.shift.secretsanta.repositories.inmemory;

import ftc.shift.secretsanta.exception.NotFoundException;
import ftc.shift.secretsanta.models.User;
import ftc.shift.secretsanta.repositories.UserRepository;
import ftc.shift.secretsanta.util.IdFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    public User updateUser(User user) {
        if (user.getName() == null)
            return null;
        if (user.getId() == null)
            return null;
        if (!userCache.containsKey(user.getId())) {
            return null;
        }
        User old = userCache.get(user.getId());
        user.setName(old.getName());

        for (String s : old.getGroupsAsParticipant()) {
            user.addGroupAsParticipant(s);
        }
        for (String s : old.getGroupsAsHost()) {
            user.addGroupAsHost(s);
        }
        userCache.put(user.getId(), user);

        return user;
    }


}
