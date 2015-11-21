package ro.devhacks.terra.model;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Document(collection = "parking_spots")
public class ParkingSpot {
    @Id
    private String id;
    private double[] position;

    public ParkingSpot() {
    }

    public ParkingSpot(String id, double lat, double lng) {
        this.id = id;
        this.setPosition(lat, lng);
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

}
