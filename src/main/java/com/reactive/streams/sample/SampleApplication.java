package com.reactive.streams.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@EnableWebFlux
@RestController
@SpringBootApplication
@Slf4j
public class SampleApplication {

    private final String ERROR_API_URL = "http://localhost:9093/error/api?request=${request}";
    private final String EXTER_API_URL = "http://localhost:9092/exter/api?request=${request}";
    private final String INTER_API_URL = "http://localhost:9091/inter/api?request=${request}";


    WebClient webClient = WebClient.builder().build();

    public static void main(String[] args) {
        // tomcat Thread 개수 1개로 처리속도를 측정.
        System.setProperty("server.tomcat.max-threads", "1");
        System.setProperty("server.port", "9090");
        SpringApplication.run(SampleApplication.class, args);

    }

    @GetMapping(path = "call")
    public Mono<String> externalAndInternalAPICall() {
        log.info("im call");
        // webFlux를 이용한 InterAPI 호출 뒤 ExterAPI 호출 한 결과를 리턴, 각각 API에는 Sleep이 2초씩 되어 있다.
        return Mono.just("Sample")
                .flatMap(request -> webClient.get().uri(INTER_API_URL, request).exchange())
                .flatMap(res -> res.bodyToMono(String.class))
                .flatMap(res -> webClient.get().uri(EXTER_API_URL, res).exchange())
                .flatMap(result -> result.bodyToMono(String.class));
    }

    @GetMapping(path = "include-error-call")
    public Mono<String> externalInternalAndErrorAPICall() {
        log.info("im include-error-call");
        // webFlux를 이용한 InterAPI 호출 뒤 ExterAPI 호출 한 결과를 리턴, 각각 API에는 Sleep이 2초씩 되어 있다.
        return Mono.just("Sample")
                .flatMap(request -> webClient.get().uri(INTER_API_URL, request).exchange())
                .flatMap(res -> res.bodyToMono(String.class))
                .flatMap(res -> webClient.get().uri(EXTER_API_URL, res).exchange())
                .flatMap(res -> webClient.get()
                        .uri(ERROR_API_URL, res)
                        .exchange()
                        .flatMap(response -> {
                            if (response.statusCode().is5xxServerError()) {
                                throw new RuntimeException("error");
                            } else {
                                return response.bodyToMono(String.class);
                            }
                        }))
                .retry(2, e -> e instanceof RuntimeException);
        // retry를 걸면 걸 때와 안걸때의 차이가 느껴진다.
    }
}

class Response {
    private String result;

    public Response(String result) {
        this.result = result;
    }
}

class ErrorResponse extends RuntimeException {

    public ErrorResponse(String result) {
        super(result);
    }
}
