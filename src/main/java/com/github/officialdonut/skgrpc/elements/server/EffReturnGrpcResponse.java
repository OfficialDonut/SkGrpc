package com.github.officialdonut.skgrpc.elements.server;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.events.GrpcRespondingEvent;
import com.google.protobuf.Message;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Return gRPC Response")
public class EffReturnGrpcResponse extends Effect {

    static {
        Skript.registerEffect(EffReturnGrpcResponse.class, "return [g]rpc response[s] %protobufmessages%");
    }

    private Expression<Message> exprResponse;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!getParser().isCurrentEvent(GrpcRespondingEvent.class)) {
            Skript.error("Return gRPC response can only be used in a RPC handler response section.");
            return false;
        }
        exprResponse = (Expression<Message>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        GrpcRespondingEvent respondingEvent = (GrpcRespondingEvent) event;
        try {
            Message[] responses = exprResponse.getArray(event);
            for (Message response : responses) {
                respondingEvent.getOutgoingStream().onNext(response);
            }
        } finally {
            respondingEvent.getOutgoingStream().onCompleted();
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "return gRPC response " + exprResponse.toString(event, b);
    }
}
