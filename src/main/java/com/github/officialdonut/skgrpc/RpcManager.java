package com.github.officialdonut.skgrpc;

import com.github.officialdonut.skprotobuf.ProtoManager;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpcManager {

    private Map<String, MethodDescriptor<Message, Message>> rpcDescriptors;
    private final ProtoManager protoManager;

    public RpcManager(ProtoManager protoManager) {
        this.protoManager = protoManager;
    }

    public void loadDescriptors() {
        rpcDescriptors = new HashMap<>();
        for (Descriptors.FileDescriptor fileDescriptor : protoManager.getFileDescriptors()) {
            for (Descriptors.ServiceDescriptor serviceDescriptor : fileDescriptor.getServices()) {
                for (Descriptors.MethodDescriptor methodDescriptor : serviceDescriptor.getMethods()) {
                    MethodDescriptor<Message, Message> descriptor = createDescriptor(methodDescriptor);
                    rpcDescriptors.put(methodDescriptor.getFullName(), descriptor);
                    rpcDescriptors.putIfAbsent(methodDescriptor.getName(), descriptor);
                }
            }
        }
    }

    private MethodDescriptor<Message, Message> createDescriptor(Descriptors.MethodDescriptor descriptor) {
        MethodDescriptor.MethodType type;
        if (descriptor.isClientStreaming()) {
            type = descriptor.isServerStreaming() ? MethodDescriptor.MethodType.BIDI_STREAMING : MethodDescriptor.MethodType.CLIENT_STREAMING;
        } else if (descriptor.isServerStreaming()) {
            type = MethodDescriptor.MethodType.SERVER_STREAMING;
        } else {
            type = MethodDescriptor.MethodType.UNARY;
        }
        return MethodDescriptor.<Message, Message>newBuilder()
                .setType(type)
                .setFullMethodName(MethodDescriptor.generateFullMethodName(descriptor.getService().getFullName(), descriptor.getName()))
                .setIdempotent(descriptor.getOptions().getIdempotencyLevel() == DescriptorProtos.MethodOptions.IdempotencyLevel.NO_SIDE_EFFECTS)
                .setRequestMarshaller(ProtoUtils.marshaller(DynamicMessage.getDefaultInstance(descriptor.getInputType())))
                .setResponseMarshaller(ProtoUtils.marshaller(DynamicMessage.getDefaultInstance(descriptor.getOutputType())))
                .build();
    }

    public Message[] invokeRpc(Channel channel, MethodDescriptor<Message, Message> descriptor, Message request) {
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

    public void invokeRpc(Channel channel, MethodDescriptor<Message, Message> descriptor, Message request, StreamObserver<Message> responseObserver) {
        ClientCall<Message, Message> call = channel.newCall(descriptor, CallOptions.DEFAULT);
        if (descriptor.getType() == MethodDescriptor.MethodType.UNARY) {
            ClientCalls.asyncUnaryCall(call, request, responseObserver);
        } else if (descriptor.getType() == MethodDescriptor.MethodType.SERVER_STREAMING) {
            ClientCalls.asyncServerStreamingCall(call, request, responseObserver);
        } else {
            throw new IllegalStateException("Invalid descriptor type: " + descriptor.getType());
        }
    }

    public StreamObserver<Message> invokeRpc(Channel channel, MethodDescriptor<Message, Message> descriptor, StreamObserver<Message> responseObserver) {
        ClientCall<Message, Message> call = channel.newCall(descriptor, CallOptions.DEFAULT);
        if (descriptor.getType() == MethodDescriptor.MethodType.CLIENT_STREAMING) {
            return ClientCalls.asyncClientStreamingCall(call, responseObserver);
        } else if (descriptor.getType() == MethodDescriptor.MethodType.BIDI_STREAMING) {
            return ClientCalls.asyncBidiStreamingCall(call, responseObserver);
        } else {
            throw new IllegalStateException("Invalid descriptor type: " + descriptor.getType());
        }
    }

    public MethodDescriptor<Message, Message> getRpcDescriptor(String name) {
        return rpcDescriptors.get(name);
    }
}
