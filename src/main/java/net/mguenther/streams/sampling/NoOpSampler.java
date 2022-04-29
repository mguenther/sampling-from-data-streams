package net.mguenther.streams.sampling;

import java.util.Collections;
import java.util.List;

public class NoOpSampler<T> implements Sampler<T> {

    @Override
    public void probe(final T value) {
    }

    @Override
    public List<T> getSamples() {
        return Collections.emptyList();
    }
}
