package me.wega.mq_events;

import me.wega.mq_events.event.EventSubscriber;
import me.wega.mq_events.event.PostOrder;
import org.jetbrains.annotations.NotNull;

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
