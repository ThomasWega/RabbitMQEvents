package me.wega.mq_events.event;

/**
 * An abstract implementation of a cancellable event.
 *
 * <p>This implementation is not always possible to use if {@link EventBus} requires events
 * to implement an {@code abstract} class.</p>
 *
 */
public abstract class AbstractCancellable implements Cancellable {
    // protected to allow children classes to access
    protected boolean cancelled;

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void cancel(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}