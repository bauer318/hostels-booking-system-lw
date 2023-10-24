package ru.bmstu.kibamba.gateway.controller;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.bmstu.kibamba.gateway.model.Hotel;
import ru.bmstu.kibamba.gateway.payload.*;

import javax.swing.text.html.parser.Entity;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class GatewayController {
    private final RestTemplate restTemplate;
    private String baseUrl = "http://localhost:8080/api/v1";

    public GatewayController(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    private HttpHeaders createHeader(String xUserName) {
        HttpHeaders headers = createHeader();
        headers.set("X-User-Name", xUserName);
        return headers;
    }

    private HttpHeaders createHeader(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private PaymentRequest createPayment(Integer price) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPaymentUid(UUID.randomUUID());
        paymentRequest.setStatus("PAID");
        paymentRequest.setPrice(price);
        return paymentRequest;
    }

    private Hotel buildHotel(HotelResponse hotelResponse) {
        return Hotel.builder()
                .id(hotelResponse.getId())
                .stars(hotelResponse.getStars())
                .name(hotelResponse.getName())
                .address(hotelResponse.getAddress())
                .country(hotelResponse.getCountry())
                .city(hotelResponse.getCity())
                .hostelUid(hotelResponse.getHostelUid())
                .price(hotelResponse.getPrice())
                .build();
    }

    @GetMapping("/test")
    public String test(@RequestHeader("X-User-Name") String username, @RequestBody ReservationRequest reservationRequest) {
        return "We get " + username + " " + reservationRequest.getStartDate();
    }


    @GetMapping(value = "/hotels", produces = "application/json")
    public ResponseEntity<Object> getHotels(@RequestParam int page, @RequestParam int size) {
        String uri = "http://localhost:8070/api/v1/hotels?page={page}&size={size}";
        ResponseEntity<List> hotelResponse = restTemplate.getForEntity(uri, List.class, page, size);
        return ResponseEntity.ok(hotelResponse.getBody().get(0));
    }

    @GetMapping(value = "/hotels/{hotelUid}", produces = "application/json")
    public HotelResponse getHotel(@PathVariable("hotelUid") UUID hotelUid) {
        String uri = "http://localhost:8070/api/v1/hotels/{hotelUid}";
        return restTemplate.getForEntity(uri, HotelResponse.class, hotelUid).getBody();
    }

    @GetMapping(value = "/loyalty", produces = "application/json")
    public LoyaltyResponse getLoyalty(@RequestHeader("X-User-Name") String xUserName) {
        String uri = "http://localhost:8050/api/v1/loyalty";
        HttpHeaders headers = createHeader(xUserName);
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(uri, HttpMethod.GET, entity, LoyaltyResponse.class).getBody();
    }

    @PutMapping(value = "/loyalty", consumes = "application/json", produces = "application/json")
    public LoyaltyResponse updateLoyalty(@RequestHeader("X-User-Name") String xUserName, @RequestBody LoyaltyPut loyaltyPut) {
        String uri = "http://localhost:8050/api/v1/loyalty";
        HttpHeaders headers = createHeader(xUserName);
        HttpEntity<LoyaltyPut> entity = new HttpEntity<>(loyaltyPut, headers);
        return restTemplate.exchange(uri, HttpMethod.PUT, entity, LoyaltyResponse.class).getBody();
    }

    @PostMapping(value = "/payments", consumes = "application/json", produces = "application/json")
    public PaymentResponse createPayment(@RequestBody PaymentRequest paymentRequest) {
        String uri = "http://localhost:8060/api/v1/payments";
        HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest);
        return restTemplate.postForObject(uri, request, PaymentResponse.class);
    }

    @PutMapping(value = "/payments/{paymentUid}", consumes = "application/json", produces = "application/json")
    public PaymentResponse updatePayment(@PathVariable("paymentUid") UUID paymentUid, @RequestBody PaymentPut paymentPut) {
        String uri = "http://localhost:8060/api/v1/payments/{paymentUid}";
        HttpHeaders headers = createHeader();
        HttpEntity<PaymentPut> entity = new HttpEntity<>(paymentPut,headers);
        return restTemplate.exchange(uri, HttpMethod.PUT, entity, PaymentResponse.class, paymentUid).getBody();
    }

    @GetMapping(value = "/payments/{paymentUid}")
    public PaymentResponse getPayment(@PathVariable("paymentUid") UUID paymentUid) {
        String uri = "http://localhost:8060/api/payments/{paymentUid}";
        return restTemplate.getForObject(uri, PaymentResponse.class, paymentUid);
    }


    @GetMapping(value = "/reservations")
    public ReservationResponse getReservation(@RequestHeader("X-User-Name") String xUserName) {
        String uri = "http://localhost:8070/api/v1/reservations";
        HttpHeaders headers = createHeader(xUserName);
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);
        return restTemplate.exchange(uri, HttpMethod.GET, request, ReservationResponse.class).getBody();

    }

    @PostMapping(value = "/reservations", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ReservationResponse> createReservation(@RequestHeader("X-User-Name") String username,
                                                                 @RequestBody ReservationRequest reservationRequest) {
        String uri = "http://localhost:8070/api/v1/reservations";
        Period period = Period.between(reservationRequest.getStartDate(),
                reservationRequest.getEndDate());

        HotelResponse hotelResponse = restTemplate
                .getForObject(baseUrl + "/hotels/{hotelUid}", HotelResponse.class, reservationRequest.getHotelUid());
        assert hotelResponse != null;
        Hotel hotel = buildHotel(hotelResponse);
        Integer price = hotel.getPrice();
        Integer costWithoutDiscount = period.getDays() * price;

        HttpHeaders headers = createHeader(username);

        HttpEntity<HttpHeaders> loyaltyEntity = new HttpEntity<>(headers);

        LoyaltyResponse loyaltyResponse = restTemplate.exchange(baseUrl + "/loyalty",
                HttpMethod.GET, loyaltyEntity, LoyaltyResponse.class).getBody();
        assert loyaltyResponse != null;
        Integer constWithDiscount = costWithoutDiscount - (costWithoutDiscount * loyaltyResponse.getDiscount() / 100);

        PaymentRequest paymentRequest = createPayment(constWithDiscount);

        HttpEntity<PaymentRequest> entityPayment = new HttpEntity<>(paymentRequest);

        PaymentResponse paymentResponse = restTemplate
                .exchange(baseUrl + "/payments", HttpMethod.POST, entityPayment, PaymentResponse.class).getBody();
        LoyaltyPut loyaltyPut = LoyaltyPut.builder()
                .reservationCount(loyaltyResponse.getReservationCount() + 1)
                .status(loyaltyResponse.getStatus())
                .build();

        HttpEntity<LoyaltyPut> entityLoyaltyPut = new HttpEntity<>(loyaltyPut, headers);
        restTemplate.exchange(baseUrl + "/loyalty", HttpMethod.PUT, entityLoyaltyPut, LoyaltyResponse.class);

        assert paymentResponse != null;
        ReservationRequestFull reservationRequestFull = ReservationRequestFull
                .builder()
                .reservationUid(UUID.randomUUID())
                .startDate(reservationRequest.getStartDate())
                .endDate(reservationRequest.getEndDate())
                .paymentUid(paymentResponse.getPaymentUid())
                .status("PAID")
                .hotel(hotel)
                .build();

        var request = new HttpEntity<>(reservationRequestFull, headers);

        return restTemplate.exchange(uri, HttpMethod.POST, request, ReservationResponse.class);

    }

    @DeleteMapping("/reservations/{reservationUid}")
    public void cancelReservation(@RequestHeader("X-User-Name") String xUserName,
                                                  @PathVariable("reservationUid") UUID reservationUid) {
        HttpHeaders headers = createHeader(xUserName);
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);

        ReservationResponse reservationResponse = restTemplate.exchange(
                baseUrl + "/reservations", HttpMethod.GET, request, ReservationResponse.class
        ).getBody();

        assert reservationResponse != null;
        UUID paymentUid = reservationResponse.getPaymentUid();

        HttpHeaders paymentHeaders = createHeader();

        PaymentPut paymentPut = new PaymentPut();
        paymentPut.setStatus("CANCELED");
        HttpEntity<PaymentPut> paymentRequest = new HttpEntity<>(paymentPut, paymentHeaders);

        restTemplate.exchange(baseUrl+"/payments/{paymentUid}",HttpMethod.PUT, paymentRequest, PaymentResponse.class,paymentUid);

        HttpEntity<HttpHeaders> loyaltyEntity = new HttpEntity<>(headers);

        LoyaltyResponse loyaltyResponse = restTemplate.exchange(baseUrl + "/loyalty",
                HttpMethod.GET, loyaltyEntity, LoyaltyResponse.class).getBody();
        assert loyaltyResponse != null;
        LoyaltyPut loyaltyPut = LoyaltyPut.builder()
                .reservationCount(loyaltyResponse.getReservationCount() - 1)
                .status(loyaltyResponse.getStatus())
                .build();
        var loyaltyRequest = new HttpEntity<>(loyaltyPut, headers);

        restTemplate.exchange(baseUrl+"/loyalty",HttpMethod.PUT,loyaltyRequest, LoyaltyResponse.class);

        String uri = "http://localhost:8070/api/v1/reservations/{reservationUid}";
        restTemplate.delete(uri,reservationUid);

    }


}
