package net.mguenther.streams;

import java.io.Serial;

public class SubscriberException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -689043280L;

    public SubscriberException(final Throwable cause) {
        super(cause);
    }
}
