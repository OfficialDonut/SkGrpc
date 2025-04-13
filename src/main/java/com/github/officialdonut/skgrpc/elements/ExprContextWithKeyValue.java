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

@Name("gRPC Context With Key Value")
public class ExprContextWithKeyValue extends SimpleExpression<Context> {

    static {
        Skript.registerExpression(ExprContextWithKeyValue.class, Context.class, ExpressionType.COMBINED, "[new] g[rpc] context from %grpccontext% with %grpccontextkey% set to %object%");
    }

    private Expression<Context> exprContext;
    private Expression<Context.Key<String>> exprKey;
    private Expression<Object> exprValue;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprContext = (Expression<Context>) expressions[0];
        exprKey = (Expression<Context.Key<String>>) expressions[1];
        exprValue = (Expression<Object>) expressions[2];
        return true;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected @Nullable Context[] get(Event event) {
        Context context = exprContext.getSingle(event);
        Context.Key key = exprKey.getSingle(event);
        Object value = exprValue.getSingle(event);
        return context != null && key != null && value != null ? new Context[]{context.withValue(key, value)} : null;
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
        return "gRPC context from " + exprContext.toString(event, b) + " with " + exprKey.toString(event, b) + " set to " + exprValue.toString(event, b);
    }
}
