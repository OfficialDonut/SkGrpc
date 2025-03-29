package com.github.officialdonut.skgrpc.elements.server;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.events.ServerInterceptorEvent;
import io.grpc.Metadata;
import io.grpc.Status;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Close gRPC Server Call")
public class EffCloseServerCall extends Effect {

    static {
        Skript.registerEffect(EffCloseServerCall.class, "close [intercepted] server call with [status] %grpcstatus% and [trailers] %grpcmetadata%");
    }

    private Expression<Status> exprStatus;
    private Expression<Metadata> exprMetadata;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!getParser().isCurrentEvent(ServerInterceptorEvent.class)) {
            Skript.error("Close server call can only be used in a server interceptor section.");
            return false;
        }
        exprStatus = (Expression<Status>) expressions[0];
        exprMetadata = (Expression<Metadata>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        ((ServerInterceptorEvent) event).closeCall(exprStatus.getOptionalSingle(event).orElse(Status.UNKNOWN), exprMetadata.getOptionalSingle(event).orElse(new Metadata()));
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "close server call with " + exprStatus.toString(event, b) + " and " + exprMetadata.toString(event, b);
    }
}
