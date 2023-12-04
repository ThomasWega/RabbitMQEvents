package me.wega.mq_events.event;

/**
 * A cancellable event.
 */
public interface Cancellable {
    /**
     * Tests if the event has been cancelled.
     *
     * @return {@code true} if the event has been cancelled, {@code false} otherwise
     */
    boolean isCancelled();

    /**
     * Sets the cancelled state of the event.
     *
     * @param cancelled {@code true} if the event should be cancelled, {@code false} otherwise
     */
    void cancel(final boolean cancelled);
}