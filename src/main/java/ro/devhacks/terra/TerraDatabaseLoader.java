package ro.devhacks.terra;

import ro.devhacks.terra.model.ParkingSpot;
import ro.devhacks.terra.model.Role;
import ro.devhacks.terra.model.User;
import ro.devhacks.terra.repository.ParkingSpotRepository;
import ro.devhacks.terra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Profile("development")
public class TerraDatabaseLoader {

    private final UserRepository userRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    @Autowired
    public TerraDatabaseLoader(UserRepository userRepository, ParkingSpotRepository parkingSpotRepository) {
        this.userRepository = userRepository;
        this.parkingSpotRepository = parkingSpotRepository;
    }

    @PostConstruct
    private void initDatabase() {

        User savedUser = createUser("user01", "user@user.com");
        User secondUser = createUser("user02", "user2@user.com");

        parkingSpotRepository.save(new ParkingSpot("bib", 44.426105, 26.109884));
        parkingSpotRepository.save(new ParkingSpot("pacii", 44.426105, 26.109884));
        parkingSpotRepository.save(new ParkingSpot("pacii", 44.433849, 26.006355));
        parkingSpotRepository.save(new ParkingSpot("gorj", 44.433972, 26.014122));
        parkingSpotRepository.save(new ParkingSpot("timi", 44.433997, 26.020211));
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
