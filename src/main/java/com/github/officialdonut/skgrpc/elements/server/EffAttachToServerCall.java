package com.github.officialdonut.skgrpc.elements.server;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.events.ServerInterceptorEvent;
import io.grpc.Context;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Attach to gRPC Server Call")
public class EffAttachToServerCall extends Effect {

    static {
        Skript.registerEffect(EffAttachToServerCall.class, "attach %grpccontext% to [intercepted] server call");
    }

    private Expression<Context> exprContext;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!getParser().isCurrentEvent(ServerInterceptorEvent.class)) {
            Skript.error("Attach to server call can only be used in a server interceptor section.");
            return false;
        }
        exprContext = (Expression<Context>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Context context = exprContext.getSingle(event);
        if (context != null) {
            ((ServerInterceptorEvent) event).attachContext(context);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "attach " + exprContext.toString(event, b) + " to server call";
    }
}
