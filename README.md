# SkGrpc
Skript addon that adds support for [gRPC](https://grpc.io/).

> [!IMPORTANT]
> SkGrpc depends on [SkProtobuf](https://github.com/OfficialDonut/SkProtobuf). SkGrpc requires a descriptor set for the RPCs you wish to use with Skript, see SkProtobuf for more information about descriptor sets.

## Examples
See full example files [here](examples).

#### Proto
<details>
<summary>echo.proto</summary>
<pre>
syntax = "proto3";<br>
message EchoRequest {
  string message = 1;
}<br>
message EchoResponse {
  string message = 1;
}<br>
service EchoService {
  rpc Echo(EchoRequest) returns (EchoResponse) {}
  rpc EchoStream(stream EchoRequest) returns (stream EchoResponse) {}
}
</pre>
</details>

#### Client
<details>
<summary>Create Channel</summary>
<pre>
on load:
    if {channel} is set:
        shutdown grpc channel {channel}<br>
    set {_creds} to insecure channel credentials
    grpc channel {channel}:
        host: "localhost"
        port: 60123
        credentials: {_creds}
</pre>
</details>

<details>
<summary>Unary RPC</summary>
<pre>
command /grpcecho &lt;string&gt;:
    trigger:
        # build request
        set {_builder} to new builder for proto "EchoRequest"
        set proto field "message" in {_builder} to arg-1
        set {_request} to proto from builder {_builder}<br>
        # send request
        set {_response} to response of rpc "Echo" for {_request} using {channel}
        send "Client received response message: %value of proto field "message" in {_response}%"
</pre>
</details>

<details>
<summary>Bidirectional Streaming</summary>
<pre>
command /grpcechostream &lt;string&gt;:
    trigger:
        # start async rpc
        set {_sender} to command sender
        set {_stream} to new rpc request stream
        async rpc "EchoStream" for request stream {_stream} using {channel}:
            on next:
                set {_message} to value of proto field "message" in event-protobufmessage
                send "Client received response message: %{_message}%" to {_sender}
            on error:
                send "Client received error: %event-grpcstatus's code% - %event-grpcstatus's description%" to {_sender}
            on complete:
                send "Client finished receiving responses." to {_sender}<br>
        # send requests
        loop arg-1 split at " ":
            set {_builder} to new builder for proto "EchoRequest"
            set proto field "message" in {_builder} to loop-value
            send proto from builder {_builder} on rpc stream {_stream}<br>
        # tell the server we're done sending requests
        complete rpc stream {_stream}
</pre>
</details>

#### Server
<details>
<summary>Create Server</summary>
<pre>
on load:
    if {server} is set:
        shutdown grpc server {server}<br>
    set {_creds} to insecure server credentials
    grpc server {server} with services "EchoService":
        port: 60123
        credentials: {_creds}<br>
    setupEchoHandler({server})
    setupEchoStreamHandler({server})
    start grpc server {server}
</pre>
</details>

<details>
<summary>Unary RPC</summary>
<pre>
function setupEchoHandler(server: grpcserver):
    rpc "Echo" handler for {_server}:
        response:
            set {_message} to value of proto field "message" in event-protobufmessage
            broadcast "Server received request message: %{_message}%"<br>
            # send response
            set {_response} to new builder for proto "EchoResponse"
            set proto field "message" in {_response} to {_message}
            return rpc response proto from builder {_response}
</pre>
</details>

<details>
<summary>Bidirectional Streaming</summary>
<pre>
function setupEchoStreamHandler(server: grpcserver):
    rpc "EchoStream" handler for {_server}:
        on connect:
            # store the stream for sending responses
            set {_stream} to event-grpcstream
        on next:
            set {_message} to value of proto field "message" in event-protobufmessage
            broadcast "Server received request message: %{_message}%"<br>
            # send response
            set {_response} to new builder for proto "EchoResponse"
            set proto field "message" in {_response} to {_message}
            send proto from builder {_response} on rpc stream {_stream}
        on error:
            broadcast "Server received error: %event-grpcstatus's code% - %event-grpcstatus's description%"
        on complete:
            broadcast "Server finished receiving requests."
            # tell the client we're done sending responses
            complete rpc stream {_stream}
</pre>
</details>
