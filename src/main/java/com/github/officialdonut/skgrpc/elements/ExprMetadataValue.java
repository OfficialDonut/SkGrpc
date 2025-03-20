package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.google.common.collect.Iterables;
import io.grpc.Metadata;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("gRPC Metadata Value")
public class ExprMetadataValue extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprMetadataValue.class, String.class, ExpressionType.COMBINED, "[g]rpc metadata value[:s] of %grpcmetadatakey% in %grpcmetadata%");
    }

    private Expression<Metadata.Key<String>> exprKey;
    private Expression<Metadata> exprMetadata;
    private boolean isSingle;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprKey = (Expression<Metadata.Key<String>>) expressions[0];
        exprMetadata = (Expression<Metadata>) expressions[1];
        isSingle = !parseResult.hasTag("s");
        return true;
    }

    @Override
    protected @Nullable String[] get(Event event) {
        Metadata.Key<String> key = exprKey.getSingle(event);
        Metadata metadata = exprMetadata.getSingle(event);
        if (key == null || metadata == null) {
            return isSingle ? null : new String[0];
        }
        if (isSingle) {
            String value = metadata.get(key);
            return value != null ? new String[]{value} : null;
        }
        Iterable<String> values = metadata.getAll(key);
        return values != null ? Iterables.toArray(values, String.class) : new String[0];
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, Changer.ChangeMode mode) {
        Metadata.Key<String> key = exprKey.getSingle(event);
        Metadata metadata = exprMetadata.getSingle(event);
        if (key == null || metadata == null) {
            return;
        }
        if (mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.SET) {
            metadata.discardAll(key);
        }
        for (Object object : delta) {
            if (object instanceof String s) {
                switch (mode) {
                    case SET, ADD -> metadata.put(key, s);
                    case REMOVE, REMOVE_ALL -> metadata.remove(key, s);
                }
            }
        }
    }

    @Override
    public @Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return switch (mode) {
            case SET, DELETE -> new Class<?>[]{String.class};
            case ADD, REMOVE, REMOVE_ALL -> !isSingle ? new Class<?>[]{String.class} : null;
            default -> null;
        };
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "gRPC metadata value(s) of " + exprKey.toString(event, b) + " in " + exprMetadata.toString(event, b);
    }
}
