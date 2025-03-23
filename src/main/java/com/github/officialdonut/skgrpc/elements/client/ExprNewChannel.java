package com.github.officialdonut.skgrpc.elements.client;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import io.grpc.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.List;

@Name("New gRPC Channel")
public class ExprNewChannel extends SectionExpression<ManagedChannel> {

    static {
        Skript.registerExpression(ExprNewChannel.class, ManagedChannel.class, ExpressionType.SIMPLE, "[new] [g]rpc channel");
        entryValidator = EntryValidator.builder()
                .addEntryData(new ExpressionEntryData<>("host", null, false, String.class))
                .addEntryData(new ExpressionEntryData<>("port", null, false, Number.class))
                .addEntryData(new ExpressionEntryData<>("credentials", null, true, ChannelCredentials.class))
                .addEntryData(new ExpressionEntryData<>("interceptors", null, true, ClientInterceptor.class))
                .addEntryData(new ExpressionEntryData<>("max inbound message size", null, true, Number.class))
                .addEntryData(new ExpressionEntryData<>("max inbound metadata size", null, true, Number.class))
                .build();
    }

    private static final EntryValidator entryValidator;

    private Expression<String> exprHost;
    private Expression<Number> exprPort;
    private Expression<ChannelCredentials> exprCredentials;
    private Expression<ClientInterceptor> exprInterceptors;
    private Expression<Number> exprMaxInboundMessageSize;
    private Expression<Number> exprMaxInboundMetadataSize;

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> list) {
        if (sectionNode == null) {
            Skript.error("Missing required section entries.");
            return false;
        }
        EntryContainer entryContainer = entryValidator.validate(sectionNode);
        if (entryContainer == null) {
            return false;
        }
        exprHost = entryContainer.get("host", Expression.class, false);
        exprPort = entryContainer.get("port", Expression.class, false);
        exprCredentials = entryContainer.getOptional("credentials", Expression.class, false);
        exprInterceptors = entryContainer.getOptional("interceptors", Expression.class, false);
        exprMaxInboundMessageSize = entryContainer.getOptional("max inbound message size", Expression.class, false);
        exprMaxInboundMetadataSize = entryContainer.getOptional("max inbound metadata size", Expression.class, false);
        return true;
    }

    @Override
    protected @Nullable ManagedChannel[] get(Event event) {
        String host = exprHost.getSingle(event);
        Number port = exprPort.getSingle(event);
        if (host != null && port != null) {
            ChannelCredentials credentials = exprCredentials != null ? exprCredentials.getOptionalSingle(event).orElse(TlsChannelCredentials.create()) : TlsChannelCredentials.create();
            ManagedChannelBuilder<?> builder = Grpc.newChannelBuilderForAddress(host, port.intValue(), credentials);
            if (exprInterceptors != null) {
                builder.intercept(exprInterceptors.getArray(event));
            }
            if (exprMaxInboundMessageSize != null) {
                exprMaxInboundMessageSize.getOptionalSingle(event).ifPresent(n -> builder.maxInboundMessageSize(n.intValue()));
            }
            if (exprMaxInboundMetadataSize != null) {
                exprMaxInboundMetadataSize.getOptionalSingle(event).ifPresent(n -> builder.maxInboundMetadataSize(n.intValue()));
            }
            return new ManagedChannel[]{builder.build()};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ManagedChannel> getReturnType() {
        return ManagedChannel.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "new gRPC channel";
    }
}
