package ftc.shift.secretsanta.services;

import ftc.shift.secretsanta.models.User;
import ftc.shift.secretsanta.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

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
        if (user == null || user.getName() == null)
            return null;
        if (user.getName().length() > 30 | user.getName().length() < 2)
            return null;
        Pattern p = Pattern.compile("[^a-zA-Zа-яА-Я0-9_ё]");
        if (p.matcher(user.getName()).find())
            return null;
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
