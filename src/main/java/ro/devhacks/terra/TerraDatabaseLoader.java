package ro.devhacks.terra;

import ro.devhacks.terra.model.*;
import ro.devhacks.terra.repository.LotRepository;
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
    private final LotRepository lotRepository;

    @Autowired
    public TerraDatabaseLoader(UserRepository userRepository, ParkingSpotRepository parkingSpotRepository, LotRepository lotRepository) {
        this.userRepository = userRepository;
        this.parkingSpotRepository = parkingSpotRepository;
        this.lotRepository = lotRepository;
    }

    @PostConstruct
    private void initDatabase() {

        User savedUser = createUser("user01", "user@user.com");
        User secondUser = createUser("user02", "user2@user.com");
        User interUser = createLotUser("intercontinental", "lot@inter.ro");

        parkingSpotRepository.save(new ParkingSpot(44.426105, 26.109884));
        parkingSpotRepository.save(new ParkingSpot(44.426105, 26.109884, ParkingSpotStatus.OCCUPIED));
        parkingSpotRepository.save(new ParkingSpot(44.433849, 26.006355));
        parkingSpotRepository.save(new ParkingSpot(44.433972, 26.014122, ParkingSpotStatus.OCCUPIED));
        parkingSpotRepository.save(new ParkingSpot(44.433997, 26.020211));

        ParkingSpot interSpot = parkingSpotRepository.save(new ParkingSpot(44.436893, 26.102448));

        Lot lot = new Lot();
        lot.setName("Intercontinental parking");
        lot.setUser(interUser);
        lot.setParkingSpotId(interSpot.getId());

        interSpot.setCapacity(170);
        interSpot.setOccupied(27);
        parkingSpotRepository.save(interSpot);

        lotRepository.save(lot);


    }

    private User createUser(String userName, String email) {
        User user = new User();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(userName));
        user.setRole(Role.USER);

        return userRepository.save(user);
    }
    private User createLotUser(String userName, String email) {
        User user = new User();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(userName));
        user.setRole(Role.LOT);

        return userRepository.save(user);
    }
}
