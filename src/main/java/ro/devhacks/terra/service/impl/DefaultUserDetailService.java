package ro.devhacks.terra.service.impl;

import ro.devhacks.terra.model.AuthenticatedUser;
import ro.devhacks.terra.model.User;
import ro.devhacks.terra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DefaultUserDetailService implements UserDetailsService {
    private final UserService userService;

    @Autowired
    public DefaultUserDetailService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public AuthenticatedUser loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email=%s was not found", email)));
        return new AuthenticatedUser(user);
    }
}

