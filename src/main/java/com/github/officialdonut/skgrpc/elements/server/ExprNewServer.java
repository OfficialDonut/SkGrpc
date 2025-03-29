package com.github.officialdonut.skgrpc.elements.server;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.SkGrpc;
import io.grpc.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.ArrayList;
import java.util.List;

@Name("New gRPC Server")
public class ExprNewServer extends SectionExpression<Server> {

    static {
        Skript.registerExpression(ExprNewServer.class, Server.class, ExpressionType.COMBINED, "[new] [g]rpc server with service[s] %*strings%");
        entryValidator = EntryValidator.builder()
                .addEntryData(new ExpressionEntryData<>("port", null, false, Number.class))
                .addEntryData(new ExpressionEntryData<>("credentials", null, false, ServerCredentials.class))
                .addEntryData(new ExpressionEntryData<>("interceptors", null, true, ServerInterceptor.class))
                .addEntryData(new ExpressionEntryData<>("max inbound message size", null, true, Number.class))
                .addEntryData(new ExpressionEntryData<>("max inbound metadata size", null, true, Number.class))
                .build();
    }

    private static final EntryValidator entryValidator;

    private List<ServiceDescriptor> services;
    private Expression<Number> exprPort;
    private Expression<ServerCredentials> exprCredentials;
    private Expression<ServerInterceptor> exprInterceptors;
    private Expression<Number> exprMaxInboundMessageSize;
    private Expression<Number> exprMaxInboundMetadataSize;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> list) {
        if (sectionNode == null) {
            Skript.error("Missing required section entries.");
            return false;
        }
        EntryContainer entryContainer = entryValidator.validate(sectionNode);
        if (entryContainer == null) {
            return false;
        }

        exprPort = entryContainer.get("port", Expression.class, false);
        exprCredentials = entryContainer.get("credentials", Expression.class, false);
        exprInterceptors = entryContainer.getOptional("interceptors", Expression.class, false);
        exprMaxInboundMessageSize = entryContainer.getOptional("max inbound message size", Expression.class, false);
        exprMaxInboundMetadataSize = entryContainer.getOptional("max inbound metadata size", Expression.class, false);

        services = new ArrayList<>();
        Literal<String> serviceNames = (Literal<String>) expressions[0];
        for (String serviceName : serviceNames.getArray()) {
            ServiceDescriptor descriptor = SkGrpc.getInstance().getRpcManager().getServiceDescriptor(serviceName);
            if (descriptor != null) {
                services.add(descriptor);
            } else {
                Skript.error("Failed to find service: " + serviceName);
            }
        }
        return true;
    }

    @Override
    protected @Nullable Server[] get(Event event) {
        Number port = exprPort.getSingle(event);
        ServerCredentials credentials = exprCredentials.getSingle(event);
        if (port != null && credentials != null) {
            ServerBuilder<?> builder = Grpc.newServerBuilderForPort(port.intValue(), credentials);
            if (exprInterceptors != null) {
                for (ServerInterceptor interceptor : exprInterceptors.getArray(event)) {
                    builder.intercept(interceptor);
                }
            }
            if (exprMaxInboundMessageSize != null) {
                exprMaxInboundMessageSize.getOptionalSingle(event).ifPresent(n -> builder.maxInboundMessageSize(n.intValue()));
            }
            if (exprMaxInboundMetadataSize != null) {
                exprMaxInboundMetadataSize.getOptionalSingle(event).ifPresent(n -> builder.maxInboundMetadataSize(n.intValue()));
            }
            Server server = SkGrpc.getInstance().getRpcManager().createServer(builder, services);
            return new Server[]{server};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Server> getReturnType() {
        return Server.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "new gRPC server with services " + services;
    }
}
