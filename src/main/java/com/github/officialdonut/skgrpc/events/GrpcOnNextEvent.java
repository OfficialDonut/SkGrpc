package com.github.officialdonut.skgrpc.events;

import ch.njol.skript.registrations.EventValues;
import com.google.protobuf.Message;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GrpcOnNextEvent extends Event {

    static {
        EventValues.registerEventValue(GrpcOnNextEvent.class, Message.class, GrpcOnNextEvent::getMessage);
    }

    private static final HandlerList HANDLERS = new HandlerList();
    private final Message message;

    public GrpcOnNextEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
