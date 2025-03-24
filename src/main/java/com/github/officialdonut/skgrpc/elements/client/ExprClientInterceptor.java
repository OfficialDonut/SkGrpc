package com.github.officialdonut.skgrpc.elements.client;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.events.ClientInterceptorEvent;
import io.grpc.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("gRPC Client Interceptor")
public class ExprClientInterceptor extends SectionExpression<ClientInterceptor> {

    static {
        Skript.registerExpression(ExprClientInterceptor.class, ClientInterceptor.class, ExpressionType.SIMPLE, "[[g]rpc] client interceptor");
    }

    private Trigger trigger;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> list) {
        if (sectionNode == null) {
            Skript.error("Missing client interceptor section.");
            return false;
        }
        trigger = loadCode(sectionNode, "interceptor", null, ClientInterceptorEvent.class);
        return true;
    }

    @Override
    protected @Nullable ClientInterceptor[] get(Event event) {
        return new ClientInterceptor[]{new ClientInterceptor() {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
                ClientInterceptorEvent interceptorEvent = new ClientInterceptorEvent(methodDescriptor, callOptions);
                trigger.execute(interceptorEvent);
                if (interceptorEvent.hasAttachedCallOptions()) {
                    callOptions = interceptorEvent.getAttachedCallOptions();
                }
                ClientCall<ReqT, RespT> clientCall = channel.newCall(methodDescriptor, callOptions);
                return !interceptorEvent.hasAttachedMetadata() ? clientCall : new ForwardingClientCall.SimpleForwardingClientCall<>(clientCall) {
                    @Override
                    public void start(Listener<RespT> responseListener, Metadata headers) {
                        headers.merge(interceptorEvent.getAttachedMetadata());
                        super.start(responseListener, headers);
                    }
                };
            }
        }};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ClientInterceptor> getReturnType() {
        return ClientInterceptor.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "gRPC client interceptor";
    }
}
