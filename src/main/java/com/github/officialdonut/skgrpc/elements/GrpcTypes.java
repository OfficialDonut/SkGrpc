package com.github.officialdonut.skgrpc.elements;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.EnumClassInfo;
import ch.njol.skript.registrations.Classes;
import com.github.officialdonut.skgrpc.StreamObserverWrapper;
import io.grpc.ChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.Status;

public class GrpcTypes {

    static {
        Classes.registerClass(new EnumClassInfo<>(Status.Code.class, "grpcstatuscode", "grpc status codes")
                .name("gRPC Status Code")
                .user("grpc ?status ?codes?"));

        Classes.registerClass(new ClassInfo<>(ManagedChannel.class, "grpcchannel")
                .name("gRPC Channel")
                .user("grpc ?channels?"));

        Classes.registerClass(new ClassInfo<>(StreamObserverWrapper.class, "grpcrequeststream")
                .name("gRPC Request Stream")
                .user("grpc ?request ?streams?"));

        Classes.registerClass(new ClassInfo<>(Status.class, "grpcstatus")
                .name("gRPC Status")
                .user("grpc ?statu(s|es)"));

        Classes.registerClass(new ClassInfo<>(ChannelCredentials.class, "grpcchannelcredentials")
                .name("gRPC Channel Credentials")
                .user("grpc ?channel ?credentials"));
    }
}
