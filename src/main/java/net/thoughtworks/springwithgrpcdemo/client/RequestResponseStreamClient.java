package net.thoughtworks.springwithgrpcdemo.client;

import net.thoughtworks.springwithgrpcdemo.GreetingServiceGrpc;
import net.thoughtworks.springwithgrpcdemo.GreetingServiceOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.stream.Stream;

public class RequestResponseStreamClient {
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8081")
                    .usePlaintext()
                    .build();

            GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);
            StreamObserver<GreetingServiceOuterClass.HelloRequest> requestStream =
                    stub.greetingWithRequestResponseStream(new StreamObserver<GreetingServiceOuterClass.HelloResponse>() {
                        @Override
                        public void onNext(GreetingServiceOuterClass.HelloResponse response) {
                            System.out.println(response);
                        }

                        @Override
                        public void onError(Throwable t) {
                            t.printStackTrace();
                        }

                        @Override
                        public void onCompleted() {
                        }
                    });

            Stream.of("Ray1", "Ray2", "Ray3")
                    .map(name -> GreetingServiceOuterClass.HelloRequest.newBuilder().setName(name).build())
                    .forEach(requestStream::onNext);
            requestStream.onCompleted();
        }).start();

        Thread.sleep(1000);
    }
}
