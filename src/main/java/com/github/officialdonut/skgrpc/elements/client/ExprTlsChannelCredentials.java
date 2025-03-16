package com.github.officialdonut.skgrpc.elements.client;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.SkGrpc;
import io.grpc.ChannelCredentials;
import io.grpc.TlsChannelCredentials;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@Name("TLS gRPC Channel Credentials")
public class ExprTlsChannelCredentials extends SimpleExpression<ChannelCredentials> {

    static {
        Skript.registerExpression(ExprTlsChannelCredentials.class, ChannelCredentials.class, ExpressionType.COMBINED, "(tls|ssl) [g]rpc channel credentials [cert:trusting [root] cert[ificate] %-string%]");
    }

    private Expression<String> exprRootCert;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (parseResult.hasTag("cert")) {
            exprRootCert = (Expression<String>) expressions[0];
        }
        return true;
    }

    @Override
    protected @Nullable ChannelCredentials[] get(Event event) {
        if (exprRootCert != null) {
            String rootCert = exprRootCert.getSingle(event);
            if (rootCert != null) {
                try {
                    return new ChannelCredentials[]{TlsChannelCredentials.newBuilder().trustManager(new File(rootCert)).build()};
                } catch (IOException e) {
                    SkGrpc.getInstance().getLogger().log(Level.SEVERE, "Failed to load root cert file", e);
                }
            }
        }
        return new ChannelCredentials[]{TlsChannelCredentials.create()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ChannelCredentials> getReturnType() {
        return ChannelCredentials.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "TLS gRPC channel credentials";
    }
}
