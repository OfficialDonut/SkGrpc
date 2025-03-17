package com.github.officialdonut.skgrpc.impl;

import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;

public class SynchronizedStreamObserver implements StreamObserver<Message> {

    private final StreamObserver<Message> delegate;

    public SynchronizedStreamObserver(StreamObserver<Message> delegate) {
        this.delegate = delegate;
    }

    @Override
    public synchronized void onNext(Message message) {
        delegate.onNext(message);
    }

    @Override
    public synchronized void onError(Throwable throwable) {
        delegate.onError(throwable);
    }

    @Override
    public synchronized void onCompleted() {
        delegate.onCompleted();
    }
}
