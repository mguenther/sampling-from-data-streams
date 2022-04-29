package net.mguenther.turbines;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public final class TurbineEventDeserializer implements Deserializer<TurbineEvent> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public TurbineEvent deserialize(final String topic, final byte[] data) {
        final var rawPayload = new String(data);
        try {
            return mapper.readValue(rawPayload, TurbineEvent.class);
        } catch (JsonProcessingException e) {
	    throw new RuntimeException(e);
        }
    }
}
