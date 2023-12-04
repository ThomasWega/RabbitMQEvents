package me.wega.mq_events.rabbit;

import com.rabbitmq.client.*;
import me.wega.mq_events.event.PostResult;
import me.wega.mq_events.rabbit.config.RabbitEventConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Handles the publishing, declaration + creation of exchanges and queues using {@link RabbitEventConfig}
 * as well as delivery of the messages and their conversion from JSON to Event instances
 * @param channel
 */
public record RabbitEventManager(@NotNull String eventsExchangeName, @NotNull Channel channel) {

    /**
     * Convert Event to JSON and publish it straight to RabbitMQ
     *
     * @param event Event instance
     * @param config Event config
     * @param <E> Event type
     */
    public <E extends RabbitEvent> void publish(@NotNull E event, @NotNull RabbitEventConfig<E> config) throws IOException {
        try {
            channel.basicPublish(eventsExchangeName, config.exchangeRoutingKey(), config.properties(), config.toJson().apply(event).toString().getBytes());
        } catch (IOException e) {
            throw new IOException("Exception occurred while publishing message for event " + event, e);
        }
    }

    @SuppressWarnings("unchecked")
    <E extends RabbitEvent, T extends E> void listen(@NotNull RabbitEventBus<E> eventBus, @NotNull RabbitEventConfig<? super T> config) throws IOException {
        declareExchange(config);
        String queueName = declareQueue(config);
        if (queueName != null) {
            try {
                deliver(queueName, eventBus, config);
            } catch (IOException e) {
                throw new IOException("Error occurred while trying to consume messages from Queue " + queueName, e);
            }
        }
    }

    private <E extends RabbitEvent> void declareExchange(RabbitEventConfig<? super E> config) throws IOException {
        try {
            channel.exchangeDeclare(eventsExchangeName, BuiltinExchangeType.TOPIC, false, false, false, null);
            channel.exchangeDeclare(config.exchangeName(), config.exchangeType(), false, true, true, null);
            channel.exchangeBind(config.exchangeName(), eventsExchangeName, config.exchangeRoutingKey());
        } catch (IOException e) {
            throw new IOException("Exception occurred while creating exchange for " + config.exchangeName(), e);
        }
    }

    private <E extends RabbitEvent> @Nullable String declareQueue(RabbitEventConfig<? super E> config) throws IOException {
        String queue;
        try {
            queue = channel.queueDeclare("", false, true, true, null).getQueue();
            channel.queueBind(queue, config.exchangeName(), config.exchangeRoutingKey());
            return queue;
        } catch (IOException e) {
            throw new IOException("Exception occurred while creating consumer queue for " + config.exchangeName(), e);
        }
    }

    private <E extends RabbitEvent, T extends E> void deliver(@NotNull String exchangeName,
                                                              @NotNull RabbitEventBus<E> eventBus,
                                                              @NotNull RabbitEventConfig<? super T> config) throws IOException {
        channel.basicConsume(exchangeName, true, new DefaultConsumer(channel) {
            @SuppressWarnings("unchecked")
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String fullMessage = new String(body, StandardCharsets.UTF_8);
                JSONObject json = new JSONObject(fullMessage);
                PostResult result = eventBus.post((T) config.fromJson().apply(json));
                try {
                    result.raise();
                } catch (PostResult.CompositeException e) {
                    e.printAllStackTraces();
                }
            }
        });
    }
}