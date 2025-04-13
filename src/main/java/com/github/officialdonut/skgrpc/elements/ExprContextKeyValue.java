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

@Name("gRPC Context Key Value")
public class ExprContextKeyValue extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprContextKeyValue.class, Object.class, ExpressionType.COMBINED, "value of [g]rpc context key %grpccontextkey% [in %-grpccontext%]");
    }

    private Expression<Context.Key<String>> exprKey;
    private Expression<Context> exprContext;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprKey = (Expression<Context.Key<String>>) expressions[0];
        exprContext = (Expression<Context>) expressions[1];
        return true;
    }

    @Override
    protected @Nullable Object[] get(Event event) {
        Context.Key<?> key = exprKey.getSingle(event);
        if (key != null) {
            if (exprContext == null) {
                return new Object[]{key.get()};
            }
            Context context = exprContext.getSingle(event);
            if (context != null) {
                return new Object[]{key.get(context)};
            }
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "value of gRPC context key " + exprKey.toString(event, b);
    }
}
