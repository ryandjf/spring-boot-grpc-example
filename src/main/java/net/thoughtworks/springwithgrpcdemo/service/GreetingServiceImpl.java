package net.thoughtworks.springwithgrpcdemo.service;

import net.thoughtworks.springwithgrpcdemo.GreetingServiceGrpc;
import net.thoughtworks.springwithgrpcdemo.GreetingServiceOuterClass;
import net.thoughtworks.springwithgrpcdemo.starter.GrpcService;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    @Override
    public void greeting(GreetingServiceOuterClass.HelloRequest request,
                         StreamObserver<GreetingServiceOuterClass.HelloResponse> responseObserver) {
        GreetingServiceOuterClass.HelloResponse response = GreetingServiceOuterClass.HelloResponse.newBuilder()
                .setGreeting("Hello there, " + request.getName())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void greetingWithResponseStream(GreetingServiceOuterClass.HelloRequest request,
                                           StreamObserver<GreetingServiceOuterClass.HelloResponse> responseObserver) {
        GreetingServiceOuterClass.HelloResponse response = GreetingServiceOuterClass.HelloResponse.newBuilder()
                .setGreeting("(Stream Response) Hello there, " + request.getName())
                .build();
        responseObserver.onNext(response);
        responseObserver.onNext(response);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<GreetingServiceOuterClass.HelloRequest> greetingWithRequestStream(
            StreamObserver<GreetingServiceOuterClass.HelloResponse> responseObserver) {
        return new StreamObserver<GreetingServiceOuterClass.HelloRequest>() {
            private List<String> nameList = new ArrayList<>();

            @Override
            public void onNext(GreetingServiceOuterClass.HelloRequest request) {
                nameList.add(request.getName());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                GreetingServiceOuterClass.HelloResponse response = GreetingServiceOuterClass.HelloResponse.newBuilder()
                        .setGreeting("(Stream Request) Hello there, " + String.join(" ", nameList))
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<GreetingServiceOuterClass.HelloRequest> greetingWithRequestResponseStream(
            StreamObserver<GreetingServiceOuterClass.HelloResponse> responseObserver) {
        return new StreamObserver<GreetingServiceOuterClass.HelloRequest>() {
            private List<String> nameList = new ArrayList<>();

            @Override
            public void onNext(GreetingServiceOuterClass.HelloRequest request) {
                nameList.add(request.getName());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                nameList.stream()
                        .map(name -> GreetingServiceOuterClass.HelloResponse.newBuilder().setGreeting("(Stream Request/Response) Hello there, " + name).build())
                        .forEach(responseObserver::onNext);
                responseObserver.onCompleted();
            }
        };
    }
}
