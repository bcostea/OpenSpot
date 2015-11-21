package ro.devhacks.terra.service;

import ro.devhacks.terra.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(long id);
    Optional<User> getUserByEmail(String email);
    Boolean saveUser (String username, String password);
}
