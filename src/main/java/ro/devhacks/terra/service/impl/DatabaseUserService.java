package ro.devhacks.terra.service.impl;

import ro.devhacks.terra.model.Role;
import ro.devhacks.terra.model.User;
import ro.devhacks.terra.repository.UserRepository;
import ro.devhacks.terra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

@Service
public class DatabaseUserService implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public DatabaseUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(userRepository.findOne(id));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findOneByEmail(email);
    }

    @Override
    public Boolean saveUser(String username, String password) {
        User user = new User();
        user.setUserName(username);
        user.setEmail(username);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(password));
        user.setRole(Role.USER);
        try {
            userRepository.save(user);
            return true;
        }
        catch (Exception ex){
            //log
            ex.printStackTrace();
            return false;
        }
    }

}
