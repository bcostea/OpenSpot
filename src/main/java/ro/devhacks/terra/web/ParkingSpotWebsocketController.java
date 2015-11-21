package ro.devhacks.terra.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ro.devhacks.terra.model.ParkingSpot;
import ro.devhacks.terra.repository.ParkingSpotRepository;

import java.util.List;

@Controller
public class ParkingSpotWebsocketController {

    private final ParkingSpotRepository parkingSpotRepository;

    @Autowired
    public ParkingSpotWebsocketController(ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
    }

    @MessageMapping("/api/public/websocket/spots/all")
    @SendTo("/topic/parkingSpots")
    public List<ParkingSpot> sendParkingSpots() {
        return parkingSpotRepository.findAll();
    }
}
