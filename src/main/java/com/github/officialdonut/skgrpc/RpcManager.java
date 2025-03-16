package com.github.officialdonut.skgrpc;

import com.github.officialdonut.skgrpc.impl.ClientRpc;
import com.github.officialdonut.skgrpc.impl.RpcHandler;
import com.github.officialdonut.skprotobuf.ProtoManager;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import io.grpc.*;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ServerCalls;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpcManager {

    private final Table<Server, MethodDescriptor<Message, Message>, RpcHandler> rpcHandlers;
    private final ProtoManager protoManager;

    private Map<String, ServiceDescriptor> serviceDescriptors;
    private Map<String, ClientRpc> clientRpcs;

    public RpcManager(ProtoManager protoManager) {
        this.protoManager = protoManager;
        this.rpcHandlers = HashBasedTable.create();
    }

    public void loadDescriptors() {
        serviceDescriptors = new HashMap<>();
        clientRpcs = new HashMap<>();
        for (Descriptors.FileDescriptor fileDescriptor : protoManager.getFileDescriptors()) {
            for (Descriptors.ServiceDescriptor serviceDescriptor : fileDescriptor.getServices()) {
                ServiceDescriptor.Builder builder = ServiceDescriptor.newBuilder(serviceDescriptor.getFullName());
                for (Descriptors.MethodDescriptor methodDescriptor : serviceDescriptor.getMethods()) {
                    ClientRpc rpc = new ClientRpc(createDescriptor(methodDescriptor));
                    clientRpcs.put(methodDescriptor.getFullName(), rpc);
                    clientRpcs.putIfAbsent(methodDescriptor.getName(), rpc);
                    builder.addMethod(rpc.getDescriptor());
                }
                ServiceDescriptor descriptor = builder.build();
                serviceDescriptors.put(serviceDescriptor.getFullName(), descriptor);
                serviceDescriptors.putIfAbsent(serviceDescriptor.getName(), descriptor);
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Server createServer(ServerBuilder<?> builder, List<ServiceDescriptor> services) {
        Map<MethodDescriptor<Message, Message>, RpcHandler> handlers = new HashMap<>();
        for (ServiceDescriptor service : services) {
            ServerServiceDefinition.Builder definition = ServerServiceDefinition.builder(service);
            for (MethodDescriptor method : service.getMethods()) {
                RpcHandler handler = new RpcHandler(method);
                handlers.put(method, handler);
                definition.addMethod(ServerMethodDefinition.create(method, switch (method.getType()) {
                    case UNARY -> ServerCalls.asyncUnaryCall(handler);
                    case CLIENT_STREAMING -> ServerCalls.asyncClientStreamingCall(handler);
                    case SERVER_STREAMING -> ServerCalls.asyncServerStreamingCall(handler);
                    case BIDI_STREAMING -> ServerCalls.asyncBidiStreamingCall(handler);
                    case UNKNOWN -> throw new IllegalStateException("Unknown RPC method type");
                }));
            }
            builder.addService(definition.build());
        }
        Server server = builder.build();
        rpcHandlers.row(server).putAll(handlers);
        return server;
    }

    public void shutdownServer(Server server) {
        rpcHandlers.row(server).clear();
        server.shutdownNow();
    }

    public ServiceDescriptor getServiceDescriptor(String serviceName) {
        return serviceDescriptors.get(serviceName);
    }

    public RpcHandler getRpcHandler(Server server, MethodDescriptor<Message, Message> descriptor) {
        return rpcHandlers.get(server, descriptor);
    }

    public ClientRpc getClientRpc(String rpcName) {
        return clientRpcs.get(rpcName);
    }
}
