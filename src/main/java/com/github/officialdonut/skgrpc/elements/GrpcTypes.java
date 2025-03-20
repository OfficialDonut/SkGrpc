package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.EnumClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.jetbrains.annotations.Nullable;

public class GrpcTypes {

    static {
        Classes.registerClass(new ClassInfo<>(StreamObserver.class, "grpcstream")
                .name("gRPC Stream")
                .user("grpc ?streams?"));

        Classes.registerClass(new ClassInfo<>(Status.class, "grpcstatus")
                .name("gRPC Status")
                .user("grpc ?status(es)?"));

        Classes.registerClass(new EnumClassInfo<>(Status.Code.class, "grpcstatuscode", "grpc status codes")
                .name("gRPC Status Code")
                .user("grpc ?status ?codes?"));

        Classes.registerClass(new ClassInfo<>(Metadata.class, "grpcmetadata")
                .name("gRPC Metadata")
                .user("grpc ?metadata")
                .changer(new Changer<>() {
                    @Override
                    public @Nullable Class<?>[] acceptChange(ChangeMode changeMode) {
                        return switch (changeMode) {
                            case REMOVE, REMOVE_ALL -> new Class<?>[]{Metadata.Key.class};
                            default -> null;
                        };
                    }
                    @Override
                    public void change(Metadata[] metadatas, @Nullable Object[] objects, ChangeMode changeMode) {
                        for (Metadata metadata : metadatas) {
                            for (Object object : objects) {
                                if (object instanceof Metadata.Key<?> key) {
                                    metadata.discardAll(key);
                                }
                            }
                        }
                    }
                }));

        Classes.registerClass(new ClassInfo<>(Metadata.Key.class, "grpcmetadatakey")
                .name("gRPC Metadata Key")
                .user("grpc ?metadata ?keys?")
                .parser(new Parser<>() {
                    @Override
                    public String toString(Metadata.Key key, int i) {
                        return key.toString();
                    }
                    @Override
                    public String toVariableNameString(Metadata.Key key) {
                        return toString(key, 0);
                    }
                    @Override
                    public Metadata.Key<?> parse(String s, ParseContext context) {
                        return Metadata.Key.of(s, Metadata.ASCII_STRING_MARSHALLER);
                    }
                }));
    }
}
