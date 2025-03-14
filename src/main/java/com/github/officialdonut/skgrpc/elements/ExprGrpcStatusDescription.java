package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import io.grpc.Status;
import org.jetbrains.annotations.Nullable;

@Name("gRPC Status Description")
public class ExprGrpcStatusDescription extends SimplePropertyExpression<Status, String> {

    static {
        register(ExprGrpcStatusDescription.class, String.class, "description", "grpcstatus");
    }

    @Override
    public @Nullable String convert(Status status) {
        return status.getDescription();
    }

    @Override
    protected String getPropertyName() {
        return "description";
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }
}
