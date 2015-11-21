package ro.devhacks.terra.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

@Entity
@SequenceGenerator(name = "seq_gen", sequenceName = "lot_seq")
public class Lot {

    @Id
    @GeneratedValue(generator = "seq_gen", strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne
    private User user;

    @ElementCollection
    @CollectionTable(name="lot_parking_spot", joinColumns=@JoinColumn(name="lot_id"))
    @Column(name="parkingspot_id")
    private List<String> parkingSpotIds = new LinkedList<String>();

    private String name;
    private String parkingSpotId;

    private double price;

    public List<String> getParkingSpotIds() {
        return parkingSpotIds;
    }

    public void setParkingSpotIds(List<String> parkingSpotIds) {
        this.parkingSpotIds = parkingSpotIds;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getParkingSpotId() {
        return parkingSpotId;
    }

    public void setParkingSpotId(String parkingSpotId) {
        this.parkingSpotId = parkingSpotId;
    }
}
