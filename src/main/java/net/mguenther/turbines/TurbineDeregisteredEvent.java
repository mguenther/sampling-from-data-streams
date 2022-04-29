package net.mguenther.turbines;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public final class TurbineDeregisteredEvent extends TurbineEvent {

    static final String TYPE = "TURBINE_DEREGISTERED_EVENT";

    public TurbineDeregisteredEvent(@JsonProperty("eventId") final UUID eventId,
                                    @JsonProperty("turbineId") final String turbineId,
                                    @JsonProperty("timestamp") long timestamp) {
        super(eventId, turbineId, timestamp);
    }

    public TurbineDeregisteredEvent(final String sensorId) {
        super(sensorId);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "TurbineDeregisteredEvent{} " + super.toString();
    }
}
