package me.wega.mq_events;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import me.wega.mq_events.rabbit.RabbitEvent;
import me.wega.mq_events.rabbit.config.RabbitEventConfig;
import me.wega.mq_events.rabbit.config.RabbitEventConfigBuilder;
import me.wega.mq_events.rabbit.config.RabbitEventConfigFactory;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.UUID;

public class TestEvent implements RabbitEvent, RabbitEventConfigFactory<TestEvent> {
    private final @NotNull UUID uuid;

    public TestEvent(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public @NotNull RabbitEventConfig<TestEvent> config() {
        return new RabbitEventConfigBuilder<TestEvent>()
                .exchangeName("event.test")
                .exchangeType(BuiltinExchangeType.FANOUT)
                .exchangeRoutingKey("test.#")
                .properties(new AMQP.BasicProperties().builder()
                        .expiration("10000")
                        .build()
                )
                .toJson(event -> new JSONObject()
                        .put("uuid", this.uuid)
                )
                .fromJson(jsonObject -> new TestEvent(UUID.fromString(jsonObject.getString("uuid"))))
                .build();
    }

    public @NotNull UUID getUUID() {
        return this.uuid;
    }
}
