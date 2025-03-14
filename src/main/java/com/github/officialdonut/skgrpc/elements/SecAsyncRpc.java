package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.SkGrpc;
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
        Skript.registerSection(SecAsyncRpc.class, "%grpcchannel% [async] [g]rpc %-string% for [request] %protobufmessage%");
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
    private MethodDescriptor<Message, Message> descriptor;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> list) {
        EntryContainer entryContainer = entryValidator.validate(sectionNode);
        if (entryContainer == null) {
            return false;
        }
        onNextTrigger = loadTrigger(entryContainer, "on next", GrpcOnNextEvent.class);
        onErrorTrigger = loadTrigger(entryContainer, "on error", GrpcOnErrorEvent.class);
        onCompletedTrigger = loadTrigger(entryContainer, "on completed", GrpcOnCompletedEvent.class);

        exprChannel = (Expression<Channel>) expressions[0];
        exprRequest = (Expression<Message>) expressions[2];
        String rpcName = ((Literal<String>) expressions[1]).getSingle();
        descriptor = SkGrpc.getInstance().getRpcManager().getRpcDescriptor(rpcName);
        if (descriptor == null) {
            Skript.error("Failed to find descriptor for RPC: " + rpcName);
            return false;
        }
        return true;
    }

    private Trigger loadTrigger(EntryContainer entryContainer, String key, Class<? extends Event> event) {
        SectionNode sectionNode = entryContainer.getOptional(key, SectionNode.class, false);
        return sectionNode != null ?  loadCode(sectionNode, key, event) : null;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Object localVars = Variables.copyLocalVariables(event);
        StreamObserver<Message> responseObserver = new StreamObserver<>() {
            Object observerLocalVars = localVars;
            @Override
            public void onNext(Message message) {
                if (onNextTrigger != null) {
                    GrpcOnNextEvent onNextEvent = new GrpcOnNextEvent(message);
                    Variables.setLocalVariables(onNextEvent, observerLocalVars);
                    onNextTrigger.execute(onNextEvent);
                    observerLocalVars = Variables.copyLocalVariables(onNextEvent);
                }
            }
            @Override
            public void onError(Throwable throwable) {
                if (onErrorTrigger != null) {
                    GrpcOnErrorEvent onErrorEvent = new GrpcOnErrorEvent(throwable);
                    Variables.setLocalVariables(onErrorEvent, observerLocalVars);
                    onErrorTrigger.execute(onErrorEvent);
                }
            }
            @Override
            public void onCompleted() {
                if (onCompletedTrigger != null) {
                    GrpcOnCompletedEvent onCompletedEvent = new GrpcOnCompletedEvent();
                    Variables.setLocalVariables(onCompletedEvent, observerLocalVars);
                    onCompletedTrigger.execute(onCompletedEvent);
                }
            }
        };

        if (descriptor.getType() == MethodDescriptor.MethodType.UNARY || descriptor.getType() == MethodDescriptor.MethodType.SERVER_STREAMING) {
            SkGrpc.getInstance().getRpcManager().invokeRpc(exprChannel.getSingle(event), descriptor, exprRequest.getSingle(event), responseObserver);
        } else {
            // todo
        }

        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "";
    }
}
