package me.wega.mq_events.event;

import org.jetbrains.annotations.NotNull;

/**
 * An event subscriber.
 *
 * @param <E> the event type
 */
@FunctionalInterface
public interface EventSubscriber<E> {
    /**
     * Invokes this event subscriber.
     *
     * @param event the event
     */
    void onEvent(final @NotNull E event);

    /**
     * Gets the post order this subscriber should be called at.
     *
     * @return the post order of this subscriber
     */
    default int postOrder() {
        return PostOrder.NORMAL;
    }

    /**
     * Gets if cancelled events should be consumed by this subscriber.
     * <p>Default = false</p>
     *
     * @return {@code true} if cancelled events should be consumed, {@code false} otherwise
     */
    default boolean acceptsCancelled() {
        return false;
    }
}
