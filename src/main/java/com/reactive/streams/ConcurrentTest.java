package com.reactive.streams;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ConcurrentTest {
    static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        ExecutorService es = Executors.newFixedThreadPool(100);

        RestTemplate rt = new RestTemplate();
        String url = "http://localhost:9090/call";

        CyclicBarrier barrier = new CyclicBarrier(101);

        for (int i = 0; i < 100; i++) {
            es.submit(() -> {

                int idx = counter.addAndGet(1);
                log.info("thread {}", idx);
                barrier.await();

                StopWatch sw = new StopWatch();
                sw.start();

                String result = rt.getForObject(url, String.class, idx);

                sw.stop();

                log.info("Elapsed: {} {}", sw.getTotalTimeSeconds(), result);

                return null;
            });
        }

        barrier.await();
        StopWatch main = new StopWatch();
        main.start();

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);
        main.stop();
        log.info("Total : {}", main.getTotalTimeSeconds());

    }
}
