package ru.bmstu.kibamba.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import ru.bmstu.kibamba.gateway.exception.ServiceUnavailableException;

import java.util.UUID;


public class RequestProcessor<ResponseType, RequestType> implements Runnable {
    private final String baseUrl;
    private UUID uid;

    private final HttpEntity<RequestType> request;

    private final ResponseType responseTypeClass;
    private final HttpMethodType methodType;


    private final RestTemplate restTemplate;


    public RequestProcessor(String baseUrl,
                            UUID uid,
                            HttpEntity<RequestType> request,
                            ResponseType responseTypeClass,
                            HttpMethodType methodType, RestTemplate restTemplate) {
        this(baseUrl, request, responseTypeClass, methodType, restTemplate);
        this.uid = uid;
    }


    public RequestProcessor(String baseUrl,
                            HttpEntity<RequestType> request,
                            ResponseType responseTypeClass,
                            HttpMethodType methodType, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.request = request;
        this.responseTypeClass = responseTypeClass;
        this.methodType = methodType;
        this.restTemplate = restTemplate;
    }

    @Override
    public void run() {
        try {
            String healthUri = this.baseUrl.concat("/manage/health");
            restTemplate.getForEntity(healthUri, String.class);
            processRequest();
        } catch (Exception e) {
            throw new ServiceUnavailableException("Service unavailable");
        }
    }

    private void processRequest() {
        switch (this.methodType) {
            case POST -> {
                restTemplate.postForObject(this.baseUrl, this.request, responseTypeClass.getClass());
            }
            case PUT -> {
                if (this.uid != null) {
                    restTemplate.exchange(this.baseUrl, HttpMethod.PUT, this.request, responseTypeClass.getClass(), this.uid);
                }

            }
        }
    }
}
