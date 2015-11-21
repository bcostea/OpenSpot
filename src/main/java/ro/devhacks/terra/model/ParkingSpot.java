package ro.devhacks.terra.model;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Document(collection = "parking_spots")
public class ParkingSpot {
    @Id

    private String id;
    private double[] position;
    private ParkingSpotType type = ParkingSpotType.PUBLIC;
    private ParkingSpotStatus status = ParkingSpotStatus.FREE;

    private int capacity=1;
    private int occupied=0;

    public ParkingSpot() {
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getOccupied() {
        return occupied;
    }

    public void setOccupied(int occupied) {
        this.occupied = occupied;
    }

    public ParkingSpot(double lat, double lng) {
        this.setPosition(lat, lng);
    }

    public ParkingSpot(double lat, double lng, ParkingSpotStatus status) {
        this.setPosition(lat, lng);
        this.setStatus(status);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public void setPosition(double lat, double lng) {
        this.position = new double[2];
        this.position[0] = lat;
        this.position[1] = lng;
    }

    public ParkingSpotType getType() {
        return type;
    }

    public void setType(ParkingSpotType type) {
        this.type = type;
    }

    public ParkingSpotStatus getStatus() {
        return status;
    }

    public void setStatus(ParkingSpotStatus status) {
        this.status = status;
    }
}
