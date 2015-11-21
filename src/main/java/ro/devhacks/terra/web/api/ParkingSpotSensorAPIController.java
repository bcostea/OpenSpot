package ro.devhacks.terra.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ro.devhacks.terra.model.ParkingSpot;
import ro.devhacks.terra.model.dto.ParkingSpotUpdate;
import ro.devhacks.terra.service.ParkingSpotService;

@Controller
public class ParkingSpotSensorAPIController implements
        ApplicationListener<BrokerAvailabilityEvent> {
    private final ParkingSpotService parkingSpotService;

    private final MessageSendingOperations<String> messagingTemplate;

    @Autowired
    public ParkingSpotSensorAPIController(ParkingSpotService parkingSpotService, MessageSendingOperations<String> messagingTemplate) {
        this.parkingSpotService = parkingSpotService;
        this.messagingTemplate = messagingTemplate;
    }

    @RequestMapping(value = {"/api/public/spots/{spotId}/update"},
                    method = RequestMethod.POST,
    consumes = "application/json")
    public @ResponseBody ParkingSpot updateSpot(@PathVariable("spotId") String spotId,
                           @RequestBody ParkingSpotUpdate spotUpdate) {
        if (spotUpdate.getId() != null && spotUpdate.getId().equals(spotId)) {
            ParkingSpot spot = parkingSpotService.updateSpot(spotUpdate);
            this.messagingTemplate.convertAndSend("/topic/spots/update", spot);
            return spot;
        } else {
            return null;
        }
    }

    @Override
    public void onApplicationEvent(BrokerAvailabilityEvent event) {

    }
}
