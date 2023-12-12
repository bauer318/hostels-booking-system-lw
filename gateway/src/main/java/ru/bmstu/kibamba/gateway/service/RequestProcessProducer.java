package ru.bmstu.kibamba.gateway.service;

import java.util.concurrent.BlockingQueue;

public class RequestProcessProducer {
    private final BlockingQueue<RequestProcessor<?, ?>> requests;

    public RequestProcessProducer(BlockingQueue<RequestProcessor<?, ?>> requests) {
        this.requests = requests;
    }

    public void addRequest(RequestProcessor<?, ?> request) {
        requests.offer(request);
    }

}
