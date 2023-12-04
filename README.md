# RabbitMQEvents Documentation

## Initialization

To start using RabbitMQEvents, initialize the necessary components in your application. Below is an example in Java:

```java
// Initialize RabbitMQ manager
public RabbitManager rabbitManager = new RabbitManager("guest", "guest", "127.0.0.1", 5672);

// Initialize RabbitMQ event manager and event bus
public RabbitEventManager rabbitEventManager;
public RabbitEventBus<RabbitEvent> rabbitEventBus;

public void initialize() {
    this.rabbitManager.onChannelInitialized(() -> {
        // Initialize event manager and event bus when the RabbitMQ channel is ready
        this.rabbitEventManager = new RabbitEventManager("events", this.rabbitManager.getChannel());
        this.rabbitEventBus = EventBus.rabbitEventBus(this.rabbitEventManager, RabbitEvent.class);
    }, 10, 250);
}
```

## Creating Custom Events

Define custom events by implementing the `RabbitEvent` interface. Here's an example:

```java
public class TestEvent implements RabbitEvent {
    private final @NotNull UUID uuid;

    public TestEvent(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public @NotNull UUID getUUID() {
        return this.uuid;
   }
}
```

## Subscribing to Events

Subscribe to events with a dedicated event subscriber class:

```java
public class TestEventSubscriber implements EventSubscriber<TestEvent> {
    @Override
    public void onEvent(@NotNull TestEvent event) {
        System.out.println("EVENT UUID = " + event.getUUID());
    }

    @Override
    public int postOrder() {
        return PostOrder.NORMAL;
    }

    @Override
    public boolean acceptsCancelled() {
        return false;
    }
}
```

This will create the following queues and exchanges

![image](https://github.com/ThomasWega/RabbitMQEvents/assets/82312488/edadfb5d-4d9d-445f-b2aa-1c0b25f04bd8)
![image](https://github.com/ThomasWega/RabbitMQEvents/assets/82312488/11044bef-54af-4a4b-bcda-f34290280495)


## Publishing Events

Publish events using the event manager:

```java
rabbitEventManager.publish(new TestEvent(uuid), new TestEventConfig().config());
```

## Closing Connections

Ensure all connections are closed when they are no longer needed:

```java
rabbitManager.close();
```

## Example Usage

Explore a complete example of RabbitMQEvents used in a Minecraft Spigot plugin [here](https://github.com/ThomasWega/RabbitMQEvents-Example).
