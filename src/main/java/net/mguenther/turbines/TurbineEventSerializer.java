package net.mguenther.turbines;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.mguenther.streams.PublisherException;
import org.apache.kafka.common.serialization.Serializer;

public final class TurbineEventSerializer implements Serializer<TurbineEvent> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] serialize(final String topic, final TurbineEvent data) {
        try {
            return mapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new PublisherException(e);
        }
    }
}
