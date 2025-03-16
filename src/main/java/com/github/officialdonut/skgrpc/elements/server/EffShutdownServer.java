package com.github.officialdonut.skgrpc.elements.server;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.SkGrpc;
import io.grpc.Server;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Shutdown gRPC Server")
public class EffShutdownServer extends Effect {

    static {
        Skript.registerEffect(EffShutdownServer.class, "(shutdown|close) [g]rpc server %grpcserver%");
    }

    private Expression<Server> exprServer;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprServer = (Expression<Server>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Server server = exprServer.getSingle(event);
        if (server != null) {
            SkGrpc.getInstance().getRpcManager().shutdownServer(server);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "shutdown gRPC server " + exprServer.toString(event, b);
    }
}
