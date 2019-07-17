package com.reactive.streams.error.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class ErrorAPIApplication {

    private volatile int count = 0;

    public static void main(String[] args) {
        System.setProperty("server.port", "9093");
        SpringApplication.run(ErrorAPIApplication.class);
    }

    @GetMapping(path = "/error/api")
    public ResponseEntity<String> errorApi(String request) throws IllegalAccessException {
        if (++count % 3 == 0) {
            throw new IllegalAccessException("error!!");
        }
        return ResponseEntity.ok().body(request.concat("/error/ok"));
    }
}
