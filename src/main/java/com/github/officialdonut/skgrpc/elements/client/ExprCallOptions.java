package com.github.officialdonut.skgrpc.elements.client;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import io.grpc.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Name("gRPC Call Options")
public class ExprCallOptions extends SectionExpression<CallOptions> {

    static {
        Skript.registerExpression(ExprCallOptions.class, CallOptions.class, ExpressionType.SIMPLE, "[[g]rpc] call options");
        entryValidator = EntryValidator.builder()
                .addEntryData(new ExpressionEntryData<>("authority", null, true, String.class))
                .addEntryData(new ExpressionEntryData<>("call credentials", null, true, CallCredentials.class))
                .addEntryData(new ExpressionEntryData<>("compression", null, true, String.class))
                .addEntryData(new ExpressionEntryData<>("deadline", null, true, Timespan.class))
                .addEntryData(new ExpressionEntryData<>("max inbound message size", null, true, Number.class))
                .addEntryData(new ExpressionEntryData<>("max outbound message size", null, true, Number.class))
                .addEntryData(new ExpressionEntryData<>("ready threshold", null, true, Number.class))
                .addEntryData(new ExpressionEntryData<>("wait for ready", null, true, Boolean.class))
                .build();
    }

    private static final EntryValidator entryValidator;

    private Expression<String> exprAuthority;
    private Expression<CallCredentials> exprCallCredentials;
    private Expression<String> exprCompression;
    private Expression<Timespan> exprDeadline;
    private Expression<Number> exprMaxInboundMessageSize;
    private Expression<Number> exprMaxOutboundMessageSize;
    private Expression<Number> exprReadyThreshold;
    private Expression<Boolean> exprWaitForReady;

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> list) {
        EntryContainer entryContainer = entryValidator.validate(sectionNode);
        if (entryContainer == null) {
            return false;
        }
        exprAuthority = entryContainer.getOptional("authority", Expression.class, false);
        exprCallCredentials = entryContainer.getOptional("call credentials", Expression.class, false);
        exprCompression = entryContainer.getOptional("compression", Expression.class, false);
        exprDeadline = entryContainer.getOptional("deadline", Expression.class, false);
        exprMaxInboundMessageSize = entryContainer.getOptional("max inbound message size", Expression.class, false);
        exprMaxOutboundMessageSize = entryContainer.getOptional("max outbound message size", Expression.class, false);
        exprReadyThreshold = entryContainer.getOptional("ready threshold", Expression.class, false);
        exprWaitForReady = entryContainer.getOptional("wait for ready", Expression.class, false);
        return true;
    }

    @Override
    protected @Nullable CallOptions[] get(Event event) {
        AtomicReference<CallOptions> callOptions = new AtomicReference<>(CallOptions.DEFAULT);
        if (exprAuthority != null) {
            exprAuthority.getOptionalSingle(event).ifPresent(s -> callOptions.set(callOptions.get().withAuthority(s)));
        }
        if (exprCallCredentials != null) {
            exprCallCredentials.getOptionalSingle(event).ifPresent(c -> callOptions.set(callOptions.get().withCallCredentials(c)));
        }
        if (exprCompression != null) {
            exprCompression.getOptionalSingle(event).ifPresent(s -> callOptions.set(callOptions.get().withCompression(s)));
        }
        if (exprDeadline != null) {
            exprDeadline.getOptionalSingle(event).ifPresent(t -> callOptions.set(callOptions.get().withDeadlineAfter(t.getDuration())));
        }
        if (exprMaxInboundMessageSize != null) {
            exprMaxInboundMessageSize.getOptionalSingle(event).ifPresent(n -> callOptions.set(callOptions.get().withMaxInboundMessageSize(n.intValue())));
        }
        if (exprMaxOutboundMessageSize != null) {
            exprMaxOutboundMessageSize.getOptionalSingle(event).ifPresent(n -> callOptions.set(callOptions.get().withMaxOutboundMessageSize(n.intValue())));
        }
        if (exprReadyThreshold != null) {
            exprReadyThreshold.getOptionalSingle(event).ifPresent(n -> callOptions.set(callOptions.get().withOnReadyThreshold(n.intValue())));
        }
        if (exprWaitForReady != null) {
            exprWaitForReady.getOptionalSingle(event).ifPresent(b -> callOptions.set(b ? callOptions.get().withWaitForReady() : callOptions.get().withoutWaitForReady()));
        }
        return new CallOptions[]{callOptions.get()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends CallOptions> getReturnType() {
        return CallOptions.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "gRPC call options";
    }
}
