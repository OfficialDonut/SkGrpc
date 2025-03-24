package com.github.officialdonut.skgrpc.elements.client;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.events.CallCredentialsEvent;
import io.grpc.Metadata;
import io.grpc.Status;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("gRPC Return Call Credentials Result")
public class EffReturnCallCredentialsResult extends Effect {

    static {
        Skript.registerEffect(EffReturnCallCredentialsResult.class, "return [[g]rpc] call credentials result %grpcmetadata/grpcstatus%");
    }

    private Expression<Object> exprResult;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!getParser().isCurrentEvent(CallCredentialsEvent.class)) {
            Skript.error("Return call credentials result can only be used in a call credentials section.");
            return false;
        }
        exprResult = (Expression<Object>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Object result = exprResult.getSingle(event);
        if (result instanceof Metadata metadata) {
            ((CallCredentialsEvent) event).getMetadataApplier().apply(metadata);
        } else if (result instanceof Status status) {
            ((CallCredentialsEvent) event).getMetadataApplier().fail(status);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "return gRPC call credentials result " + exprResult.toString(event, b);
    }
}
