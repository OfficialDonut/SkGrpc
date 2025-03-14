package com.github.officialdonut.skgrpc.events;

import ch.njol.skript.registrations.EventValues;
import com.google.common.base.Throwables;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GrpcOnErrorEvent extends Event {

    static {
        EventValues.registerEventValue(GrpcOnErrorEvent.class, String.class, GrpcOnErrorEvent::getError);
    }

    private static final HandlerList HANDLERS = new HandlerList();
    private final String error;

    public GrpcOnErrorEvent(Throwable throwable) {
        error = Throwables.getStackTraceAsString(throwable);
    }

    public String getError() {
        return error;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
