package ro.devhacks.terra.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import ro.devhacks.terra.model.ParkingSpot;
import ro.devhacks.terra.model.ParkingSpotStatus;
import ro.devhacks.terra.model.dto.ParkingSpotUpdate;
import ro.devhacks.terra.repository.ParkingSpotRepository;
import ro.devhacks.terra.service.ParkingSpotService;

import java.util.List;

@Service
public class DefaultParkingSpotService implements ParkingSpotService {

    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    public DefaultParkingSpotService(ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
    }

    @Override
    public List<ParkingSpot> findByPositionWithin(Circle circle) {
        return parkingSpotRepository.findByPositionWithin(circle);
    }

    @Override
    public List<ParkingSpot> findByPositionWithin(Box box) {
        return parkingSpotRepository.findByPositionWithin(box);
    }

    @Override
    public List<ParkingSpot> findByPositionNear(Point location, Distance distance) {
        return parkingSpotRepository.findByPositionNear(location, distance);
    }

    @Override
    public ParkingSpot updateSpot(ParkingSpotUpdate spotUpdate) {
        ParkingSpot spot = parkingSpotRepository.findOne(spotUpdate.getId()) ;
        spot.setStatus(spotUpdate.getFree() ? ParkingSpotStatus.FREE : ParkingSpotStatus.OCCUPIED);
        return parkingSpotRepository.save(spot);
    }
}
