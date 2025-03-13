package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.SkGrpc;
import com.google.protobuf.Message;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("gRPC Response")
public class ExprRpcResponse extends SimpleExpression<Message> {

    static {
        Skript.registerExpression(ExprRpcResponse.class, Message.class, ExpressionType.COMBINED, "response[s] of %grpcchannel% [g]rpc %-string% for [request] %protobufmessage%");
    }

    private Expression<Channel> exprChannel;
    private Expression<Message> exprRequest;
    private MethodDescriptor<Message, Message> descriptor;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprChannel = (Expression<Channel>) expressions[0];
        exprRequest = (Expression<Message>) expressions[2];
        String rpcName = ((Literal<String>) expressions[1]).getSingle();
        descriptor = SkGrpc.getInstance().getRpcManager().getRpcDescriptor(rpcName);
        if (descriptor == null) {
            Skript.error("Failed to find descriptor for RPC: " + rpcName);
            return false;
        }
        if (descriptor.getType() == MethodDescriptor.MethodType.CLIENT_STREAMING || descriptor.getType() == MethodDescriptor.MethodType.BIDI_STREAMING) {
            Skript.error("Cannot use client side streaming RPC in response expression");
            return false;
        }
        return true;
    }

    @Override
    protected @Nullable Message[] get(Event event) {
        Channel channel = exprChannel.getSingle(event);
        Message request = exprRequest.getSingle(event);
        return channel != null && request != null ? SkGrpc.getInstance().getRpcManager().invokeRpc(channel, descriptor, request) : null;
    }

    @Override
    public boolean isSingle() {
        return descriptor.getType() != MethodDescriptor.MethodType.SERVER_STREAMING;
    }

    @Override
    public Class<? extends Message> getReturnType() {
        return Message.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "response of %s rpc %s for %s".formatted(exprChannel.toString(event, b), descriptor, exprRequest.toString(event, b));
    }
}
