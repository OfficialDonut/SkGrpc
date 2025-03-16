package com.github.officialdonut.skgrpc.impl;

import com.google.protobuf.Message;
import io.grpc.MethodDescriptor;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;

public class RpcHandler implements ServerCalls.UnaryMethod<Message, Message>, ServerCalls.ClientStreamingMethod<Message, Message>, ServerCalls.ServerStreamingMethod<Message, Message>, ServerCalls.BidiStreamingMethod<Message, Message> {

    private final MethodDescriptor<Message, Message> descriptor;
    private RpcRequestObserver requestObserver;
    private RpcStreamObserver.Builder streamObserverBuilder;

    public RpcHandler(MethodDescriptor<Message, Message> descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public StreamObserver<Message> invoke(StreamObserver<Message> responseObserver) {
        if (streamObserverBuilder == null) {
            return ServerCalls.asyncUnimplementedStreamingCall(descriptor, responseObserver);
        }
        ServerCallStreamObserver<Message> callStreamObserver = (ServerCallStreamObserver<Message>) responseObserver;
        RpcStreamObserver observer = streamObserverBuilder.build();
        if (observer.hasOnReady()) {
            callStreamObserver.setOnReadyHandler(observer::onReady);
        }
        if (observer.hasOnCancel()) {
            callStreamObserver.setOnCancelHandler(observer::onCancel);
        }
        if (observer.hasOnClose()) {
            callStreamObserver.setOnCloseHandler(observer::onClose);
        }
        if (observer.hasOnConnect()) {
            observer.onConnect(new SynchronizedStreamObserver(responseObserver));
        }
        return new StreamObserver<>() {
            @Override
            public void onNext(Message message) {
                observer.onNext(message);
            }
            @Override
            public void onError(Throwable throwable) {
                observer.onError(throwable);
            }
            @Override
            public void onCompleted() {
                observer.onCompleted();
            }
        };
    }

    @Override
    public void invoke(Message message, StreamObserver<Message> responseObserver) {
        if (requestObserver != null) {
            requestObserver.onRequest(message, responseObserver);
        } else {
            ServerCalls.asyncUnimplementedUnaryCall(descriptor, responseObserver);
        }
    }

    public void setRequestObserver(RpcRequestObserver requestObserver) {
        this.requestObserver = requestObserver;
    }

    public void setStreamObserverBuilder(RpcStreamObserver.Builder streamObserverBuilder) {
        this.streamObserverBuilder = streamObserverBuilder;
    }
}
