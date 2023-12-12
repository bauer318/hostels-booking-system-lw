package ru.bmstu.kibamba.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.bmstu.kibamba.gateway.exception.ServiceUnavailableException;
import ru.bmstu.kibamba.gateway.payload.LoyaltyInfoResponse;
import ru.bmstu.kibamba.gateway.payload.LoyaltyPut;
import ru.bmstu.kibamba.gateway.service.GatewayService;

@RestController
@RequestMapping("/api/v1/loyalty")
@Slf4j
public class LoyaltyServiceController {
    private final RestTemplate restTemplate;
    private final GatewayService gatewayService;

    private final String loyaltyBaseUrl = "http://localhost:8050/api/v1/loyalty";

    @Autowired
    public LoyaltyServiceController(RestTemplate restTemplate, GatewayService gatewayService) {
        this.restTemplate = restTemplate;
        this.gatewayService = gatewayService;
    }

    @GetMapping("/manage/health")
    public ResponseEntity<String> manageHealth() {
        try {
            String healthUri = loyaltyBaseUrl.concat("/manage/health");
            return restTemplate.getForEntity(healthUri, String.class);
        } catch (Exception e) {
            throw new ServiceUnavailableException("Loyalty service unavailable");
        }
    }

    @GetMapping(produces = "application/json")
    public LoyaltyInfoResponse getLoyalty(@RequestHeader("X-User-Name") String xUserName) {
        try {
            restTemplate.getForEntity("http://localhost:8080/api/v1/loyalty/manage/health", String.class);
            log.info("loyalty service available");
            HttpHeaders headers = gatewayService.createHeader(xUserName);
            HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
            return restTemplate.exchange(loyaltyBaseUrl, HttpMethod.GET, entity, LoyaltyInfoResponse.class).getBody();
        } catch (Exception e) {
            throw new ServiceUnavailableException("Loyalty Service unavailable");
        }
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    public LoyaltyInfoResponse updateLoyalty(@RequestHeader("X-User-Name") String xUserName, @RequestBody LoyaltyPut loyaltyPut) {
        try {
            HttpHeaders headers = gatewayService.createHeader(xUserName);
            HttpEntity<LoyaltyPut> entity = new HttpEntity<>(loyaltyPut, headers);
            return restTemplate.exchange(loyaltyBaseUrl, HttpMethod.PUT, entity, LoyaltyInfoResponse.class).getBody();
        } catch (Exception e) {
            throw new ServiceUnavailableException("Loyalty Service unavailable");
        }

    }
}
