package com.github.officialdonut.skgrpc.elements.server;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.grpc.InsecureServerCredentials;
import io.grpc.ServerCredentials;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Insecure gRPC Server Credentials")
public class ExprInsecureServerCredentials extends SimpleExpression<ServerCredentials> {

    static {
        Skript.registerExpression(ExprInsecureServerCredentials.class, ServerCredentials.class, ExpressionType.SIMPLE, "insecure [[g]rpc] server credentials");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        Skript.warning("Insecure gRPC server credentials should only be used for local testing.");
        return true;
    }

    @Override
    protected @Nullable ServerCredentials[] get(Event event) {
        return new ServerCredentials[]{InsecureServerCredentials.create()};
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
        return "insecure gRPC server credentials";
    }
}
