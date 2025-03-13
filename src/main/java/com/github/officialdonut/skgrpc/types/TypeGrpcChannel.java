package com.github.officialdonut.skgrpc.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import io.grpc.Channel;

public class TypeGrpcChannel {

    static {
        Classes.registerClass(new ClassInfo<>(Channel.class, "grpcchannel").name("gRPC Channel"));
    }
}
