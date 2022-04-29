package net.mguenther.turbines;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TurbineRegisteredEvent.class, name = TurbineRegisteredEvent.TYPE),
        @JsonSubTypes.Type(value = TurbineDeregisteredEvent.class, name = TurbineDeregisteredEvent.TYPE)
})
public abstract class TurbineEvent {

    private final UUID eventId;

    private final String turbineId;

    private final long timestamp;

    @JsonCreator
    public TurbineEvent(@JsonProperty("eventId") final UUID eventId,
                        @JsonProperty("turbineId") final String turbineId,
                        @JsonProperty("timestamp") final long timestamp) {
        this.eventId = eventId;
        this.turbineId = turbineId;
        this.timestamp = timestamp;
    }

    public TurbineEvent(final String turbineId) {
        this(UUID.randomUUID(), turbineId, System.currentTimeMillis());
    }

    public abstract String getType();

    public final UUID getEventId() {
        return eventId;
    }

    public final String getTurbineId() {
        return turbineId;
    }

    public final long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "TurbineEvent{" +
                "eventId=" + eventId +
                ", turbineId='" + turbineId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
