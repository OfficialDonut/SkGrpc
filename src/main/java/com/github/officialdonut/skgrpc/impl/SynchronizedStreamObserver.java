package com.github.officialdonut.skgrpc.impl;

import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;

public class SynchronizedStreamObserver implements StreamObserver<Message> {

    private StreamObserver<Message> delegate;

    public SynchronizedStreamObserver() {}

    public SynchronizedStreamObserver(StreamObserver<Message> delegate) {
        this.delegate = delegate;
    }

    @Override
    public synchronized void onNext(Message message) {
        if (delegate != null) {
            delegate.onNext(message);
        }
    }

    @Override
    public synchronized void onError(Throwable throwable) {
        if (delegate != null) {
            delegate.onError(throwable);
        }
    }

    @Override
    public synchronized void onCompleted() {
        if (delegate != null) {
            delegate.onCompleted();
        }
    }

    public synchronized void setDelegate(StreamObserver<Message> delegate) {
        this.delegate = delegate;
    }
}
