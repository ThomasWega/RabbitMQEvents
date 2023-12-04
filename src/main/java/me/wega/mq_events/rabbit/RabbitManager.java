package me.wega.mq_events.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class RabbitManager {

    private final Connection connection;
    @Getter
    private final Channel channel;
    private ConnectionFactory factory;

    /**
     * Sets parameters and creates new channel and queue.
     */
    public RabbitManager(@NotNull String user,
                         @NotNull String password,
                         @NotNull String ip,
                         @NotNull Integer port) {
        this.factory = new ConnectionFactory();
        this.factory.setUsername(user);
        this.factory.setPassword(password);
        this.factory.setHost(ip);
        this.factory.setPort(port);
        try {
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("Failed to initialize RabbitMQ connection or channel", e);
        }
    }


    /**
     * @param callback When channel is initialized
     */
    public void onChannelInitialized(Runnable callback, int maxRetries, long sleepPeriodMillis) {
        CompletableFuture.runAsync(() -> {
                    int retries = 0;
                    while (channel == null && retries < maxRetries) {
                        try {
                            Thread.sleep(sleepPeriodMillis);
                        } catch (InterruptedException ignored) {
                            Thread.currentThread().interrupt();
                        }
                        retries++;
                    }

                    if (channel != null) {
                        callback.run();
                    }
                }).orTimeout(10L, TimeUnit.SECONDS)
                .exceptionally(throwable -> null);
    }


    /**
     * @return true - if channel is initialized<p>
     * false - if channel is not initialized
     */
    public boolean isChannelInitialized() {
        return channel != null;
    }

    /**
     * Close all connections, channels
     * and set {@link ConnectionFactory} to null
     */
    public void close() throws IOException, TimeoutException {
        if (channel != null) {
            channel.close();
        }
        if (connection != null) {
            connection.close();
        }
        factory = null;
    }
}