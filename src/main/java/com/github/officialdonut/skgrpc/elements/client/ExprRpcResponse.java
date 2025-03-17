package com.github.officialdonut.skgrpc.elements.client;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.SkGrpc;
import com.github.officialdonut.skgrpc.impl.ClientRpc;
import com.google.protobuf.Message;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

@Name("gRPC Response")
public class ExprRpcResponse extends SimpleExpression<Message> {

    static {
        Skript.registerExpression(ExprRpcResponse.class, Message.class, ExpressionType.COMBINED, "response[s] of [g]rpc %*string% for [request] %protobufmessage% using %grpcchannel%");
    }

    private Expression<Message> exprRequest;
    private Expression<Channel> exprChannel;
    private ClientRpc rpc;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprRequest = (Expression<Message>) expressions[1];
        exprChannel = (Expression<Channel>) expressions[2];
        String rpcName = ((Literal<String>) expressions[0]).getSingle();
        rpc = SkGrpc.getInstance().getRpcManager().getClientRpc(rpcName);
        if (rpc == null) {
            Skript.error("Failed to find descriptor for RPC: " + rpcName);
            return false;
        }
        if (rpc.isClientStreaming()) {
            Skript.error("Client streaming RPCs must use async request stream.");
            return false;
        }
        return true;
    }

    @Override
    protected @Nullable Message[] get(Event event) {
        try {
            Message request = exprRequest.getSingle(event);
            Channel channel = exprChannel.getSingle(event);
            return channel != null && request != null ? rpc.invoke(channel, request) : null;
        } catch (StatusRuntimeException e) {
            SkGrpc.getInstance().getLogger().log(Level.WARNING, "gRPC request failed", e);
            return null;
        }
    }

    @Override
    public boolean isSingle() {
        return !rpc.isServerStreaming();
    }

    @Override
    public Class<? extends Message> getReturnType() {
        return Message.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "response of rpc %s for %s using %s".formatted(rpc, exprRequest.toString(event, b), exprChannel.toString(event, b));
    }
}
