package ru.bmstu.kibamba.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.bmstu.kibamba.gateway.model.Hotel;
import ru.bmstu.kibamba.gateway.payload.*;
import ru.bmstu.kibamba.gateway.service.GatewayService;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class GatewayController {
    private final RestTemplate restTemplate;
    private final GatewayService gatewayService;

    @Autowired
    public GatewayController(RestTemplateBuilder builder,
                             GatewayService gatewayService) {
        this.restTemplate = builder.build();
        this.gatewayService = gatewayService;
    }

    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity<UserInfoResponse> getMe(@RequestHeader("X-User-Name") String xUserName) {
        UserInfoResponse userInfo = new UserInfoResponse();
        userInfo.setReservations(new ArrayList<>());
        HttpHeaders headers = gatewayService.createHeader(xUserName);
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);
        String baseUrl = "http://localhost:8080/api/v1";
        ReservationShortResponse[] reservationShortResponses = restTemplate.exchange(
                baseUrl +"/reservations",HttpMethod.GET,request,ReservationShortResponse[].class
        ).getBody();
        assert reservationShortResponses != null;
        userInfo.getReservations().addAll(List.of(reservationShortResponses));
        LoyaltyInfoResponse loyalty = restTemplate.exchange(
                baseUrl + "/loyalty", HttpMethod.GET, request, LoyaltyInfoResponse.class
        ).getBody();
        assert loyalty != null;
        userInfo.setLoyalty(LoyaltyShortResponse
                .builder()
                        .discount(loyalty.getDiscount())
                        .status(loyalty.getStatus())
                .build());
        return ResponseEntity.ok(userInfo);
    }
}
