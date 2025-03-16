package com.github.officialdonut.skgrpc.impl;

import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;

public interface RpcStreamObserver extends StreamObserver<Message> {

    void onConnect(StreamObserver<Message> outgoingStream);
    void onReady();
    void onCancel();
    void onClose();

    boolean hasOnConnect();
    boolean hasOnReady();
    boolean hasOnCancel();
    boolean hasOnClose();

    interface Builder {

        RpcStreamObserver build();
    }
}
