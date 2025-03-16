package com.github.officialdonut.skgrpc.impl;

import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import com.github.officialdonut.skgrpc.events.*;
import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;
import org.bukkit.event.Event;

public class SkriptStreamObserver implements RpcStreamObserver {

    private final Trigger onNextTrigger;
    private final Trigger onErrorTrigger;
    private final Trigger onCompleteTrigger;
    private final Trigger onConnectTrigger;
    private final Trigger onReadyTrigger;
    private final Trigger onCancelTrigger;
    private final Trigger onCloseTrigger;
    private Object localVars;

    protected SkriptStreamObserver(Builder builder) {
        onNextTrigger = builder.onNextTrigger;
        onErrorTrigger = builder.onErrorTrigger;
        onCompleteTrigger = builder.onCompleteTrigger;
        onConnectTrigger = builder.onConnectTrigger;
        onReadyTrigger = builder.onReadyTrigger;
        onCancelTrigger = builder.onCancelTrigger;
        onCloseTrigger = builder.onCloseTrigger;
        localVars = builder.localVars;
    }

    @Override
    public void onNext(Message message) {
        executeTrigger(onNextTrigger, new GrpcNextEvent(message));
    }

    @Override
    public void onError(Throwable throwable) {
        executeTrigger(onErrorTrigger, new GrpcErrorEvent(throwable));
    }

    @Override
    public void onCompleted() {
        executeTrigger(onCompleteTrigger, new GrpcCompleteEvent());
    }

    @Override
    public void onConnect(StreamObserver<Message> outgoingStream) {
        executeTrigger(onConnectTrigger, new GrpcConnectEvent(outgoingStream));
    }

    @Override
    public void onReady() {
        executeTrigger(onReadyTrigger, new GrpcReadyEvent());
    }

    @Override
    public void onCancel() {
        executeTrigger(onCancelTrigger, new GrpcCancelEvent());
    }

    @Override
    public void onClose() {
        executeTrigger(onCloseTrigger, new GrpcCloseEvent());
    }

    @Override
    public boolean hasOnConnect() {
        return onConnectTrigger != null;
    }

    @Override
    public boolean hasOnReady() {
        return onReadyTrigger != null;
    }

    @Override
    public boolean hasOnCancel() {
        return onCancelTrigger != null;
    }

    @Override
    public boolean hasOnClose() {
        return onCloseTrigger != null;
    }

    protected void executeTrigger(Trigger trigger, Event event) {
        if (trigger != null) {
            if (localVars != null) {
                Variables.setLocalVariables(event, localVars);
            }
            try {
                TriggerItem.walk(trigger, event);
            } finally {
                localVars = Variables.removeLocals(event);
            }
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder implements RpcStreamObserver.Builder {

        private Trigger onNextTrigger;
        private Trigger onErrorTrigger;
        private Trigger onCompleteTrigger;
        private Trigger onConnectTrigger;
        private Trigger onReadyTrigger;
        private Trigger onCancelTrigger;
        private Trigger onCloseTrigger;
        private Object localVars;

        public Builder onNextTrigger(Trigger onNextTrigger) {
            this.onNextTrigger = onNextTrigger;
            return this;
        }

        public Builder onErrorTrigger(Trigger onErrorTrigger) {
            this.onErrorTrigger = onErrorTrigger;
            return this;
        }

        public Builder onCompleteTrigger(Trigger onCompleteTrigger) {
            this.onCompleteTrigger = onCompleteTrigger;
            return this;
        }

        public Builder onConnectTrigger(Trigger onConnectTrigger) {
            this.onConnectTrigger = onConnectTrigger;
            return this;
        }

        public Builder onReadyTrigger(Trigger onReadyTrigger) {
            this.onReadyTrigger = onReadyTrigger;
            return this;
        }

        public Builder onCancelTrigger(Trigger onCancelTrigger) {
            this.onCancelTrigger = onCancelTrigger;
            return this;
        }

        public Builder onCloseTrigger(Trigger onCloseTrigger) {
            this.onCloseTrigger = onCloseTrigger;
            return this;
        }

        public Builder localVars(Object localVars) {
            this.localVars = localVars;
            return this;
        }

        public SkriptStreamObserver build() {
            return new SkriptStreamObserver(this);
        }
    }
}
