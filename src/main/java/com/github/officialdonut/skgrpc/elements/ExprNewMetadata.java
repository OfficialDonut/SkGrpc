package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.grpc.Metadata;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("New gRPC Metadata")
public class ExprNewMetadata extends SimpleExpression<Metadata> {

    static {
        Skript.registerExpression(ExprNewMetadata.class, Metadata.class, ExpressionType.SIMPLE, "[new] [g]rpc metadata");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable Metadata[] get(Event event) {
        return new Metadata[]{new Metadata()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Metadata> getReturnType() {
        return Metadata.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "new gRPC metadata";
    }
}
