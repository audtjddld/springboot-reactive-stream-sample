package com.reactive.streams.exter.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@SpringBootApplication
public class ExterAPIApplication {
    public static void main(String[] args) {
        System.setProperty("server.tomcat.max-threads", "1000");
        System.setProperty("server.port", "9092");
        SpringApplication.run(ExterAPIApplication.class, args);
    }

    @GetMapping(path = "/exter/api")
    public String getData(String request) throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);

        return request.concat("/exter");
    }
}
