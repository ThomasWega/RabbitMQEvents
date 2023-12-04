package me.wega.event;

import me.wega.rabbit.RabbitEvent;
import me.wega.rabbit.RabbitEventBus;
import me.wega.rabbit.RabbitEventManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * An event bus.
 *
 * @param <E> the event type
 */
public interface EventBus<E extends Event> {

    /**
     * Creates an event bus for RabbitMQ.
     *
     * @param type the event type
     * @param <E> the event type
     * @return an event bus
     */
    static <E extends RabbitEvent> @NotNull RabbitEventBus<E> rabbitEventBus(final @NotNull RabbitEventManager eventManager, final @NotNull Class<E> type) {
        return rabbitEventBus(eventManager, type, Accepts.nonCancelledWhenNotAcceptingCancelled());
    }

    /**
     * Creates an event bus for RabbitMQ.
     *
     * @param type the event type
     * @param <E> the event type
     * @return an event bus
     */
    static <E extends RabbitEvent> @NotNull RabbitEventBus<E> rabbitEventBus(final @NotNull RabbitEventManager eventManager, final @NotNull Class<E> type, final @NotNull Accepts<E> accepts) {
        return new RabbitEventBus<>(eventManager, type, accepts);
    }

    /**
     * Gets the type of events accepted by this event bus.
     *
     * <p>This is represented by the <code>E</code> type parameter.</p>
     *
     * @return the event type
     */
    @NotNull Class<E> type();

    /**
     * Posts an event to all registered subscribers.
     *
     * @param event the event
     * @return the post result of the operation
     */
    @NotNull PostResult post(final @NotNull E event);

    /**
     * Determines whether the specified event has been subscribed to.
     *
     * @param type the event type
     * @return {@code true} if the event has subscribers, {@code false} otherwise
     */
    boolean subscribed(final @NotNull Class<? extends E> type);

    /**
     * Registers the given {@code subscriber} to receive events.
     *
     * @param event the event type
     * @param subscriber the subscriber
     * @param <T> the event type
     */
    <T extends E> void subscribe(final @NotNull Class<T> event, final @NotNull EventSubscriber<? super T> subscriber);

    /**
     * Unregisters the given {@code subscriber} to no longer receive events.
     *
     * @param subscriber the subscriber
     * @param <T> the event type
     */
    <T extends E> void unsubscribe(final @NotNull EventSubscriber<? super T> subscriber);

    /**
     * Unregisters all subscribers matching the {@code predicate}.
     *
     * @param predicate the predicate to test subscribers for removal
     */
    void unsubscribeIf(final @NotNull Predicate<EventSubscriber<? super E>> predicate);

    /**
     * An acceptor.
     *
     * @param <E> the event type
     */
    interface Accepts<E> {
        /**
         * The default acceptor.
         *
         * @param <E> the event type
         * @return the default acceptor
         */
        static <E> @NotNull Accepts<E> nonCancelledWhenNotAcceptingCancelled() {
            return (type, event, subscriber) -> {
                if(!subscriber.acceptsCancelled()) {
                    return !(event instanceof Cancellable) || !((Cancellable) event).isCancelled();
                }
                return true;
            };
        }

        /**
         * Tests if a subscriber accepts an event.
         *
         * @param type the event type
         * @param event the event
         * @param subscriber the event subscriber
         * @return {@code true} if {@code subscriber} accepts the {@code event}
         */
        boolean accepts(final Class<E> type, final @NotNull E event, final @NotNull EventSubscriber<? super E> subscriber);
    }
}