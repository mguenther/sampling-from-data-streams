package net.mguenther.turbines;

import net.mguenther.streams.Publisher;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class TurbineEventPublisher implements Publisher<TurbineEvent> {

    private final Producer<String, TurbineEvent> underlyingProducer;

    private final String topic;

    public TurbineEventPublisher(final String topic, final Map<String, Object> userSuppliedConfig) {
        final var config = new HashMap<>(userSuppliedConfig);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TurbineEventSerializer.class.getName());
        this.underlyingProducer = new KafkaProducer<>(config);
        this.topic = topic;
    }

    @Override
    public Future<RecordMetadata> log(final TurbineEvent payload) {
        final var record = new ProducerRecord<>(topic, payload.getTurbineId(), payload);
        return underlyingProducer.send(record);
    }

    @Override
    public void close() {
        underlyingProducer.close();
    }
}
