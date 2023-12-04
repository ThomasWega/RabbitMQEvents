package me.wega.event;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates the outcome of a {@link EventBus#post(Event)} call.
 *
 */
public abstract class PostResult {
    PostResult() {
        if(!(this instanceof Success || this instanceof Failure)) {
            throw new IllegalStateException();
        }
    }

    /**
     * Marks that no exceptions were thrown by subscribers.
     *
     * @return a {@link PostResult} indicating success
     */
    public static @NotNull PostResult success() {
        return Success.INSTANCE;
    }

    /**
     * Marks that exceptions were thrown by subscribers.
     *
     * @param exceptions the exceptions that were thrown
     * @return a {@link PostResult} indicating failure
     */
    public static @NotNull PostResult failure(final @NotNull Map<EventSubscriber<?>, Throwable> exceptions) {
        if(exceptions.isEmpty()) {
            throw new IllegalStateException("no exceptions present");
        }
        return new Failure(new HashMap<>(exceptions));
    }

    /**
     * Gets if the {@link EventBus#post(Event)} call was successful.
     *
     * @return if the call was successful
     */
    public abstract boolean wasSuccessful();

    /**
     * Gets the exceptions that were thrown whilst posting the event to subscribers.
     *
     * @return the exceptions thrown by subscribers
     */
    public abstract @NotNull Map<EventSubscriber<?>, Throwable> exceptions();

    /**
     * Raises a {@link CompositeException} if the posting was not
     * {@link #wasSuccessful() successful}.
     *
     * @throws CompositeException if posting was not successful
     */
    public abstract void raise() throws CompositeException;

    @Override
    public abstract String toString();

    private static final class Success extends PostResult {
        static final Success INSTANCE = new Success();

        @Override
        public boolean wasSuccessful() {
            return true;
        }

        @Override
        public @NotNull Map<EventSubscriber<?>, Throwable> exceptions() {
            return Collections.emptyMap();
        }

        @Override
        public void raise() {
        }

        @Override
        public String toString() {
            return "PostResult.success()";
        }
    }

    private static final class Failure extends PostResult {
        private final Map<EventSubscriber<?>, Throwable> exceptions;

        private Failure(final @NotNull Map<EventSubscriber<?>, Throwable> exceptions) {
            this.exceptions = exceptions;
        }

        @Override
        public boolean wasSuccessful() {
            return this.exceptions.isEmpty();
        }

        @Override
        public @NotNull Map<EventSubscriber<?>, Throwable> exceptions() {
            return this.exceptions;
        }

        @Override
        public void raise() throws CompositeException {
            throw new CompositeException(this);
        }

        @Override
        public String toString() {
            return "PostResult.failure(" + this.exceptions + ")";
        }
    }

    /**
     * Exception encapsulating a combined {@link #failure(Map) failure}.
     *
     * @since 3.0.0
     */
    public static final class CompositeException extends Exception {
        private final PostResult result;

        CompositeException(final @NotNull PostResult result) {
            super("Exceptions occurred whilst posting to subscribers");
            this.result = result;
        }

        /**
         * Gets the result that created this composite exception.
         *
         * @return the result
         * @since 5.0.0
         */
        public @NotNull PostResult result() {
            return this.result;
        }

        /**
         * Prints all the stack traces involved in the composite exception.
         *
         * @see Exception#printStackTrace()
         * @since 5.0.0
         */
        public void printAllStackTraces() {
            this.printStackTrace();
            for(final Throwable exception : this.result.exceptions().values()) {
                exception.printStackTrace();
            }
        }
    }
}
