package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Send gRPC Stream Message")
public class EffSendStreamMessage extends Effect {

    static {
        Skript.registerEffect(EffSendStreamMessage.class, "send %protobufmessages% (on|to) [g]rpc stream %grpcstream%");
    }

    private Expression<Message> exprMessage;
    private Expression<StreamObserver<Message>> exprStream;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprMessage = (Expression<Message>) expressions[0];
        exprStream = (Expression<StreamObserver<Message>>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Message[] requests = exprMessage.getArray(event);
        StreamObserver<Message> stream = exprStream.getSingle(event);
        if (stream != null) {
            for (Message request : requests) {
                stream.onNext(request);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "send " + exprMessage.toString(event, b) + " on gRPC stream " + exprStream.toString(event, b);
    }
}
