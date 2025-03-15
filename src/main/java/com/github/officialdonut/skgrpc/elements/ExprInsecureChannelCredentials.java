package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.grpc.ChannelCredentials;
import io.grpc.InsecureChannelCredentials;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Insecure gRPC Channel Credentials")
public class ExprInsecureChannelCredentials extends SimpleExpression<ChannelCredentials> {

    static {
        Skript.registerExpression(ExprInsecureChannelCredentials.class, ChannelCredentials.class, ExpressionType.SIMPLE, "insecure [g]rpc channel credentials");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable ChannelCredentials[] get(Event event) {
        return new ChannelCredentials[]{InsecureChannelCredentials.create()};
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
        return "insecure gRPC channel credentials";
    }
}
