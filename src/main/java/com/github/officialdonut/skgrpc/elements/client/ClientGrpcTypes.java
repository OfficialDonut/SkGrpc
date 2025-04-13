package com.github.officialdonut.skgrpc.elements.client;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Utils;
import io.grpc.*;

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
        Utils.addPluralOverride("grpccalloptions", "multiplegrpccalloptions");

        Classes.registerClass(new ClassInfo<>(CallCredentials.class, "grpccallcredentials")
                .name("gRPC Call Credentials")
                .user("grpc ?call ?credentials"));

        Classes.registerClass(new ClassInfo<>(ClientInterceptor.class, "grpcclientinterceptor")
                .name("gRPC Client Interceptor")
                .user("grpc ?client ?interceptors?"));

        Classes.registerClass(new ClassInfo<>(MethodDescriptor.class, "grpcmethoddescriptor")
                .name("gRPC Method Descriptor")
                .user("grpc ?method ?descriptors?"));

        Classes.registerClass(new ClassInfo<>(CallCredentials.RequestInfo.class, "grpcrequestinfo")
                .name("gRPC Request Info")
                .user("grpc ?request ?infos?"));
    }
}
