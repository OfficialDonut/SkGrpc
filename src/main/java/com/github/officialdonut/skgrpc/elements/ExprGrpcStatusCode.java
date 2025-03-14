package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import io.grpc.Status;
import org.jetbrains.annotations.Nullable;

@Name("gRPC Status Code")
public class ExprGrpcStatusCode extends SimplePropertyExpression<Status, Status.Code> {

    static {
        register(ExprGrpcStatusCode.class, Status.Code.class, "code", "grpcstatus");
    }


    @Override
    public @Nullable Status.Code convert(Status status) {
        return status.getCode();
    }

    @Override
    protected String getPropertyName() {
        return "code";
    }

    @Override
    public Class<? extends Status.Code> getReturnType() {
        return Status.Code.class;
    }
}
