### Setup
1. Copy `descriptors.dsc` to the folder `plugins/SkProtobuf/descriptors` on your server.
2. Copy `grpc-client.sk` and `grpc-server.sk` to the folder `plugins/Skript/scripts` on your server.
3. Load descriptors with `/skgrpc reload`.
4. Load skripts with `/sk reload grpc-client` and `/sk reload grpc-server`.

If you want to regenerate the descriptors file use the following protoc command:
```shell
protoc -odescriptors.dsc --include_imports echo.proto
```

### Commands
- /grpcecho \<string>
- /grpcechostream \<string>