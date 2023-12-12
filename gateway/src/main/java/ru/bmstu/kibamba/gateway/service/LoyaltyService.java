package ru.bmstu.kibamba.gateway.service;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.bmstu.kibamba.gateway.payload.LoyaltyInfoResponse;
import ru.bmstu.kibamba.gateway.payload.LoyaltyPut;

import java.util.Collections;

@Service
public class LoyaltyService {


    public void retryProcessRequest(RestTemplate restTemplate,
                                    String baseUrl,
                                    HttpEntity<HttpHeaders> entity, String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-User-Name", username);
        LoyaltyInfoResponse loyaltyResponse = restTemplate.exchange(baseUrl,
                HttpMethod.GET, entity, LoyaltyInfoResponse.class).getBody();
        assert loyaltyResponse != null;
        LoyaltyPut loyaltyPut = LoyaltyPut.builder()
                .reservationCount(loyaltyResponse.getReservationCount() - 1)
                .status(loyaltyResponse.getStatus())
                .build();
        restTemplate.exchange(baseUrl, HttpMethod.PUT,
                new HttpEntity<>(loyaltyPut, headers), LoyaltyInfoResponse.class);
    }
}
