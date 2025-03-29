package com.github.officialdonut.skgrpc.elements.client;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.events.ClientInterceptorEvent;
import io.grpc.CallOptions;
import io.grpc.Metadata;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Attach to gRPC Client Call")
public class EffAttachToClientCall extends Effect {

    static {
        Skript.registerEffect(EffAttachToClientCall.class, "attach %grpcmetadata/grpccalloptions% to [intercepted] client call");
    }

    private Expression<Object> exprAttachment;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!getParser().isCurrentEvent(ClientInterceptorEvent.class)) {
            Skript.error("Attach to client call can only be used in a client interceptor section.");
            return false;
        }
        exprAttachment = (Expression<Object>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Object attachment = exprAttachment.getSingle(event);
        if (attachment instanceof Metadata metadata) {
            ((ClientInterceptorEvent) event).attachMetadata(metadata);
        } else if (attachment instanceof CallOptions callOptions) {
            ((ClientInterceptorEvent) event).attachCallOptions(callOptions);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "attach " + exprAttachment.toString(event, b) + " to client call";
    }
}
