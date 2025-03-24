package com.github.officialdonut.skgrpc.events;

import ch.njol.skript.registrations.EventValues;
import io.grpc.CallCredentials;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CallCredentialsEvent extends Event {

    static {
        EventValues.registerEventValue(CallCredentialsEvent.class, CallCredentials.RequestInfo.class, CallCredentialsEvent::getRequestInfo);
    }

    private static final HandlerList HANDLERS = new HandlerList();

    private final CallCredentials.RequestInfo requestInfo;
    private final CallCredentials.MetadataApplier metadataApplier;

    public CallCredentialsEvent(CallCredentials.RequestInfo requestInfo, CallCredentials.MetadataApplier metadataApplier) {
        this.requestInfo = requestInfo;
        this.metadataApplier = metadataApplier;
    }

    public CallCredentials.RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public CallCredentials.MetadataApplier getMetadataApplier() {
        return metadataApplier;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
