package ro.devhacks.terra;

import ro.devhacks.terra.model.PairingSession;
import ro.devhacks.terra.model.Role;
import ro.devhacks.terra.model.User;
import ro.devhacks.terra.repository.PairingSessionRepository;
import ro.devhacks.terra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.DateUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
@Profile("development")
public class TerraDatabaseLoader {

    private final UserRepository userRepository;
    private final PairingSessionRepository pairingSessionRepository;

    @Autowired
    public TerraDatabaseLoader(UserRepository userRepository, PairingSessionRepository pairingSessionRepository) {
        this.userRepository = userRepository;
        this.pairingSessionRepository = pairingSessionRepository;
    }

    @PostConstruct
    private void initDatabase() {

        User savedUser = createUser("user01", "user@user.com");
        User secondUser = createUser("user02", "user2@user.com");

        PairingSession pairingSessionOne = new PairingSession();
        pairingSessionOne.setSessionName("Let's code together");
        pairingSessionOne.setLanguage("Java");
        pairingSessionOne.setPractice("TDD");
        pairingSessionOne.setLocation("ITAKE Product Development Room");
        pairingSessionOne.setAtTime(DateUtils.createNow().getTime());
        pairingSessionOne.setDuration("1 hour");
        pairingSessionOne.setCreator(savedUser);

        PairingSession pairingSessionTwo = new PairingSession();
        pairingSessionTwo.setSessionName("Ruby ninja pairing");
        pairingSessionTwo.setLanguage("Ruby");
        pairingSessionTwo.setPractice("Ninja pairing");
        pairingSessionTwo.setLocation("ITAKE Product Development Room");
        pairingSessionTwo.setAtTime(DateUtils.createNow().getTime());
        pairingSessionTwo.setCreator(savedUser);
        pairingSessionTwo.setDuration("1 hour");

        pairingSessionRepository.save(Arrays.asList(pairingSessionOne,pairingSessionTwo) );
    }

    private User createUser(String userName, String email) {
        User user = new User();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(userName));
        user.setRole(Role.USER);

        return userRepository.save(user);
    }
}
