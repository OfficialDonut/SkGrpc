package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.StreamObserverWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("New gRPC Request Stream")
public class ExprNewRequestStream extends SimpleExpression<StreamObserverWrapper> {

    static {
        Skript.registerExpression(ExprNewRequestStream.class, StreamObserverWrapper.class, ExpressionType.SIMPLE, "[new] [g]rpc request stream");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable StreamObserverWrapper[] get(Event event) {
        return new StreamObserverWrapper[]{new StreamObserverWrapper()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends StreamObserverWrapper> getReturnType() {
        return StreamObserverWrapper.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "new gRPC request stream";
    }
}
