package ro.devhacks.terra.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @RequestMapping({"/api/public/spots/near"})
    public @ResponseBody List<ParkingSpot> getNearParkingSpots(@RequestParam double lat, @RequestParam double lng){
        return parkingSpotRepository.findByPositionNear(new Point(lat, lng), new Distance(0.5, Metrics.KILOMETERS));
    }


}
