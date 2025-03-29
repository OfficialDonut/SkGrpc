package com.github.officialdonut.skgrpc.events;

import ch.njol.skript.registrations.EventValues;
import io.grpc.*;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerInterceptorEvent extends Event {

    static {
        EventValues.registerEventValue(ServerInterceptorEvent.class, ServerCall.class, ServerInterceptorEvent::getServerCall);
        EventValues.registerEventValue(ServerInterceptorEvent.class, Metadata.class, ServerInterceptorEvent::getMetadata);
    }

    private static final HandlerList HANDLERS = new HandlerList();

    private final ServerCall<?, ?> serverCall;
    private final Metadata metadata;

    private Context attachedContext;
    private boolean callClosed;

    public ServerInterceptorEvent(ServerCall<?, ?> serverCall, Metadata metadata) {
        this.serverCall = serverCall;
        this.metadata = metadata;
    }

    public void attachContext(Context attachedContext) {
        this.attachedContext = attachedContext;
    }

    public void closeCall(Status status, Metadata metadata) {
        serverCall.close(status, metadata);
        callClosed = true;
    }

    public boolean hasAttachedContext() {
        return attachedContext != null;
    }

    public boolean isCallClosed() {
        return callClosed;
    }

    public Context getAttachedContext() {
        return attachedContext;
    }

    public ServerCall<?, ?> getServerCall() {
        return serverCall;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
