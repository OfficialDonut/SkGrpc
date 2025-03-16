package com.github.officialdonut.skgrpc.elements.server;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.officialdonut.skgrpc.SkGrpc;
import io.grpc.ServerCredentials;
import io.grpc.TlsServerCredentials;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@Name("TLS gRPC Server Credentials")
public class ExprTlsServerCredentials extends SimpleExpression<ServerCredentials> {

    static {
        Skript.registerExpression(ExprTlsServerCredentials.class, ServerCredentials.class, ExpressionType.COMBINED, "(tls|ssl) [g]rpc server credentials using cert[ificate] [chain] %string% [and] [private] key %string%");
    }

    private Expression<String> exprCert;
    private Expression<String> exprKey;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprCert = (Expression<String>) expressions[0];
        exprKey = (Expression<String>) expressions[1];
        return true;
    }

    @Override
    protected @Nullable ServerCredentials[] get(Event event) {
        try {
            String cert = exprCert.getSingle(event);
            String key = exprKey.getSingle(event);
            return cert != null && key != null ? new ServerCredentials[]{TlsServerCredentials.create(new File(cert), new File(key))} : null;
        } catch (IOException e) {
            SkGrpc.getInstance().getLogger().log(Level.SEVERE, "Failed to load cert and key file", e);
            return null;
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ServerCredentials> getReturnType() {
        return ServerCredentials.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "TLS gRPC server credentials using cert " + exprCert.toString(event, b) + " key " + exprKey.toString(event, b);
    }
}
