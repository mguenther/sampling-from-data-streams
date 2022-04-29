package net.mguenther.turbines;

import net.mguenther.streams.Subscriber;
import net.mguenther.streams.sampling.NoOpSampler;
import net.mguenther.streams.sampling.Sampler;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TurbineEventSubscriber implements Subscriber<TurbineEvent> {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String topic;

    private final Map<String, Object> config;

    private final Sampler<TurbineEvent> sampler;

    private Consumer<String, TurbineEvent> underlyingConsumer;

    private volatile boolean running = true;

    public TurbineEventSubscriber(final String topic,
                                  final Map<String, Object> userSuppliedConfig) {
        this(topic, userSuppliedConfig, new NoOpSampler<>());
    }

    public TurbineEventSubscriber(final String topic,
                                  final Map<String, Object> userSuppliedConfig,
                                  final Sampler<TurbineEvent> sampler) {
        final var config = new HashMap<>(userSuppliedConfig);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TurbineEventDeserializer.class.getName());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "turbine-event-consumers");
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        this.topic = topic;
        this.config = config;
        this.sampler = sampler;
    }

    @Override
    public void onEvent(final TurbineEvent event) {
        sampler.probe(event);
        if (event instanceof TurbineRegisteredEvent) {
            log.info("A new wind turbine has been registered. It's ID is {} and it is located at {}/{} (lat/lon).",
                    event.getTurbineId(),
                    ((TurbineRegisteredEvent) event).getLatitude(),
                    ((TurbineRegisteredEvent) event).getLongitude());
        } else if (event instanceof TurbineDeregisteredEvent) {
            log.info("The wind turbine with ID {} has been de-registered.", event.getTurbineId());
        } else {
            log.warn("Received an unknown event type: {}", event);
        }
    }

    @Override
    public void close() {
        running = false;
    }

    @Override
    public void run() {
        initialize();
        while (isRunning()) {
            poll();
        }
        shutdown();
    }

    private void initialize() {
        underlyingConsumer = new KafkaConsumer<>(config);
        underlyingConsumer.subscribe(Collections.singletonList(topic));
    }

    private boolean isRunning() {
        return running;
    }

    private void poll() {
        var records = underlyingConsumer.poll(Duration.ofMillis(1_000));
        records.forEach(record -> onEvent(record.value()));
    }

    private void shutdown() {
        underlyingConsumer.close();
    }
}
