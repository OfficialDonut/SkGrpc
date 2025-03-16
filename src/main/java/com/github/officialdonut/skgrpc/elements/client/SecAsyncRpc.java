package com.github.officialdonut.skgrpc.elements.client;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.SkGrpc;
import com.github.officialdonut.skgrpc.events.GrpcReadyEvent;
import com.github.officialdonut.skgrpc.impl.ClientRpc;
import com.github.officialdonut.skgrpc.impl.SkriptStreamObserver;
import com.github.officialdonut.skgrpc.impl.SynchronizedStreamObserver;
import com.github.officialdonut.skgrpc.events.GrpcCompleteEvent;
import com.github.officialdonut.skgrpc.events.GrpcErrorEvent;
import com.github.officialdonut.skgrpc.events.GrpcNextEvent;
import com.google.protobuf.Message;
import io.grpc.Channel;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.StreamObserver;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@Name("Async gRPC Request")
public class SecAsyncRpc extends Section {

    static {
        Skript.registerSection(SecAsyncRpc.class, "[async] [g]rpc %*string% for [request] (%-protobufmessage%|:stream %-grpcstream%) using %grpcchannel%");

        entryValidator = EntryValidator.builder()
                .addSection("on next", true)
                .addSection("on error", true)
                .addSection("on complete", true)
                .addSection("on ready", true)
                .build();
    }

    private static final EntryValidator entryValidator;

    private Trigger onNextTrigger;
    private Trigger onErrorTrigger;
    private Trigger onCompleteTrigger;
    private Trigger onReadyTrigger;
    private Expression<Message> exprRequest;
    private Expression<StreamObserver<Message>> exprRequestStream;
    private Expression<Channel> exprChannel;
    private ClientRpc rpc;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean kleenean, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> list) {
        EntryContainer entryContainer = entryValidator.validate(sectionNode);
        if (entryContainer == null) {
            return false;
        }

        onNextTrigger = loadTrigger(entryContainer, "on next", GrpcNextEvent.class);
        onErrorTrigger = loadTrigger(entryContainer, "on error", GrpcErrorEvent.class);
        onCompleteTrigger = loadTrigger(entryContainer, "on complete", GrpcCompleteEvent.class);
        onReadyTrigger = loadTrigger(entryContainer, "on ready", GrpcReadyEvent.class);

        exprChannel = (Expression<Channel>) expressions[3];
        String rpcName = ((Literal<String>) expressions[0]).getSingle();
        rpc = SkGrpc.getInstance().getRpcManager().getClientRpc(rpcName);
        if (rpc == null) {
            Skript.error("Failed to find RPC: " + rpcName);
            return false;
        }

        if (parseResult.hasTag("stream")) {
            if (!rpc.isClientStreaming()) {
                Skript.error("Request streams can only be used for client streaming RPCs.");
                return false;
            }
            exprRequestStream = (Expression<StreamObserver<Message>>) expressions[2];
        } else {
            if (rpc.isClientStreaming()) {
                Skript.error("Client streaming RPCs must use request streams.");
                return false;
            }
            if (onReadyTrigger != null) {
                Skript.error("'on ready' can only be used with client streaming RPCs.");
                return false;
            }
            exprRequest = (Expression<Message>) expressions[1];
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

        SkriptStreamObserver observer = SkriptStreamObserver.newBuilder()
                .onNextTrigger(onNextTrigger)
                .onErrorTrigger(onErrorTrigger)
                .onCompleteTrigger(onCompleteTrigger)
                .onReadyTrigger(onReadyTrigger)
                .localVars(Variables.copyLocalVariables(event))
                .build();

        if (exprRequest != null) {
            Message request = exprRequest.getSingle(event);
            if (request != null) {
                rpc.invoke(channel, request, observer);
            }
        } else {
            if (exprRequestStream.getSingle(event) instanceof SynchronizedStreamObserver requestStream) {
                ClientCallStreamObserver<Message> callStreamObserver = (ClientCallStreamObserver<Message>) rpc.invoke(channel, observer);
                requestStream.setDelegate(callStreamObserver);
                if (observer.hasOnReady()) {
                    callStreamObserver.setOnReadyHandler(observer::onReady);
                }
            } else {
                throw new IllegalStateException();
            }
        }

        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "async rpc " + rpc + " using " + exprChannel.toString(event, b);
    }
}
