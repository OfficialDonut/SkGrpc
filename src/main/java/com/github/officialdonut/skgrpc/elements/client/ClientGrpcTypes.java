package com.github.officialdonut.skgrpc.elements.client;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import io.grpc.CallCredentials;
import io.grpc.CallOptions;
import io.grpc.ChannelCredentials;
import io.grpc.ManagedChannel;

public class ClientGrpcTypes {

    static {
        Classes.registerClass(new ClassInfo<>(ManagedChannel.class, "grpcchannel")
                .name("gRPC Channel")
                .user("grpc ?channels?"));

        Classes.registerClass(new ClassInfo<>(ChannelCredentials.class, "grpcchannelcredentials")
                .name("gRPC Channel Credentials")
                .user("grpc ?channel ?credentials"));

        Classes.registerClass(new ClassInfo<>(CallOptions.class, "grpccalloptions")
                .name("gRPC Call Options")
                .user("grpc ?call ?options"));

        Classes.registerClass(new ClassInfo<>(CallCredentials.class, "grpccallcredentials")
                .name("gRPC Call Credentials")
                .user("grpc ?call ?credentials"));
    }
}
