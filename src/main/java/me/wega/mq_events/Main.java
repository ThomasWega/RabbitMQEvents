package me.wega.mq_events;

import me.wega.mq_events.event.EventBus;
import me.wega.mq_events.rabbit.RabbitEvent;
import me.wega.mq_events.rabbit.RabbitEventBus;
import me.wega.mq_events.rabbit.RabbitEventManager;
import me.wega.mq_events.rabbit.RabbitManager;

public class Main {
    private RabbitManager rabbitManager = new RabbitManager("user", "password", "127.0.0.1", 5672);
    private RabbitEventManager rabbitEventManager;
    private RabbitEventBus<RabbitEvent> rabbitEventBus;

    public void initialize() {
        this.rabbitManager.onChannelInitialized(() -> {
            this.rabbitEventManager = new RabbitEventManager("events", this.rabbitManager.getChannel());
            this.rabbitEventBus = EventBus.rabbitEventBus(this.rabbitEventManager, RabbitEvent.class);
        }, 10, 250);
    }
}
