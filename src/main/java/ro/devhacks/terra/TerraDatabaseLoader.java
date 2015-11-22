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

        parkingSpotRepository.save(new ParkingSpot(44.42226417869395, 26.11239105463028));
        parkingSpotRepository.save(new ParkingSpot(44.43561846512888, 26.049914360046387));
        parkingSpotRepository.save(new ParkingSpot(44.43555717875888, 26.050536632537842));
        parkingSpotRepository.save(new ParkingSpot(44.435541857156316, 26.050955057144165));
        parkingSpotRepository.save(new ParkingSpot(44.435587821951906, 26.05134665966034));
        parkingSpotRepository.save(new ParkingSpot(44.43549972272858, 26.05194479227066));
        parkingSpotRepository.save(new ParkingSpot(44.43553228115269, 26.052085608243942));

        ParkingSpot interSpot1 = parkingSpotRepository.save(new ParkingSpot(44.436813, 26.102418));
        ParkingSpot interSpot2 = parkingSpotRepository.save(new ParkingSpot(44.436823, 26.102428));
        ParkingSpot interSpot3 = parkingSpotRepository.save(new ParkingSpot(44.436833, 26.102438));
        ParkingSpot interSpot4 = parkingSpotRepository.save(new ParkingSpot(44.436843, 26.102448));
        ParkingSpot interSpot5 = parkingSpotRepository.save(new ParkingSpot(44.436853, 26.102458));
        ParkingSpot interSpot6 = parkingSpotRepository.save(new ParkingSpot(44.436813, 26.102418));
        ParkingSpot interSpot7 = parkingSpotRepository.save(new ParkingSpot(44.436823, 26.102428));
        ParkingSpot interSpot8 = parkingSpotRepository.save(new ParkingSpot(44.436833, 26.102438));
        ParkingSpot interSpot9 = parkingSpotRepository.save(new ParkingSpot(44.436843, 26.102448));
        ParkingSpot interSpot10 = parkingSpotRepository.save(new ParkingSpot(44.436853, 26.102458));

        Lot lot = new Lot();
        lot.setName("Intercontinental parking");
        lot.setUser(interUser);
        lot.getParkingSpotIds().add(interSpot1.getId());
        lot.getParkingSpotIds().add(interSpot2.getId());
        lot.getParkingSpotIds().add(interSpot3.getId());
        lot.getParkingSpotIds().add(interSpot4.getId());
        lot.getParkingSpotIds().add(interSpot5.getId());
        lot.getParkingSpotIds().add(interSpot6.getId());
        lot.getParkingSpotIds().add(interSpot7.getId());
        lot.getParkingSpotIds().add(interSpot8.getId());
        lot.getParkingSpotIds().add(interSpot9.getId());
        lot.getParkingSpotIds().add(interSpot10.getId());

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
