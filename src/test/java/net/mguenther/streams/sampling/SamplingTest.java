package net.mguenther.streams.sampling;

import net.mguenther.kafka.junit.EmbeddedKafkaCluster;
import net.mguenther.kafka.junit.KeyValue;
import net.mguenther.turbines.TurbineEvent;
import net.mguenther.turbines.TurbineEventSerializer;
import net.mguenther.turbines.TurbineEventSubscriber;
import net.mguenther.turbines.TurbineRegisteredEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static net.mguenther.kafka.junit.EmbeddedKafkaCluster.provisionWith;
import static net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig.defaultClusterConfig;
import static net.mguenther.kafka.junit.SendKeyValues.to;

public class SamplingTest {

    private EmbeddedKafkaCluster kafka;

    @BeforeEach
    void setupKafka() {
        kafka = provisionWith(defaultClusterConfig());
        kafka.start();
    }

    @AfterEach
    void tearDownKafka() {
        kafka.stop();
    }

    @Test
    void shouldSampleFractionOfInboundEvents() throws Exception {

        final var config = Map.<String, Object>of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBrokerList(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        final var sampler = new CurtailedBernoulliSampler<TurbineEvent>(50, 0.05);
        final var latch = new CountDownLatch(1_000);
        final var consumer = new InstrumentingTurbineEventSubscriber("turbine-events", config, sampler, latch);
        final var consumerThread = new Thread(consumer);

        consumerThread.start();

        final var records = new ArrayList<KeyValue<String, TurbineRegisteredEvent>>();

        for (int i = 0; i < 1_000; i++) {
            final var turbineId = UUID.randomUUID().toString().substring(0, 8);
            final var latitude = Math.random() * 50.0;
            final var longitude = Math.random() * 10.0;
            final var event = new TurbineRegisteredEvent(turbineId, latitude, longitude);
            records.add(new KeyValue<>(turbineId, event));
        }

        kafka.send(to("turbine-events", records)
                .with(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TurbineEventSerializer.class.getName()));

        latch.await(10, TimeUnit.SECONDS);

        consumer.close();
        consumerThread.join(TimeUnit.SECONDS.toMillis(1));

        final List<TurbineEvent> collectedSamples = sampler.getSamples();

        System.out.println(collectedSamples.size());
        System.out.println(sampler.getComputationsAfterFill());
    }

    static class InstrumentingTurbineEventSubscriber extends TurbineEventSubscriber {

        private final CountDownLatch latch;

        public InstrumentingTurbineEventSubscriber(final String topic,
                                                   final Map<String, Object> userSuppliedConfig,
                                                   final Sampler<TurbineEvent> sampler,
                                                   final CountDownLatch latch) {
            super(topic, userSuppliedConfig, sampler);
            this.latch = latch;
        }

        @Override
        public void onEvent(final TurbineEvent event) {
            super.onEvent(event);
            latch.countDown();
        }
    }
}
