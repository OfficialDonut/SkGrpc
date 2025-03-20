package com.github.officialdonut.skgrpc.elements.client;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.SkGrpc;
import com.github.officialdonut.skgrpc.impl.ClientRpc;
import com.github.officialdonut.skgrpc.impl.SkriptStreamObserver;
import com.github.officialdonut.skgrpc.events.GrpcCompleteEvent;
import com.github.officialdonut.skgrpc.events.GrpcErrorEvent;
import com.github.officialdonut.skgrpc.events.GrpcNextEvent;
import com.google.protobuf.Message;
import io.grpc.CallOptions;
import io.grpc.Channel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@Name("Async gRPC Request")
public class SecAsyncRpc extends Section {

    static {
        Skript.registerSection(SecAsyncRpc.class, "[async] [g]rpc %*string% for [request] %protobufmessage% using %grpcchannel% [with %-grpccalloptions%]");

        entryValidator = EntryValidator.builder()
                .addSection("on next", true)
                .addSection("on error", true)
                .addSection("on complete", true)
                .build();
    }

    private static final EntryValidator entryValidator;

    private Trigger onNextTrigger;
    private Trigger onErrorTrigger;
    private Trigger onCompleteTrigger;
    private Expression<Message> exprRequest;
    private Expression<Channel> exprChannel;
    private Expression<CallOptions> exprCallOptions;
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

        exprRequest = (Expression<Message>) expressions[1];
        exprChannel = (Expression<Channel>) expressions[2];
        exprCallOptions = (Expression<CallOptions>) expressions[3];

        String rpcName = ((Literal<String>) expressions[0]).getSingle();
        rpc = SkGrpc.getInstance().getRpcManager().getClientRpc(rpcName);
        if (rpc == null) {
            Skript.error("Failed to find descriptor for RPC: " + rpcName);
            return false;
        }
        if (rpc.isClientStreaming()) {
            Skript.error("Client streaming RPCs must use async request streams.");
            return false;
        }

        return true;
    }

    private Trigger loadTrigger(EntryContainer entryContainer, String key, Class<? extends Event> event) {
        SectionNode sectionNode = entryContainer.getOptional(key, SectionNode.class, false);
        return sectionNode != null ? loadCode(sectionNode, key, event) : null;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Channel channel = exprChannel.getSingle(event);
        if (channel != null) {
            Message request = exprRequest.getSingle(event);
            if (request != null) {
                CallOptions callOptions = exprCallOptions != null ? exprCallOptions.getOptionalSingle(event).orElse(CallOptions.DEFAULT) : CallOptions.DEFAULT;
                rpc.invoke(channel, request, callOptions, SkriptStreamObserver.newBuilder()
                        .onNextTrigger(onNextTrigger)
                        .onErrorTrigger(onErrorTrigger)
                        .onCompleteTrigger(onCompleteTrigger)
                        .localVars(Variables.copyLocalVariables(event))
                        .build());
            }
        }
        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "async rpc %s for %s using %s".formatted(rpc, exprRequest.toString(event, b), exprChannel.toString(event, b));
    }
}
