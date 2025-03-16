package com.github.officialdonut.skgrpc.elements.server;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
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
public class SecNewServer extends Section {

    static {
        Skript.registerSection(SecNewServer.class, "[new] [g]rpc server %object% with services %*strings%");
        entryValidator = EntryValidator.builder()
                .addEntryData(new ExpressionEntryData<>("port", null, false, Number.class))
                .addEntryData(new ExpressionEntryData<>("credentials", null, false, ServerCredentials.class))
                .addEntryData(new ExpressionEntryData<>("max inbound message size", null, true, Number.class))
                .addEntryData(new ExpressionEntryData<>("max inbound metadata size", null, true, Number.class))
                .build();
    }

    private static final EntryValidator entryValidator;

    private Variable<?> variable;
    private List<ServiceDescriptor> services;

    private Expression<Number> exprPort;
    private Expression<ServerCredentials> exprCredentials;
    private Expression<Number> exprMaxInnboundMessageSize;
    private Expression<Number> exprMaxInnboundMetadataSize;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> list) {
        EntryContainer entryContainer = entryValidator.validate(sectionNode);
        if (entryContainer == null) {
            return false;
        }

        exprPort = entryContainer.get("port", Expression.class, false);
        exprCredentials = entryContainer.get("credentials", Expression.class, false);
        exprMaxInnboundMessageSize = entryContainer.getOptional("max inbound message size", Expression.class, false);
        exprMaxInnboundMetadataSize = entryContainer.getOptional("max inbound metadata size", Expression.class, false);

        services = new ArrayList<>();
        Literal<String> serviceNames = (Literal<String>) expressions[1];
        for (String serviceName : serviceNames.getArray()) {
            ServiceDescriptor descriptor = SkGrpc.getInstance().getRpcManager().getServiceDescriptor(serviceName);
            if (descriptor != null) {
                services.add(descriptor);
            } else {
                Skript.error("Failed to find service: " + serviceName);
            }
        }

        if (expressions[0] instanceof Variable<?> v) {
            variable = v;
        } else {
            Skript.error("Object expression must be a variable.");
            return false;
        }

        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Number port = exprPort.getSingle(event);
        ServerCredentials credentials = exprCredentials.getSingle(event);
        if (port != null && credentials != null) {
            ServerBuilder<?> builder = Grpc.newServerBuilderForPort(port.intValue(), credentials);
            if (exprMaxInnboundMessageSize != null) {
                exprMaxInnboundMessageSize.getOptionalSingle(event).ifPresent(n -> builder.maxInboundMessageSize(n.intValue()));
            }
            if (exprMaxInnboundMetadataSize != null) {
                exprMaxInnboundMetadataSize.getOptionalSingle(event).ifPresent(n -> builder.maxInboundMetadataSize(n.intValue()));
            }
            Server server = SkGrpc.getInstance().getRpcManager().createServer(builder, services);
            variable.change(event, new Object[]{server}, Changer.ChangeMode.SET);
        }
        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "new gRPC server";
    }
}
