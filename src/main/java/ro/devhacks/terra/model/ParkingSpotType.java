package ro.devhacks.terra.model;

public enum ParkingSpotType {
    PUBLIC("PUBLIC"),
    PRIVATE("PRIVATE"),
    PERSONAL("PERSONAL")
    ;

    private final String type;

    private ParkingSpotType(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
