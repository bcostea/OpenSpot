package ro.devhacks.terra.model;

import org.springframework.security.core.authority.AuthorityUtils;

/**
 * An AuthenticatedUser is a stand-in for a principal that wraps the User mapped model and extends the
 * Spring Security User, allowing us to have a clean user model
 */
public class AuthenticatedUser extends org.springframework.security.core.userdetails.User {

    private User user;

    public AuthenticatedUser(User user) {
        super(user.getEmail(), user.getPasswordHash(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public Role getRole() {
        return user.getRole();
    }

}