package net.mguenther.streams;

import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.Closeable;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface Publisher<T> extends Closeable {

    Future<RecordMetadata> log(T payload);

    default RecordMetadata logSync(T payload) throws InterruptedException {
        try {
            return log(payload).get();
        } catch (ExecutionException e) {
            throw new PublisherException(e.getCause());
        }
    }

    default RecordMetadata logSync(T payload, Duration duration) throws InterruptedException {
        try {
            return log(payload).get(duration.toMillis(), TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            throw new PublisherException(e.getCause());
        } catch (TimeoutException e) {
            throw new PublisherException(e);
        }
    }

    @Override
    void close();
}
