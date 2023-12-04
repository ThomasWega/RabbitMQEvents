package me.wega.mq_events.rabbit.config;

import me.wega.mq_events.rabbit.RabbitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * If builder is used for config declaration, {@link RabbitEventConfigFactory}
 * needs to be implemented rather then {@link RabbitEventConfig}
 * @param <E> Event type
 */
public interface RabbitEventConfigFactory<E extends RabbitEvent> {
    @NotNull RabbitEventConfig<E> config();
}
