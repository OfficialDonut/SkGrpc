package com.github.officialdonut.skgrpc.elements.server;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import io.grpc.Server;

public class ServerGrpcTypes {

    static {
        Classes.registerClass(new ClassInfo<>(Server.class, "grpcserver")
                .name("gRPC Server")
                .user("grpc ?servers?"));
    }
}
