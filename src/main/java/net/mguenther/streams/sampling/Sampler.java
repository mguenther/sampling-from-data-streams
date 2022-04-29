package net.mguenther.streams.sampling;

import java.util.List;

public interface Sampler<T> {

    void probe(T value);

    List<T> getSamples();
}