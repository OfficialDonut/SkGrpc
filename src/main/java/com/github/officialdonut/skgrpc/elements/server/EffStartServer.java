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

import java.io.IOException;
import java.util.logging.Level;

@Name("Start gRPC Server")
public class EffStartServer extends Effect {

    static {
        Skript.registerEffect(EffStartServer.class, "start [g]rpc server %grpcserver%");
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
            try {
                server.start();
            } catch (IOException e) {
                SkGrpc.getInstance().getLogger().log(Level.SEVERE, "Failed to start gRPC server", e);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "start gRPC server " + exprServer.toString(event, b);
    }
}
