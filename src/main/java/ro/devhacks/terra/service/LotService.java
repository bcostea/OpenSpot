package ro.devhacks.terra.service;

import ro.devhacks.terra.model.Lot;
import ro.devhacks.terra.model.ParkingSpot;
import ro.devhacks.terra.model.User;

import java.util.List;

public interface LotService {
    Lot getLot(User activeUser);
    List<ParkingSpot> getSpots(Lot lot);
}
