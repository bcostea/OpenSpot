package ro.devhacks.terra.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ro.devhacks.terra.model.ParkingSpot;
import ro.devhacks.terra.repository.ParkingSpotRepository;

import java.util.List;

@Controller
public class ParkingSpotAPIController {

    private final ParkingSpotRepository parkingSpotRepository;

    @Autowired
    public ParkingSpotAPIController(ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
    }

    @RequestMapping({"/api/public/spots/all"})
    public @ResponseBody List<ParkingSpot> getAllParkingSpots(){
        return parkingSpotRepository.findAll();
    }

}
