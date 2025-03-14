package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.SkGrpc;
import com.github.officialdonut.skgrpc.StreamObserverWrapper;
import com.github.officialdonut.skgrpc.events.GrpcOnCompletedEvent;
import com.github.officialdonut.skgrpc.events.GrpcOnErrorEvent;
import com.github.officialdonut.skgrpc.events.GrpcOnNextEvent;
import com.google.protobuf.Message;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import io.grpc.stub.StreamObserver;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@Name("Async gRPC Request")
public class SecAsyncRpc extends Section {

    static {
        Skript.registerSection(SecAsyncRpc.class, "%grpcchannel% [async] [g]rpc %*string% for [request] %protobufmessage%", "%grpcchannel% [async] [g]rpc %*string% for [request] stream %grpcrequeststream%");
        entryValidator = EntryValidator.builder()
                .addSection("on next", true)
                .addSection("on error", true)
                .addSection("on completed", true)
                .build();
    }

    private static final EntryValidator entryValidator;

    private Trigger onNextTrigger;
    private Trigger onErrorTrigger;
    private Trigger onCompletedTrigger;
    private Expression<Channel> exprChannel;
    private Expression<Message> exprRequest;
    private Expression<StreamObserverWrapper> exprRequestStream;
    private MethodDescriptor<Message, Message> descriptor;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean kleenean, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> list) {
        EntryContainer entryContainer = entryValidator.validate(sectionNode);
        if (entryContainer == null) {
            return false;
        }
        onNextTrigger = loadTrigger(entryContainer, "on next", GrpcOnNextEvent.class);
        onErrorTrigger = loadTrigger(entryContainer, "on error", GrpcOnErrorEvent.class);
        onCompletedTrigger = loadTrigger(entryContainer, "on completed", GrpcOnCompletedEvent.class);

        exprChannel = (Expression<Channel>) expressions[0];
        String rpcName = ((Literal<String>) expressions[1]).getSingle();
        descriptor = SkGrpc.getInstance().getRpcManager().getRpcDescriptor(rpcName);
        if (descriptor == null) {
            Skript.error("Failed to find descriptor for RPC: " + rpcName);
            return false;
        }

        if (matchedPattern == 0) {
            exprRequest = (Expression<Message>) expressions[2];
            if (descriptor.getType() == MethodDescriptor.MethodType.CLIENT_STREAMING || descriptor.getType() == MethodDescriptor.MethodType.BIDI_STREAMING) {
                Skript.error("Client side streaming RPCs must use async request stream.");
            }
        } else {
            exprRequestStream = (Expression<StreamObserverWrapper>) expressions[2];
            if (descriptor.getType() == MethodDescriptor.MethodType.UNARY || descriptor.getType() == MethodDescriptor.MethodType.SERVER_STREAMING) {
                Skript.error("Async request stream can only be used for client side streaming RPCs.");
            }
        }

        return true;
    }

    private Trigger loadTrigger(EntryContainer entryContainer, String key, Class<? extends Event> event) {
        SectionNode sectionNode = entryContainer.getOptional(key, SectionNode.class, false);
        return sectionNode != null ?  loadCode(sectionNode, key, event) : null;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Channel channel = exprChannel.getSingle(event);
        if (channel == null) {
            return super.walk(event, false);
        }

        Object localVars = Variables.copyLocalVariables(event);
        StreamObserver<Message> responseObserver = new StreamObserver<>() {
            Object observerLocalVars = localVars;
            @Override
            public void onNext(Message message) {
                if (onNextTrigger != null) {
                    GrpcOnNextEvent onNextEvent = new GrpcOnNextEvent(message);
                    Variables.setLocalVariables(onNextEvent, observerLocalVars);
                    TriggerItem.walk(onNextTrigger, onNextEvent);
                    observerLocalVars = Variables.copyLocalVariables(onNextEvent);
                }
            }
            @Override
            public void onError(Throwable throwable) {
                if (onErrorTrigger != null) {
                    GrpcOnErrorEvent onErrorEvent = new GrpcOnErrorEvent(throwable);
                    Variables.setLocalVariables(onErrorEvent, observerLocalVars);
                    TriggerItem.walk(onErrorTrigger, onErrorEvent);
                }
            }
            @Override
            public void onCompleted() {
                if (onCompletedTrigger != null) {
                    GrpcOnCompletedEvent onCompletedEvent = new GrpcOnCompletedEvent();
                    Variables.setLocalVariables(onCompletedEvent, observerLocalVars);
                    TriggerItem.walk(onCompletedTrigger, onCompletedEvent);
                }
            }
        };

        if (exprRequest != null) {
            Message request = exprRequest.getSingle(event);
            if (request != null) {
                SkGrpc.getInstance().getRpcManager().invokeRpc(channel, descriptor, request, responseObserver);
            }
        } else {
            StreamObserverWrapper requestStream = exprRequestStream.getSingle(event);
            if (requestStream != null) {
                requestStream.setDelegate(SkGrpc.getInstance().getRpcManager().invokeRpc(channel, descriptor, responseObserver));
            }
        }

        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return exprRequest != null ?
                "%s async rpc %s for request %s".formatted(exprChannel.toString(event, b), descriptor, exprRequest.toString(event, b)) :
                "%s async rpc %s for request stream %s".formatted(exprChannel.toString(event, b), descriptor, exprRequestStream.toString(event, b));
    }
}
