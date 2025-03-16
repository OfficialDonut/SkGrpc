package com.github.officialdonut.skgrpc.impl;

import com.google.protobuf.Message;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.MethodDescriptor;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientRpc {

    private final MethodDescriptor<Message, Message> descriptor;

    public ClientRpc(MethodDescriptor<Message, Message> descriptor) {
        this.descriptor = descriptor;
    }

    public Message[] invoke(Channel channel, Message request) {
        if (descriptor.getType() == MethodDescriptor.MethodType.UNARY) {
            return new Message[]{ClientCalls.blockingUnaryCall(channel, descriptor, CallOptions.DEFAULT, request)};
        } else if (descriptor.getType() == MethodDescriptor.MethodType.SERVER_STREAMING) {
            List<Message> responses = new ArrayList<>();
            ClientCalls.blockingServerStreamingCall(channel, descriptor, CallOptions.DEFAULT, request).forEachRemaining(responses::add);
            return responses.toArray(Message[]::new);
        } else {
            throw new IllegalStateException("Invalid descriptor type: " + descriptor.getType());
        }
    }

    public void invoke(Channel channel, Message request, StreamObserver<Message> responseObserver) {
        ClientCall<Message, Message> call = channel.newCall(descriptor, CallOptions.DEFAULT);
        if (descriptor.getType() == MethodDescriptor.MethodType.UNARY) {
            ClientCalls.asyncUnaryCall(call, request, responseObserver);
        } else if (descriptor.getType() == MethodDescriptor.MethodType.SERVER_STREAMING) {
            ClientCalls.asyncServerStreamingCall(call, request, responseObserver);
        } else {
            throw new IllegalStateException("Invalid descriptor type: " + descriptor.getType());
        }
    }

    public StreamObserver<Message> invoke(Channel channel, StreamObserver<Message> responseObserver) {
        ClientCall<Message, Message> call = channel.newCall(descriptor, CallOptions.DEFAULT);
        if (descriptor.getType() == MethodDescriptor.MethodType.CLIENT_STREAMING) {
            return ClientCalls.asyncClientStreamingCall(call, responseObserver);
        } else if (descriptor.getType() == MethodDescriptor.MethodType.BIDI_STREAMING) {
            return ClientCalls.asyncBidiStreamingCall(call, responseObserver);
        } else {
            throw new IllegalStateException("Invalid descriptor type: " + descriptor.getType());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClientRpc clientRpc = (ClientRpc) o;
        return Objects.equals(descriptor, clientRpc.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(descriptor);
    }

    @Override
    public String toString() {
        return descriptor.toString();
    }

    public boolean isClientStreaming() {
        return descriptor.getType() == MethodDescriptor.MethodType.CLIENT_STREAMING || descriptor.getType() == MethodDescriptor.MethodType.BIDI_STREAMING;
    }

    public boolean isServerStreaming() {
        return descriptor.getType() == MethodDescriptor.MethodType.SERVER_STREAMING || descriptor.getType() == MethodDescriptor.MethodType.BIDI_STREAMING;
    }

    public MethodDescriptor<Message, Message> getDescriptor() {
        return descriptor;
    }
}
