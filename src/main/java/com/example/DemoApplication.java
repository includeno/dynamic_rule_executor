package com.example;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@MapperScan("com.example.mapper")
@Slf4j
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        log.info("args: "+ Arrays.toString(args));
        SpringApplication.run(DemoApplication.class, args);
    }
}
