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

@Name("gRPC Metadata Keys")
@SuppressWarnings({"unchecked", "rawtypes"})
public class ExprMetadataKeys extends SimpleExpression<Metadata.Key> {

    static {
        Skript.registerExpression(ExprMetadataKeys.class, Metadata.Key.class, ExpressionType.COMBINED, "[all] [g]rpc metadata keys (in|of) %grpcmetadata%");
    }

    private Expression<Metadata> exprMetadata;

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprMetadata = (Expression<Metadata>) expressions[0];
        return true;
    }

    @Override
    protected @Nullable Metadata.Key[] get(Event event) {
        Metadata metadata = exprMetadata.getSingle(event);
        return metadata == null ? new Metadata.Key[0] : metadata.keys()
                .stream()
                .map(k -> Metadata.Key.of(k, Metadata.ASCII_STRING_MARSHALLER))
                .toArray(Metadata.Key[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Metadata.Key> getReturnType() {
        return Metadata.Key.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "gRPC metadata keys in " + exprMetadata.toString(event, b);
    }
}
