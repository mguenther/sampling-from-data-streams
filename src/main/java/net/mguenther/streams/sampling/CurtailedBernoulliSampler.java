package net.mguenther.streams.sampling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CurtailedBernoulliSampler<T> implements Sampler<T> {

    private final List<T> samples;

    private final int maxNumberOfSamples;

    private final Random sourceOfRandomness;

    private final double probability;

    private long skipUntil;

    private int computationsAfterFill;

    public CurtailedBernoulliSampler(final int maxNumberOfSamples, final double probability) {
        this(maxNumberOfSamples, probability, new Random());
    }

    public CurtailedBernoulliSampler(final int maxNumberOfSamples, final double probability, final Random sourceOfRandomness) {
        this.samples = new ArrayList<>(maxNumberOfSamples);
        this.maxNumberOfSamples = maxNumberOfSamples;
        this.probability = probability;
        this.sourceOfRandomness = sourceOfRandomness;
        this.skipUntil = 0;
        this.computationsAfterFill = 0;
    }

    @Override
    public void probe(final T value) {
        if (hasCapacity()) {
            samples.add(value);
        } else {
            if (skipUntil == 0) {
                calculateNextSkipUntil();
            }

            advance();

            if (skipUntil == 0) {
                samples.remove(0);
                samples.add(value);
            }
        }
    }

    private boolean hasCapacity() {
        return !isCapacityExhausted();
    }

    private boolean isCapacityExhausted() {
        return samples.size() == maxNumberOfSamples;
    }

    private void calculateNextSkipUntil() {
        skipUntil = Math.max(1, (int) Math.floor(Math.log(sourceOfRandomness.nextDouble()) / Math.log(1 - probability)));
        computationsAfterFill++;
        //System.out.println("[BERNOULLI] next: " + skipUntil);
    }

    private void advance() {
        skipUntil--;
    }

    @Override
    public List<T> getSamples() {
        return Collections.unmodifiableList(samples);
    }

    public int getComputationsAfterFill() {
        return computationsAfterFill;
    }
}
