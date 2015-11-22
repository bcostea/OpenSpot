package ro.devhacks.terra.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.devhacks.terra.model.Lot;
import ro.devhacks.terra.model.ParkingSpot;
import ro.devhacks.terra.model.User;
import ro.devhacks.terra.repository.LotRepository;
import ro.devhacks.terra.repository.ParkingSpotRepository;
import ro.devhacks.terra.service.LotService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by BogdanCo on 2015-11-22.
 */
@Service
public class LotServiceImpl implements LotService {

    private final LotRepository lotRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    @Autowired
    public LotServiceImpl(LotRepository lotRepository, ParkingSpotRepository parkingSpotRepository) {
        this.lotRepository = lotRepository;
        this.parkingSpotRepository = parkingSpotRepository;
    }

    public Lot getLot(User activeUser) {
        return lotRepository.findByUser(activeUser);
    }

    public List<ParkingSpot> getSpots(Lot lot) {
        Iterable<ParkingSpot> spotIter = parkingSpotRepository.findAll(lot.getParkingSpotIds());
        List<ParkingSpot> spots = new ArrayList<>();

        Iterator<ParkingSpot> iter = spotIter.iterator();
        while (iter.hasNext()) {
            spots.add(iter.next());
        }

        return spots;
    }

}
