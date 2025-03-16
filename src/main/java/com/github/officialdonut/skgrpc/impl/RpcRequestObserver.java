package com.github.officialdonut.skgrpc.impl;

import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;

public interface RpcRequestObserver {

    void onRequest(Message request, StreamObserver<Message> responseStream);
}
