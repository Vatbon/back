package ftc.shift.sample.services;

import ftc.shift.sample.models.User;
import ftc.shift.sample.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User provideUser(String userId) {
        return userRepository.getUser(userId);
    }

    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public boolean isRegistered(User user) {
        return userRepository.containsName(user.getName());
    }

    public User provideUserByName(String name) {
        return userRepository.getUserByName(name);
    }

    public boolean isRegistered(String userId) {
        return userRepository.containsName(provideUser(userId).getName());
    }
}
