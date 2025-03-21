package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import io.grpc.Metadata;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("gRPC Metadata Key Is Set")
public class CondMetadataKeyIsSet extends Condition {

    static {
        Skript.registerCondition(CondMetadataKeyIsSet.class,
                "[g]rpc metadata key[s] %grpcmetadatakeys% (is|are) set in %grpcmetadata%",
                "[g]rpc metadata key[s] %grpcmetadatakeys% (isn't|is not|aren't|are not) set in %grpcmetadata%");
    }

    private Expression<Metadata.Key<String>> exprKey;
    private Expression<Metadata> exprMetadata;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprKey = (Expression<Metadata.Key<String>>) expressions[0];
        exprMetadata = (Expression<Metadata>) expressions[1];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        return exprMetadata.check(event, message -> exprKey.check(event, message::containsKey), isNegated());
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return PropertyCondition.toString(this, PropertyCondition.PropertyType.BE, event, b, exprKey, "set in " + exprMetadata.toString(event, b));
    }
}
