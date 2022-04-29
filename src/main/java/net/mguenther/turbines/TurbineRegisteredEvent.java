package net.mguenther.turbines;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public final class TurbineRegisteredEvent extends TurbineEvent {

    static final String TYPE = "TURBINE_REGISTERED_EVENT";

    @JsonProperty
    private final double latitude;

    @JsonProperty
    private final double longitude;

    @JsonCreator
    public TurbineRegisteredEvent(@JsonProperty("eventId") final UUID eventId,
                                  @JsonProperty("turbineId") final String turbineId,
                                  @JsonProperty("timestamp") final long timestamp,
                                  @JsonProperty("latitude") final double latitude,
                                  @JsonProperty("longitude") final double longitude) {
        super(eventId, turbineId, timestamp);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public TurbineRegisteredEvent(final String turbineId,
                                  final double latitude,
                                  final double longitude) {
        super(turbineId);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "TurbineRegisteredEvent{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                "} " + super.toString();
    }
}
