package ru.bmstu.kibamba.gateway.service;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

@Slf4j
public class RequestProcessConsumer implements Runnable {
    private final BlockingQueue<RequestProcessor<?, ?>> requests;
    private final long timeOut;

    public RequestProcessConsumer(BlockingQueue<RequestProcessor<?, ?>> requests, long timeOut) {
        this.requests = requests;
        this.timeOut = timeOut;
    }

    @Override
    public void run() {
        log.info("Run process request");
        try {
            Thread.sleep(this.timeOut);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        consumeRequest();

    }

    private void consumeRequest() {
        try {
            RequestProcessor<?, ?> request = requests.take();
            try {
                request.run();
            } catch (Exception e) {
                requests.put(request);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
