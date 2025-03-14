package com.github.officialdonut.skgrpc;

import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;

public class StreamObserverWrapper implements StreamObserver<Message> {

    private StreamObserver<Message> delegate;

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
