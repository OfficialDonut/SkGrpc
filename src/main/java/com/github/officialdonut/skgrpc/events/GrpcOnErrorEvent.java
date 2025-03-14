package com.github.officialdonut.skgrpc.events;

import ch.njol.skript.registrations.EventValues;
import io.grpc.Status;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GrpcOnErrorEvent extends Event {

    static {
        EventValues.registerEventValue(GrpcOnErrorEvent.class, Status.class, GrpcOnErrorEvent::getStatus);
    }

    private static final HandlerList HANDLERS = new HandlerList();
    private final Status status;

    public GrpcOnErrorEvent(Throwable throwable) {
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
