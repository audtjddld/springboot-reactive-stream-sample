package com.reactive.streams.inter.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@SpringBootApplication
public class InterAPIApplication {
    public static void main(String[] args) {
        System.setProperty("server.tomcat.max-threads", "1000");
        System.setProperty("server.port", "9091");
        SpringApplication.run(InterAPIApplication.class, args);
    }

    @GetMapping(path = "/inter/api")
    public String getInfo(String request) throws InterruptedException {

        TimeUnit.SECONDS.sleep(2);

        return request.concat("/inter");
    }
}
