package com.github.officialdonut.skgrpc.elements.server;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.events.ServerInterceptorEvent;
import io.grpc.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("gRPC Server Interceptor")
public class ExprServerInterceptor extends SectionExpression<ServerInterceptor> {

    static {
        Skript.registerExpression(ExprServerInterceptor.class, ServerInterceptor.class, ExpressionType.SIMPLE, "[[g]rpc] server interceptor");
    }

    private Trigger trigger;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> list) {
        if (sectionNode == null) {
            Skript.error("Missing server interceptor section.");
            return false;
        }
        trigger = loadCode(sectionNode, "interceptor", null, ServerInterceptorEvent.class);
        return true;
    }

    @Override
    protected @Nullable ServerInterceptor[] get(Event event) {
        return new ServerInterceptor[]{new ServerInterceptor() {
            @Override
            public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
                ServerInterceptorEvent interceptorEvent = new ServerInterceptorEvent(serverCall, metadata);
                trigger.execute(interceptorEvent);
                if (interceptorEvent.isCallClosed()) {
                    return new ServerCall.Listener<>(){};
                } else {
                    return interceptorEvent.hasAttachedContext() ?
                            Contexts.interceptCall(interceptorEvent.getAttachedContext(), serverCall, metadata, serverCallHandler) :
                            serverCallHandler.startCall(serverCall, metadata);
                }
            }
        }};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ServerInterceptor> getReturnType() {
        return ServerInterceptor.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "gRPC server interceptor";
    }
}
