package ftc.shift.sample.repositories;

import ftc.shift.sample.models.User;

public interface UserRepository {
    User getUser(String userId);
}
