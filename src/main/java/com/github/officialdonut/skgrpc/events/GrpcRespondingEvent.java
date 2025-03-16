package com.github.officialdonut.skgrpc.events;

import ch.njol.skript.registrations.EventValues;
import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;
import org.bukkit.event.HandlerList;

public class GrpcRespondingEvent extends GrpcConnectEvent {

    static {
        EventValues.registerEventValue(GrpcRespondingEvent.class, Message.class, GrpcRespondingEvent::getRequest);
    }

    private static final HandlerList HANDLERS = new HandlerList();
    private final Message request;

    public GrpcRespondingEvent(Message request, StreamObserver<Message> responseStream) {
        super(responseStream);
        this.request = request;
    }

    public Message getRequest() {
        return request;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
