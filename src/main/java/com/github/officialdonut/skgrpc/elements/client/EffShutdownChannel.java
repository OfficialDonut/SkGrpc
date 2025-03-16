package com.github.officialdonut.skgrpc.elements.client;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import io.grpc.ManagedChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Shutdown gRPC Channel")
public class EffShutdownChannel extends Effect {

    static {
        Skript.registerEffect(EffShutdownChannel.class, "(shutdown|close) [g]rpc channel %grpcchannel%");
    }

    private Expression<ManagedChannel> exprChannel;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprChannel = (Expression<ManagedChannel>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        ManagedChannel channel = exprChannel.getSingle(event);
        if (channel != null) {
            channel.shutdownNow();
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "shutdown gRPC channel " + exprChannel.toString(event, b);
    }
}
