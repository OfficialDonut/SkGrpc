package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.grpc.Context;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Current gRPC Context")
public class ExprCurrentContext extends SimpleExpression<Context> {

    static {
        Skript.registerExpression(ExprCurrentContext.class, Context.class, ExpressionType.SIMPLE, "current [g]rpc context");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable Context[] get(Event event) {
        return new Context[]{Context.current()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Context> getReturnType() {
        return Context.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "current gRPC context";
    }
}
