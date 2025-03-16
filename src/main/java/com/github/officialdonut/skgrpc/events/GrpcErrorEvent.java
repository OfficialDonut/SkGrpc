package com.github.officialdonut.skgrpc.events;

import ch.njol.skript.registrations.EventValues;
import io.grpc.Status;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GrpcErrorEvent extends Event {

    static {
        EventValues.registerEventValue(GrpcErrorEvent.class, Status.class, GrpcErrorEvent::getStatus);
    }

    private static final HandlerList HANDLERS = new HandlerList();
    private final Status status;

    public GrpcErrorEvent(Throwable throwable) {
        status = Status.fromThrowable(throwable);
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
