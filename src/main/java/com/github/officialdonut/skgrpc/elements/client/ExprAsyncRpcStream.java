package com.github.officialdonut.skgrpc.elements.client;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.SkGrpc;
import com.github.officialdonut.skgrpc.events.GrpcCompleteEvent;
import com.github.officialdonut.skgrpc.events.GrpcErrorEvent;
import com.github.officialdonut.skgrpc.events.GrpcNextEvent;
import com.github.officialdonut.skgrpc.events.GrpcReadyEvent;
import com.github.officialdonut.skgrpc.impl.ClientRpc;
import com.github.officialdonut.skgrpc.impl.SkriptStreamObserver;
import com.github.officialdonut.skgrpc.impl.SynchronizedStreamObserver;
import com.google.protobuf.Message;
import io.grpc.Channel;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.StreamObserver;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.List;

@Name("Async gRPC Request Stream")
@SuppressWarnings({"unchecked", "rawtypes"})
public class ExprAsyncRpcStream extends SectionExpression<StreamObserver> {

    static {
        Skript.registerExpression(ExprAsyncRpcStream.class, StreamObserver.class, ExpressionType.COMBINED, "[async] [request] stream for [g]rpc %*string% using %grpcchannel%");

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
    private Expression<Channel> exprChannel;
    private ClientRpc rpc;

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> list) {
        EntryContainer entryContainer = entryValidator.validate(sectionNode);
        if (entryContainer == null) {
            return false;
        }

        onNextTrigger = loadTrigger(entryContainer, "on next", GrpcNextEvent.class);
        onErrorTrigger = loadTrigger(entryContainer, "on error", GrpcErrorEvent.class);
        onCompleteTrigger = loadTrigger(entryContainer, "on complete", GrpcCompleteEvent.class);
        onReadyTrigger = loadTrigger(entryContainer, "on ready", GrpcReadyEvent.class);
        exprChannel = (Expression<Channel>) expressions[1];

        String rpcName = ((Literal<String>) expressions[0]).getSingle();
        rpc = SkGrpc.getInstance().getRpcManager().getClientRpc(rpcName);
        if (rpc == null) {
            Skript.error("Failed to find descriptor for RPC: " + rpcName);
            return false;
        }
        if (!rpc.isClientStreaming()) {
            Skript.error("Request streams can only be used for client streaming RPCs.");
            return false;
        }

        return true;
    }

    private Trigger loadTrigger(EntryContainer entryContainer, String key, Class<? extends Event> event) {
        SectionNode sectionNode = entryContainer.getOptional(key, SectionNode.class, false);
        return sectionNode != null ? loadCode(sectionNode, key, null, event) : null;
    }

    @Override
    protected @Nullable StreamObserver[] get(Event event) {
        Channel channel = exprChannel.getSingle(event);
        if (channel == null) {
            return null;
        }

        SkriptStreamObserver observer = SkriptStreamObserver.newBuilder()
                .onNextTrigger(onNextTrigger)
                .onErrorTrigger(onErrorTrigger)
                .onCompleteTrigger(onCompleteTrigger)
                .onReadyTrigger(onReadyTrigger)
                .localVars(Variables.copyLocalVariables(event))
                .build();

        ClientCallStreamObserver<Message> callStreamObserver = (ClientCallStreamObserver<Message>) rpc.invoke(channel, observer);
        if (observer.hasOnReady()) {
            callStreamObserver.setOnReadyHandler(observer::onReady);
        }
        return new StreamObserver[]{new SynchronizedStreamObserver(callStreamObserver)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends StreamObserver> getReturnType() {
        return StreamObserver.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "async request stream for rpc " + rpc + " using " + exprChannel.toString(event, b);
    }
}
