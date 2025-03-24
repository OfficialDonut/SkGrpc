package com.github.officialdonut.skgrpc.elements.client;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.events.CallCredentialsEvent;
import io.grpc.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Executor;

@Name("gRPC Call Credentials")
public class ExprCallCredentials extends SectionExpression<CallCredentials> {

    static {
        Skript.registerExpression(ExprCallCredentials.class, CallCredentials.class, ExpressionType.SIMPLE, "[:async] [[g]rpc] call credentials");
    }

    private boolean async;
    private Trigger trigger;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> list) {
        if (sectionNode == null) {
            Skript.error("Missing call credentials section.");
            return false;
        }
        async = parseResult.hasTag("async");
        trigger = loadCode(sectionNode, "call credentials", null, CallCredentialsEvent.class);
        return true;
    }

    @Override
    protected @Nullable CallCredentials[] get(Event event) {
        Object localVars = Variables.copyLocalVariables(event);
        return new CallCredentials[]{new CallCredentials() {
            @Override
            public void applyRequestMetadata(RequestInfo requestInfo, Executor executor, MetadataApplier metadataApplier) {
                CallCredentialsEvent callCredentialsEvent = new CallCredentialsEvent(requestInfo, metadataApplier);
                Variables.setLocalVariables(callCredentialsEvent, localVars);
                if (async) {
                    executor.execute(() -> trigger.execute(callCredentialsEvent));
                } else {
                    trigger.execute(callCredentialsEvent);
                }
            }
        }};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends CallCredentials> getReturnType() {
        return CallCredentials.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "gRPC call credentials";
    }
}
