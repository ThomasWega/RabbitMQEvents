package me.wega.mq_events.rabbit;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

final class Internals {
    private Internals() {
    }

    @SuppressWarnings("unchecked")
    static <T> @NotNull List<Class<? super T>> ancestors(final @NotNull Class<T> type) {
        final List<Class<? super T>> types = new ArrayList<>();
        types.add(type);
        for(int i = 0; i < types.size(); i++) {
            final Class<?> next = types.get(i);
            final Class<?> superclass = next.getSuperclass();
            if(superclass != null) {
                types.add((Class<? super T>) superclass);
            }
            final Class<?>[] interfaces = next.getInterfaces();
            for(final Class<?> iface : interfaces) {
                // we have a list because we want to preserve order, but we don't want duplicates
                if(!types.contains(iface)) {
                    types.add((Class<? super T>) iface);
                }
            }
        }
        return types;
    }
}
