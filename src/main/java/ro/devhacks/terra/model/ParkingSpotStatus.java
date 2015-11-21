package ro.devhacks.terra.model;

public enum ParkingSpotStatus {
    FREE("FREE"),
    OCCUPIED("OCCUPIED")
    ;

    private final String status;

    private ParkingSpotStatus(final String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
