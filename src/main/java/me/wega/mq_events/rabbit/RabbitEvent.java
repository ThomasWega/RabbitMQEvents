package me.wega.mq_events.rabbit;

import me.wega.mq_events.event.Event;

/**
 * Type of event that is used in RabbitMQ and supports sending the event as JSON message
 */
public interface RabbitEvent extends Event {
}
