package ru.bmstu.kibamba.gateway.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import ru.bmstu.kibamba.gateway.model.Hotel;
import ru.bmstu.kibamba.gateway.payload.*;

import java.util.Collections;
import java.util.UUID;

@Service
public class GatewayService {

    public HttpHeaders createHeader(String xUserName) {
        HttpHeaders headers = createHeader();
        headers.set("X-User-Name", xUserName);
        return headers;
    }

    public HttpHeaders createHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public PaymentRequest createPayment(Integer price) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPaymentUid(UUID.randomUUID());
        paymentRequest.setStatus("PAID");
        paymentRequest.setPrice(price);
        return paymentRequest;
    }

    public Hotel buildHotel(HotelResponse hotelResponse) {
        return Hotel.builder()
                .id(hotelResponse.getId())
                .stars(hotelResponse.getStars())
                .name(hotelResponse.getName())
                .address(hotelResponse.getAddress())
                .country(hotelResponse.getCountry())
                .city(hotelResponse.getCity())
                .hotelUid(hotelResponse.getHotelUid())
                .price(hotelResponse.getPrice())
                .build();
    }

}
