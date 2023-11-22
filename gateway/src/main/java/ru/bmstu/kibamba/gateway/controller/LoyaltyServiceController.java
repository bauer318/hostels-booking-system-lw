package ru.bmstu.kibamba.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.bmstu.kibamba.gateway.payload.LoyaltyInfoResponse;
import ru.bmstu.kibamba.gateway.payload.LoyaltyPut;
import ru.bmstu.kibamba.gateway.service.GatewayService;

@RestController
@RequestMapping("/api/v1/loyalty")
public class LoyaltyServiceController {
    private final RestTemplate restTemplate;
    private final GatewayService gatewayService;

    private final String loyaltyBaseUrl = "http://localhost:8050/api/v1/loyalty";

    @Autowired
    public LoyaltyServiceController(RestTemplate restTemplate, GatewayService gatewayService) {
        this.restTemplate = restTemplate;
        this.gatewayService = gatewayService;
    }

    @GetMapping(produces = "application/json")
    public LoyaltyInfoResponse getLoyalty(@RequestHeader("X-User-Name") String xUserName) {
        HttpHeaders headers = gatewayService.createHeader(xUserName);
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(loyaltyBaseUrl, HttpMethod.GET, entity, LoyaltyInfoResponse.class).getBody();
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    public LoyaltyInfoResponse updateLoyalty(@RequestHeader("X-User-Name") String xUserName, @RequestBody LoyaltyPut loyaltyPut) {
        HttpHeaders headers = gatewayService.createHeader(xUserName);
        HttpEntity<LoyaltyPut> entity = new HttpEntity<>(loyaltyPut, headers);
        return restTemplate.exchange(loyaltyBaseUrl, HttpMethod.PUT, entity, LoyaltyInfoResponse.class).getBody();
    }
}
