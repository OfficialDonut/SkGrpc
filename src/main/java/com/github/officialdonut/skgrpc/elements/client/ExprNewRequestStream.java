package com.github.officialdonut.skgrpc.elements.client;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.impl.SynchronizedStreamObserver;
import io.grpc.stub.StreamObserver;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("New gRPC Request Stream")
@SuppressWarnings("rawtypes")
public class ExprNewRequestStream extends SimpleExpression<StreamObserver> {

    static {
        Skript.registerExpression(ExprNewRequestStream.class, StreamObserver.class, ExpressionType.SIMPLE, "[new] [g]rpc request stream");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable StreamObserver[] get(Event event) {
        return new StreamObserver[]{new SynchronizedStreamObserver()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends StreamObserver> getReturnType() {
        return StreamObserver.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "new gRPC request stream";
    }
}
