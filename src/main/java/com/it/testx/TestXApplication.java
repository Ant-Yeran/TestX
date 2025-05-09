package com.it.testx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.it.testx.mapper")
public class TestXApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestXApplication.class, args);
    }

}
