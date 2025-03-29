package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.google.protobuf.Message;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Complete gRPC Stream")
public class EffCompleteStream extends Effect {

    static {
        Skript.registerEffect(EffCompleteStream.class, "(complete|close) [g]rpc stream %grpcstream% [error:with error %-grpcstatus%]");
    }

    private Expression<StreamObserver<Message>> exprStream;
    private Expression<Status> exprError;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprStream = (Expression<StreamObserver<Message>>) expressions[0];
        if (parseResult.hasTag("error")) {
            exprError = (Expression<Status>) expressions[1];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        StreamObserver<Message> stream = exprStream.getSingle(event);
        if (stream != null) {
            if (exprError != null) {
                stream.onError(exprError.getOptionalSingle(event).orElse(Status.UNKNOWN).asRuntimeException());
            } else {
                stream.onCompleted();
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "complete gRPC stream " + exprStream.toString(event, b) + (exprError != null ? (" with error " + exprError.toString(event, b)) : "");
    }
}
