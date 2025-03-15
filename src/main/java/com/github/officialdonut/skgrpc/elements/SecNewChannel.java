package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
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
public class SecNewChannel extends Section {

    static {
        Skript.registerSection(SecNewChannel.class, "[open] [new] [g]rpc channel %object%");
        entryValidator = EntryValidator.builder()
                .addEntryData(new ExpressionEntryData<>("host", null, false, String.class))
                .addEntryData(new ExpressionEntryData<>("port", null, false, Number.class))
                .addEntryData(new ExpressionEntryData<>("credentials", null, true, ChannelCredentials.class))
                .build();
    }

    private static final EntryValidator entryValidator;

    private Variable<?> variable;
    private Expression<String> exprHost;
    private Expression<Number> exprPort;
    private Expression<ChannelCredentials> exprCredentials;

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> list) {
        EntryContainer entryContainer = entryValidator.validate(sectionNode);
        if (entryContainer == null) {
            return false;
        }
        exprHost = entryContainer.get("host", Expression.class, false);
        exprPort = entryContainer.get("port", Expression.class, false);
        exprCredentials = entryContainer.getOptional("credentials", Expression.class, false);

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
        String host = exprHost.getSingle(event);
        Number port = exprPort.getSingle(event);
        if (host != null && port != null) {
            ChannelCredentials credentials = exprCredentials != null ? exprCredentials.getOptionalSingle(event).orElse(TlsChannelCredentials.create()) : TlsChannelCredentials.create();
            ManagedChannelBuilder<?> builder = Grpc.newChannelBuilderForAddress(host, port.intValue(), credentials);
            variable.change(event, new Object[]{builder.build()}, Changer.ChangeMode.SET);
        }
        return super.walk(event, false);
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "new gRPC channel";
    }
}
