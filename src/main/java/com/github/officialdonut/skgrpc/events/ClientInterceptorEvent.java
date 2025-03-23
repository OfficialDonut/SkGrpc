package com.github.officialdonut.skgrpc.events;

import ch.njol.skript.registrations.EventValues;
import io.grpc.CallOptions;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClientInterceptorEvent extends Event {

    static {
        EventValues.registerEventValue(ClientInterceptorEvent.class, MethodDescriptor.class, ClientInterceptorEvent::getMethodDescriptor);
        EventValues.registerEventValue(ClientInterceptorEvent.class, CallOptions.class, ClientInterceptorEvent::getCallOptions);
    }

    private static final HandlerList HANDLERS = new HandlerList();

    private final MethodDescriptor<?, ?> methodDescriptor;
    private final CallOptions callOptions;

    private Metadata attachedMetadata;
    private CallOptions attachedCallOptions;

    public ClientInterceptorEvent(MethodDescriptor<?, ?> methodDescriptor, CallOptions callOptions) {
        this.methodDescriptor = methodDescriptor;
        this.callOptions = callOptions;
    }

    public void attachMetadata(Metadata metadata) {
        this.attachedMetadata = metadata;
    }

    public void attachCallOptions(CallOptions callOptions) {
        this.attachedCallOptions = callOptions;
    }

    public boolean hasAttachedMetadata() {
        return attachedMetadata != null;
    }

    public boolean hasAttachedCallOptions() {
        return attachedCallOptions != null;
    }

    public Metadata getAttachedMetadata() {
        return attachedMetadata;
    }

    public CallOptions getAttachedCallOptions() {
        return attachedCallOptions;
    }

    public MethodDescriptor<?, ?> getMethodDescriptor() {
        return methodDescriptor;
    }

    public CallOptions getCallOptions() {
        return callOptions;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
