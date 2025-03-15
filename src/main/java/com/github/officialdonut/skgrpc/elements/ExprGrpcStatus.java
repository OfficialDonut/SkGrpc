package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.grpc.Status;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("gRPC Status")
public class ExprGrpcStatus extends SimpleExpression<Status> {

    static {
        Skript.registerExpression(ExprGrpcStatus.class, Status.class, ExpressionType.COMBINED, "[g]rpc status %grpcstatuscode% [desc:with description %-string%]");
    }

    private Expression<Status.Code> exprStatusCode;
    private Expression<String> exprDescription;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprStatusCode = (Expression<Status.Code>) expressions[0];
        if (parseResult.hasTag("desc")) {
            exprDescription = (Expression<String>) expressions[1];
        }
        return true;
    }

    @Override
    protected @Nullable Status[] get(Event event) {
        Status.Code statusCode = exprStatusCode.getSingle(event);
        if (statusCode != null) {
            Status status = Status.fromCode(statusCode);
            if (exprDescription != null) {
                String description = exprDescription.getSingle(event);
                if (description != null) {
                    status = status.withDescription(description);
                }
            }
            return new Status[]{status};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Status> getReturnType() {
        return Status.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "gRPC status " + exprStatusCode.toString(event, b) + (exprDescription != null ? (" with description " + exprDescription.toString(event, b)) : "");
    }
}
