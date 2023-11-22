package ru.bmstu.kibamba.gateway.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.bmstu.kibamba.gateway.payload.PaymentInfoResponse;
import ru.bmstu.kibamba.gateway.payload.PaymentPut;
import ru.bmstu.kibamba.gateway.payload.PaymentRequest;
import ru.bmstu.kibamba.gateway.payload.PaymentResponse;
import ru.bmstu.kibamba.gateway.service.GatewayService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final RestTemplate restTemplate;
    private final GatewayService gatewayService;
    private final String baseUrl = "http://localhost:8060/api/v1/payments";
    private int count = 0;

    @Autowired
    public PaymentController(RestTemplate restTemplate, GatewayService gatewayService) {
        this.restTemplate = restTemplate;
        this.gatewayService = gatewayService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public PaymentResponse createPayment(@RequestBody PaymentRequest paymentRequest) {
        HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest);
        return restTemplate.postForObject(baseUrl, request, PaymentResponse.class);
    }

    @PutMapping(value = "/{paymentUid}", consumes = "application/json", produces = "application/json")
    public PaymentResponse updatePayment(@PathVariable("paymentUid") UUID paymentUid, @RequestBody PaymentPut paymentPut) {
        String uri = baseUrl.concat("/{paymentUid}");
        HttpHeaders headers = gatewayService.createHeader();
        HttpEntity<PaymentPut> entity = new HttpEntity<>(paymentPut, headers);
        return restTemplate.exchange(uri, HttpMethod.PUT, entity, PaymentResponse.class, paymentUid).getBody();
    }

    @GetMapping(value = "/{paymentUid}")
    //@io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "paymentCb", fallbackMethod = "fallbackTest")
    public PaymentResponse getPayment(@PathVariable("paymentUid") UUID paymentUid) {
        String uri = baseUrl.concat("/{paymentUid}");
        String healthUri = baseUrl.concat("/manage/health");
        PaymentResponse result = null;
        try {
            ResponseEntity<String> str = restTemplate.getForEntity(healthUri, String.class);
            result = restTemplate.getForObject(uri, PaymentResponse.class, paymentUid);
            System.out.println(str.getBody());
            System.out.println(str.getStatusCode());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            count++;
            if (count == 2) {
                System.out.println("Pause");
            }
        }
        return result;
    }

    private PaymentResponse fallbackTest(Throwable throwable) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setStatus("");
        paymentResponse.setPrice(0);
        return paymentResponse;
    }
}
