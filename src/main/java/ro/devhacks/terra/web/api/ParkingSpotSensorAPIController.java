package ro.devhacks.terra.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ro.devhacks.terra.model.ParkingSpot;
import ro.devhacks.terra.model.dto.ParkingSpotUpdate;
import ro.devhacks.terra.service.ParkingSpotService;

@Controller
public class ParkingSpotSensorAPIController {
    private final ParkingSpotService parkingSpotService;

    @Autowired
    public ParkingSpotSensorAPIController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @RequestMapping(value = {"/api/public/spots/{spotId}/update"},
                    method = RequestMethod.POST,
    consumes = "application/json")
    public @ResponseBody ParkingSpot updateSpot(@PathVariable("spotId") String spotId,
                           @RequestBody ParkingSpotUpdate spotUpdate) {
        if (spotUpdate.getId() != null && spotUpdate.getId().equals(spotId)) {
            return parkingSpotService.updateSpot(spotUpdate);
        } else {
            return null;
        }
    }

}
