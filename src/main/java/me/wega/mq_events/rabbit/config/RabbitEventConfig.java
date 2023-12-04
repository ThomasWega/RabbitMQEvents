package me.wega.mq_events.rabbit.config;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import me.wega.mq_events.event.EventConfig;
import me.wega.mq_events.rabbit.RabbitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Config for {@link RabbitEvent}.
 * All the values need to be specified in order
 * to create a valid exchange and consumer queues in RabbitMQ
 *
 * @param <E> Event type
 */
public interface RabbitEventConfig<E extends RabbitEvent> extends EventConfig<E> {
    @NotNull String exchangeName();
    @NotNull BuiltinExchangeType exchangeType();
    @NotNull String exchangeRoutingKey();
    @NotNull AMQP.BasicProperties properties();
}
