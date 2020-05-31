package net.thoughtworks.springwithgrpcdemo.starter;

import io.grpc.BindableService;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface GrpcService {
    Class<? extends BindableService>[] value() default {};
}
