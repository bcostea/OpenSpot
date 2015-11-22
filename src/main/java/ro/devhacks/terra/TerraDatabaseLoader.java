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
        parkingSpotRepository.save(new ParkingSpot(44.42109179789962, 26.096820831298828));
        parkingSpotRepository.save(new ParkingSpot(44.41891555162064, 26.10128402709961));
        parkingSpotRepository.save(new ParkingSpot(44.42311471475128, 26.10076904296875));
        parkingSpotRepository.save(new ParkingSpot(44.421551557875866, 26.106433868408203));

        parkingSpotRepository.save(new ParkingSpot(44.42226417869395, 26.11239105463028));
        parkingSpotRepository.save(new ParkingSpot(44.43561846512888, 26.049914360046387));
        parkingSpotRepository.save(new ParkingSpot(44.43555717875888, 26.050536632537842));
        parkingSpotRepository.save(new ParkingSpot(44.435541857156316, 26.050955057144165));
        parkingSpotRepository.save(new ParkingSpot(44.435587821951906, 26.05134665966034));
        parkingSpotRepository.save(new ParkingSpot(44.43549972272858, 26.05194479227066));
        parkingSpotRepository.save(new ParkingSpot(44.43553228115269, 26.052085608243942));
        parkingSpotRepository.save(new ParkingSpot(44.41591158566286, 26.082669496536255));
        parkingSpotRepository.save(new ParkingSpot(44.417252660963115, 26.086124181747437));
        parkingSpotRepository.save(new ParkingSpot(44.417444240638126, 26.086843013763428));
        parkingSpotRepository.save(new ParkingSpot(44.41947494660301, 26.08376383781433));
        parkingSpotRepository.save(new ParkingSpot(44.41942130622025, 26.088892221450806));
        parkingSpotRepository.save(new ParkingSpot(44.41935233994155, 26.09029769897461));
        parkingSpotRepository.save(new ParkingSpot(44.41876995478855, 26.09024405479431));
        parkingSpotRepository.save(new ParkingSpot(44.422662629558715, 26.088881492614746));
        parkingSpotRepository.save(new ParkingSpot(44.42185039992162, 26.088227033615112));
        parkingSpotRepository.save(new ParkingSpot(44.42191936325371, 26.087132692337036));
        parkingSpotRepository.save(new ParkingSpot(44.4243330286274, 26.093838214874268));
        parkingSpotRepository.save(new ParkingSpot(44.42282354163813, 26.095576286315918));
        parkingSpotRepository.save(new ParkingSpot(44.42217989066302, 26.09455704689026));
        parkingSpotRepository.save(new ParkingSpot(44.421812086924206, 26.097958087921143));
        // Biblioteca
        parkingSpotRepository.save(new ParkingSpot(44.426097240037066, 26.109960973262787));
        parkingSpotRepository.save(new ParkingSpot(44.426093409016794, 26.110041439533234));
        parkingSpotRepository.save(new ParkingSpot(44.426087662485884, 26.11013799905777));
        parkingSpotRepository.save(new ParkingSpot(44.426087662485884, 26.110256016254425));
        parkingSpotRepository.save(new ParkingSpot(44.42608383146495, 26.11034721136093));
        parkingSpotRepository.save(new ParkingSpot(44.42608191595439, 26.110430359840393));
        parkingSpotRepository.save(new ParkingSpot(44.426078084933096, 26.11051082611084));
        parkingSpotRepository.save(new ParkingSpot(44.42606850737873, 26.110602021217346));
        parkingSpotRepository.save(new ParkingSpot(44.42606276084538, 26.110733449459076));
        parkingSpotRepository.save(new ParkingSpot(44.426122141662866, 26.109682023525238));

        ParkingSpot interSpot1 = parkingSpotRepository.save(new ParkingSpot(44.436913, 26.102418, ParkingSpotType.PRIVATE, 6));
        ParkingSpot interSpot2 = parkingSpotRepository.save(new ParkingSpot(44.436923, 26.102428, ParkingSpotType.PRIVATE, 6));
        ParkingSpot interSpot3 = parkingSpotRepository.save(new ParkingSpot(44.436933, 26.102438, ParkingSpotType.PRIVATE, 6));
        ParkingSpot interSpot4 = parkingSpotRepository.save(new ParkingSpot(44.436943, 26.102448, ParkingSpotType.PRIVATE, 6));
        ParkingSpot interSpot5 = parkingSpotRepository.save(new ParkingSpot(44.436953, 26.102458, ParkingSpotType.PRIVATE, 6));

        ParkingSpot interSpot6 = parkingSpotRepository.save(new ParkingSpot(44.436813, 26.102418, ParkingSpotType.PRIVATE, 6));
        ParkingSpot interSpot7 = parkingSpotRepository.save(new ParkingSpot(44.436823, 26.102428, ParkingSpotType.PRIVATE, 6));
        ParkingSpot interSpot8 = parkingSpotRepository.save(new ParkingSpot(44.436833, 26.102438, ParkingSpotType.PRIVATE, 6));
        ParkingSpot interSpot9 = parkingSpotRepository.save(new ParkingSpot(44.436843, 26.102448, ParkingSpotType.PRIVATE, 6));
        ParkingSpot interSpot10 = parkingSpotRepository.save(new ParkingSpot(44.436853, 26.102458, ParkingSpotType.PRIVATE, 6));

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
