package net.thoughtworks.springwithgrpcdemo.starter;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class GrpcServerRunner implements CommandLineRunner, DisposableBean {
    private final ConfigurableApplicationContext applicationContext;
    private Server server;

    public GrpcServerRunner(@Autowired ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
        ServerBuilder serverBuilder = ServerBuilder.forPort(8081);

        String[] beanNames = applicationContext.getBeanNamesForAnnotation(GrpcService.class);
        Arrays.stream(beanNames).forEach(beanName -> {
            BindableService service = applicationContext.getBean(beanName, BindableService.class);
            serverBuilder.addService(service);
        });

        server = serverBuilder.build().start();

        runSever();
    }

    private void runSever() {
        Thread thread = new Thread(() -> {
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(false);
        thread.start();
    }

    @Override
    public void destroy() {
        server.shutdown();
    }
}
