package ro.devhacks.terra.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ro.devhacks.terra.model.ParkingSpot;
import ro.devhacks.terra.model.ParkingSpotStatus;
import ro.devhacks.terra.model.dto.ParkingSpotUpdate;
import ro.devhacks.terra.repository.ParkingSpotRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;

public class DefaultParkingSpotServiceTest {
    ParkingSpotRepository parkingSpotRepository;
    @Before
    public void setup() {
        this.parkingSpotRepository = Mockito.mock(ParkingSpotRepository.class);
    }

    @Test
    public void on_update_with_free_set_free(){
        DefaultParkingSpotService defaultParkingSpotService = new DefaultParkingSpotService(parkingSpotRepository);

        ParkingSpotUpdate parkingSpotUpdate = new ParkingSpotUpdate();
        parkingSpotUpdate.setId("TEST");
        parkingSpotUpdate.setFree(true);

        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setId("TEST");
        parkingSpot.setStatus(ParkingSpotStatus.OCCUPIED);

        Mockito
                .when(parkingSpotRepository.findOne(anyString()))
                .thenReturn(parkingSpot);

        defaultParkingSpotService.updateSpot(parkingSpotUpdate);

        assertEquals(ParkingSpotStatus.FREE, parkingSpot.getStatus());

    }


    @Test
    public void on_update_with_free_set_occupied(){
        DefaultParkingSpotService defaultParkingSpotService = new DefaultParkingSpotService(parkingSpotRepository);

        ParkingSpotUpdate parkingSpotUpdate = new ParkingSpotUpdate();
        parkingSpotUpdate.setId("TEST");
        parkingSpotUpdate.setFree(false);

        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setId("TEST");
        parkingSpot.setStatus(ParkingSpotStatus.FREE);

        Mockito
                .when(parkingSpotRepository.findOne(anyString()))
                .thenReturn(parkingSpot);

        defaultParkingSpotService.updateSpot(parkingSpotUpdate);

        assertEquals(ParkingSpotStatus.OCCUPIED, parkingSpot.getStatus());

    }

}
