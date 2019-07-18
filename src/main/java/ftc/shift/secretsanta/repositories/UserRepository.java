package ftc.shift.secretsanta.repositories;

import ftc.shift.secretsanta.models.User;

public interface UserRepository {
    User getUser(String userId);

    User createUser(User user);

    boolean containsName(String name);

    User getUserByName(String name);

    User updateUser(User user);
}
