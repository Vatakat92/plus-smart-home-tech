package ru.yandex.practicum.grpc.server.service;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.echo.EchoRequest;
import ru.yandex.practicum.grpc.echo.EchoResponse;
import ru.yandex.practicum.grpc.echo.EchoServiceGrpc;

@GrpcService
@Slf4j
public class EchoService extends EchoServiceGrpc.EchoServiceImplBase {

    @Override
    public void echo(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
        try {
            log.info("echo received: {}", request.getMessage());
            responseObserver.onNext(EchoResponse.newBuilder().setMessage(request.getMessage()).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getLocalizedMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}