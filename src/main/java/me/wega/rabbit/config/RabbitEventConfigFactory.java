package me.wega.rabbit.config;

import me.wega.rabbit.RabbitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * If builder is used for config declaration, {@link RabbitEventConfigFactory}
 * needs to be implemented rather then {@link RabbitEventConfig}
 * @param <E> Event type
 */
public interface RabbitEventConfigFactory<E extends RabbitEvent> {
    @NotNull RabbitEventConfig<E> config();
}
