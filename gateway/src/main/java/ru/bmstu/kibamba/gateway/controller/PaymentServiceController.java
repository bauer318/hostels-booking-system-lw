package ru.bmstu.kibamba.gateway.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.bmstu.kibamba.gateway.payload.PaymentPut;
import ru.bmstu.kibamba.gateway.payload.PaymentRequest;
import ru.bmstu.kibamba.gateway.payload.PaymentResponse;
import ru.bmstu.kibamba.gateway.service.GatewayService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@Slf4j
public class PaymentServiceController {
    private final RestTemplate restTemplate;
    private final GatewayService gatewayService;
    private final String baseUrl = "http://localhost:8060/api/v1/payments";
    private int count = 0;

    @Autowired
    public PaymentServiceController(RestTemplate restTemplate, GatewayService gatewayService) {
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
    public PaymentResponse getPayment(@PathVariable("paymentUid") UUID paymentUid) {
        if (count < 2) {
            checkServiceAvailability();
            String uri = baseUrl.concat("/{paymentUid}");
            return restTemplate.getForObject(uri, PaymentResponse.class, paymentUid);
        } else {
            return fallbackTest();
        }
    }

    private PaymentResponse fallbackTest() {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setStatus("");
        paymentResponse.setPrice(0);
        return paymentResponse;
    }

    private void checkServiceAvailability() {
        try {
            String healthUri = baseUrl.concat("/manage/health");
            restTemplate.getForEntity(healthUri, String.class);
            count = 0;
        } catch (Exception e) {
            count++;
            log.info("service not available {}", e.getMessage());
        }
    }
}
