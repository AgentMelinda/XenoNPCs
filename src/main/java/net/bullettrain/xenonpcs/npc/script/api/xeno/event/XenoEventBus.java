package net.bullettrain.xenonpcs.npc.script.api.xeno.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * NpcAPI.events(): forwards every call to a real bus and counts registrations, because
 * IEventBus has no listener query (bus 8.0.5). Typed events are built only when someone listens.
 * {@link #unregister} does not decrement: a stale "listening" answer only costs an unread event.
 */
public final class XenoEventBus implements IEventBus {
    private final IEventBus delegate;
    private final AtomicInteger registrations = new AtomicInteger();

    public XenoEventBus(IEventBus delegate) { this.delegate = Objects.requireNonNull(delegate); }

    public boolean hasListeners() { return registrations.get() > 0; }

    @Override public void register(Object target) { registrations.incrementAndGet(); delegate.register(target); }
    @Override public <T extends Event> void addListener(Consumer<T> consumer) { registrations.incrementAndGet(); delegate.addListener(consumer); }
    @Override public <T extends Event> void addListener(EventPriority priority, Consumer<T> consumer) { registrations.incrementAndGet(); delegate.addListener(priority, consumer); }
    @Override public <T extends Event> void addListener(EventPriority priority, boolean receiveCanceled, Consumer<T> consumer) { registrations.incrementAndGet(); delegate.addListener(priority, receiveCanceled, consumer); }
    @Override public <T extends Event> void addListener(EventPriority priority, boolean receiveCanceled, Class<T> type, Consumer<T> consumer) { registrations.incrementAndGet(); delegate.addListener(priority, receiveCanceled, type, consumer); }
    @Override public <T extends net.minecraftforge.eventbus.api.GenericEvent<? extends F>, F> void addGenericListener(Class<F> filter, Consumer<T> consumer) { registrations.incrementAndGet(); delegate.addGenericListener(filter, consumer); }
    @Override public <T extends net.minecraftforge.eventbus.api.GenericEvent<? extends F>, F> void addGenericListener(Class<F> filter, EventPriority priority, Consumer<T> consumer) { registrations.incrementAndGet(); delegate.addGenericListener(filter, priority, consumer); }
    @Override public <T extends net.minecraftforge.eventbus.api.GenericEvent<? extends F>, F> void addGenericListener(Class<F> filter, EventPriority priority, boolean receiveCanceled, Consumer<T> consumer) { registrations.incrementAndGet(); delegate.addGenericListener(filter, priority, receiveCanceled, consumer); }
    @Override public <T extends net.minecraftforge.eventbus.api.GenericEvent<? extends F>, F> void addGenericListener(Class<F> filter, EventPriority priority, boolean receiveCanceled, Class<T> type, Consumer<T> consumer) { registrations.incrementAndGet(); delegate.addGenericListener(filter, priority, receiveCanceled, type, consumer); }
    // The 1.21.1 bus's extra overloads, kept for scripts written against it.
    public <T extends Event> void addListener(Class<T> type, Consumer<T> consumer) { addListener(EventPriority.NORMAL, false, type, consumer); }
    public <T extends Event> void addListener(EventPriority priority, Class<T> type, Consumer<T> consumer) { addListener(priority, false, type, consumer); }
    public <T extends Event> void addListener(boolean receiveCanceled, Consumer<T> consumer) { addListener(EventPriority.NORMAL, receiveCanceled, consumer); }
    public <T extends Event> void addListener(boolean receiveCanceled, Class<T> type, Consumer<T> consumer) { addListener(EventPriority.NORMAL, receiveCanceled, type, consumer); }
    @Override public void unregister(Object target) { delegate.unregister(target); }
    @Override public boolean post(Event event) { return delegate.post(event); }
    @Override public boolean post(Event event, net.minecraftforge.eventbus.api.IEventBusInvokeDispatcher wrapper) { return delegate.post(event, wrapper); }
    @Override public void shutdown() { delegate.shutdown(); }
    @Override public void start() { delegate.start(); }
}
