package ro.devhacks.terra.service;

import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import ro.devhacks.terra.model.ParkingSpot;

import java.util.List;

public interface ParkingSpotService {

    List<ParkingSpot> findByPositionWithin(Circle circle);
    List<ParkingSpot> findByPositionWithin(Box box);
    List<ParkingSpot> findByPositionNear(Point location, Distance distance);

}
