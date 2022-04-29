# Sampling from data streams

Sampling data from a continuous stream of data is a useful technique to efficiently extrapolate information from a potentially large body of data. There are a couple of sampling strategies in literature that vary in their degree of complexity. I'd like to introduce you to a rather simple sampling strategy that is easy to implement as well as easy to reason about and might take you a long way until you have to go for more advanced solutions. I'm talking about Bernoulli sampling.

You'll find an implementation of a curtailed Bernoulli sampler in this repository (cf. `Curtailed
`). You'll also find a small showcase that demonstrates the integration of this sampling strategy with a Kafka-based stream processor that consumes domain events.

Have a look at the corresponding [blog](TBD) for a detailed explanation on the implemented sampling strategy.

## License

This work is released under the terms of the [LGPL v3](http://www.gnu.org/licenses/lgpl-3.0.html).