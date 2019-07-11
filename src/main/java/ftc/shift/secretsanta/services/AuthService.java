package ftc.shift.secretsanta.services;

import ftc.shift.secretsanta.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private UserService userService;

    @Autowired
    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public User authUser(User user) {
        if (userService.isRegistered(user)) {
            return userService.provideUserByName(user.getName());
        } else
            return userService.createUser(user);
    }

}
