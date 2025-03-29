package com.github.officialdonut.skgrpc.elements.server;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import io.grpc.Server;
import io.grpc.ServerCall;
import io.grpc.ServerCredentials;
import io.grpc.ServerInterceptor;

public class ServerGrpcTypes {

    static {
        Classes.registerClass(new ClassInfo<>(Server.class, "grpcserver")
                .name("gRPC Server")
                .user("grpc ?servers?"));

        Classes.registerClass(new ClassInfo<>(ServerCredentials.class, "grpcservercredentials")
                .name("gRPC Server Credentials")
                .user("grpc ?server ?credentials"));

        Classes.registerClass(new ClassInfo<>(ServerInterceptor.class, "grpcserverinterceptor")
                .name("gRPC Server Interceptor")
                .user("grpc ?server ?interceptors?"));

        Classes.registerClass(new ClassInfo<>(ServerCall.class, "grpcservercall")
                .name("gRPC Server Call")
                .user("grpc ?server ?calls?"));
    }
}
