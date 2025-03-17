package com.github.officialdonut.skgrpc.elements.server;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.SkGrpc;
import com.github.officialdonut.skgrpc.events.*;
import com.github.officialdonut.skgrpc.impl.ClientRpc;
import com.github.officialdonut.skgrpc.impl.RpcHandler;
import com.github.officialdonut.skgrpc.impl.SkriptStreamObserver;
import io.grpc.Server;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@Name("gRPC Handler")
public class SecRpcHandler extends Section {

    static {
        Skript.registerSection(SecRpcHandler.class, "[g]rpc %*string% handler for %grpcserver%");
        entryValidator = EntryValidator.builder()
                .addSection("response", true)
                .addSection("on next", true)
                .addSection("on error", true)
                .addSection("on complete", true)
                .addSection("on connect", true)
                .addSection("on ready", true)
                .addSection("on cancel", true)
                .addSection("on close", true)
                .build();
    }

    private static final EntryValidator entryValidator;

    private Trigger responseTrigger;
    private Trigger onNextTrigger;
    private Trigger onErrorTrigger;
    private Trigger onCompleteTrigger;
    private Trigger onConnectTrigger;
    private Trigger onReadyTrigger;
    private Trigger onCancelTrigger;
    private Trigger onCloseTrigger;
    private Expression<Server> exprServer;
    private ClientRpc rpc;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> list) {
        EntryContainer entryContainer = entryValidator.validate(sectionNode);
        if (entryContainer == null) {
            return false;
        }

        responseTrigger = loadTrigger(entryContainer, "response", GrpcRespondingEvent.class);
        onNextTrigger = loadTrigger(entryContainer, "on next", GrpcNextEvent.class);
        onErrorTrigger = loadTrigger(entryContainer, "on error", GrpcErrorEvent.class);
        onCompleteTrigger = loadTrigger(entryContainer, "on complete", GrpcCompleteEvent.class);
        onConnectTrigger = loadTrigger(entryContainer, "on connect", GrpcConnectEvent.class);
        onReadyTrigger = loadTrigger(entryContainer, "on ready", GrpcReadyEvent.class);
        onCancelTrigger = loadTrigger(entryContainer, "on cancel", GrpcNextEvent.class);
        onCloseTrigger = loadTrigger(entryContainer, "on close", GrpcCloseEvent.class);

        exprServer = (Expression<Server>) expressions[1];
        String rpcName = ((Literal<String>) expressions[0]).getSingle();
        rpc = SkGrpc.getInstance().getRpcManager().getClientRpc(rpcName);
        if (rpc == null) {
            Skript.error("Failed to find descriptor for RPC: " + rpcName);
            return false;
        }
        if (rpc.isClientStreaming() && responseTrigger != null) {
            Skript.error("Client streaming RPCs cannot use 'response'.");
            return false;
        }
        if (!rpc.isClientStreaming() && (onNextTrigger != null || onErrorTrigger != null || onCompleteTrigger != null || onConnectTrigger != null || onReadyTrigger != null || onCancelTrigger != null || onCloseTrigger != null)) {
            Skript.error("Only 'response' can be used for non client streaming RPCs.");
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
        Server server = exprServer.getSingle(event);
        if (server == null) {
            return super.walk(event, false);
        }

        RpcHandler handler = SkGrpc.getInstance().getRpcManager().getRpcHandler(server, rpc.getDescriptor());
        if (handler != null) {
            if (responseTrigger != null) {
                handler.setRequestObserver((request, responseStream) -> {
                    GrpcRespondingEvent respondingEvent = new GrpcRespondingEvent(request, responseStream);
                    TriggerItem.walk(responseTrigger, respondingEvent);
                });
            } else {
                handler.setStreamObserverBuilder(SkriptStreamObserver.newBuilder()
                        .onNextTrigger(onNextTrigger)
                        .onErrorTrigger(onErrorTrigger)
                        .onCompleteTrigger(onCompleteTrigger)
                        .onConnectTrigger(onConnectTrigger)
                        .onReadyTrigger(onReadyTrigger)
                        .onCancelTrigger(onCancelTrigger)
                        .onCloseTrigger(onCloseTrigger));
            }
        }

        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "rpc " + rpc + " handler for server " + exprServer.toString(event, b);
    }
}
