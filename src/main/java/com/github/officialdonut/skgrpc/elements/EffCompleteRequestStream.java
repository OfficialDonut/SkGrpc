package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.StreamObserverWrapper;
import io.grpc.Status;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Complete gRPC Request Stream")
public class EffCompleteRequestStream extends Effect {

    static {
        Skript.registerEffect(EffCompleteRequestStream.class, "(complete|close) [g]rpc [request] stream %grpcrequeststream% [error:with error %-grpcstatus%]");
    }

    private Expression<StreamObserverWrapper> exprRequestStream;
    private Expression<Status> exprError;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprRequestStream = (Expression<StreamObserverWrapper>) expressions[0];
        if (parseResult.hasTag("error")) {
            exprError = (Expression<Status>) expressions[1];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        StreamObserverWrapper requestStream = exprRequestStream.getSingle(event);
        if (requestStream != null) {
            if (exprError != null) {
                Status status = exprError.getSingle(event);
                requestStream.onError(status != null ? status.asException() : Status.INTERNAL.asException());
            } else {
                requestStream.onCompleted();
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "complete gRPC request stream " + exprRequestStream.toString(event, b) + (exprError != null ? (" with error " + exprError.toString(event, b)) : "");
    }
}
