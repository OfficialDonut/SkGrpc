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

@Name("New gRPC Context Key")
@SuppressWarnings({"unchecked", "rawtypes"})
public class ExprNewContextKey extends SimpleExpression<Context.Key> {

    static {
        Skript.registerExpression(ExprNewContextKey.class, Context.Key.class, ExpressionType.COMBINED, "[new] [g]rpc context key %string% [with default [value] %-object%]");
    }

    private Expression<String> exprName;
    private Expression<Object> exprDefault;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprName = (Expression<String>) expressions[0];
        exprDefault = (Expression<Object>) expressions[1];
        return true;
    }

    @Override
    protected @Nullable Context.Key[] get(Event event) {
        String name = exprName.getSingle(event);
        if (name == null) {
            return null;
        }
        return new Context.Key[]{exprDefault != null ? Context.keyWithDefault(name, exprDefault.getSingle(event)) : Context.key(name)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Context.Key> getReturnType() {
        return Context.Key.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "new gRPC context key " + exprName.toString(event, b);
    }
}
