package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.StreamObserverWrapper;
import com.google.protobuf.Message;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Send gRPC Request")
public class EffSendRequest extends Effect {

    static {
        Skript.registerEffect(EffSendRequest.class, "send %protobufmessages% to [g]rpc [request] stream %grpcrequeststream% ");
    }

    private Expression<Message> exprRequest;
    private Expression<StreamObserverWrapper> exprRequestStream;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprRequest = (Expression<Message>) expressions[0];
        exprRequestStream = (Expression<StreamObserverWrapper>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Message[] requests = exprRequest.getArray(event);
        StreamObserverWrapper requestStream = exprRequestStream.getSingle(event);
        if (requestStream != null) {
            for (Message request : requests) {
                requestStream.onNext(request);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "send rpc request " + exprRequest.toString(event, b) + " " + exprRequestStream.toString(event, b);
    }
}
