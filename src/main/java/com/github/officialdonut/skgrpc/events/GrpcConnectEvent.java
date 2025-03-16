package com.github.officialdonut.skgrpc.events;

import ch.njol.skript.registrations.EventValues;
import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GrpcConnectEvent extends Event {

    static {
        EventValues.registerEventValue(GrpcConnectEvent.class, StreamObserver.class, GrpcConnectEvent::getOutgoingStream);
    }

    private static final HandlerList HANDLERS = new HandlerList();
    private final StreamObserver<Message> outgoingStream;

    public GrpcConnectEvent(StreamObserver<Message> outgoingStream) {
        this.outgoingStream = outgoingStream;
    }

    public StreamObserver<Message> getOutgoingStream() {
        return outgoingStream;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
