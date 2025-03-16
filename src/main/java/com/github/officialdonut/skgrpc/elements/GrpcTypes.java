package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.EnumClassInfo;
import ch.njol.skript.registrations.Classes;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

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
    }
}
