package ru.bmstu.kibamba.gateway.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.bmstu.kibamba.gateway.model.Hotel;
import ru.bmstu.kibamba.gateway.payload.*;

import java.time.Period;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class GatewayController {
    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/v1";
    private final CircuitBreaker timeCircuitBreaker;

    @Autowired
    public GatewayController(RestTemplateBuilder builder, CircuitBreaker timeCircuitBreaker) {
        this.restTemplate = builder.build();
        this.timeCircuitBreaker = timeCircuitBreaker;
    }

    private void fallbackTest(String message){
        System.out.println(message);
    }

    private HttpHeaders createHeader(String xUserName) {
        HttpHeaders headers = createHeader();
        headers.set("X-User-Name", xUserName);
        return headers;
    }

    private HttpHeaders createHeader() {
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
                .hotelUid(hotelResponse.getHotelUid())
                .price(hotelResponse.getPrice())
                .build();
    }

    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity<UserInfoResponse> getMe(@RequestHeader("X-User-Name") String xUserName) {
        UserInfoResponse userInfo = new UserInfoResponse();
        userInfo.setReservations(new ArrayList<>());
        HttpHeaders headers = createHeader(xUserName);
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);
        ReservationShortResponse[] reservationShortResponses = restTemplate.exchange(
                baseUrl+"/reservations",HttpMethod.GET,request,ReservationShortResponse[].class
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


    @GetMapping(value = "/hotels", produces = "application/json")
    public ResponseEntity<PaginationResponse> getHotels(@RequestParam(required = false, defaultValue = "0") int page,
                                                        @RequestParam(required = false, defaultValue = "5") int size) {
        String uri = "http://localhost:8070/api/v1/hotels?page={page}&size={size}";
        ResponseEntity<PaginationResponse> paginationResponse = restTemplate.getForEntity(uri, PaginationResponse.class, page, size);
        return ResponseEntity.ok(paginationResponse.getBody());
    }

    @GetMapping(value = "/hotels/{hotelUid}", produces = "application/json")
    public HotelResponse getHotel(@PathVariable("hotelUid") UUID hotelUid) {
        String uri = "http://localhost:8070/api/v1/hotels/{hotelUid}";
        return restTemplate.getForEntity(uri, HotelResponse.class, hotelUid).getBody();
    }

    @GetMapping(value = "/loyalty", produces = "application/json")
    public LoyaltyInfoResponse getLoyalty(@RequestHeader("X-User-Name") String xUserName) {
        String uri = "http://localhost:8050/api/v1/loyalty";
        HttpHeaders headers = createHeader(xUserName);
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(uri, HttpMethod.GET, entity, LoyaltyInfoResponse.class).getBody();
    }

    @PutMapping(value = "/loyalty", consumes = "application/json", produces = "application/json")
    public LoyaltyInfoResponse updateLoyalty(@RequestHeader("X-User-Name") String xUserName, @RequestBody LoyaltyPut loyaltyPut) {
        String uri = "http://localhost:8050/api/v1/loyalty";
        HttpHeaders headers = createHeader(xUserName);
        HttpEntity<LoyaltyPut> entity = new HttpEntity<>(loyaltyPut, headers);
        return restTemplate.exchange(uri, HttpMethod.PUT, entity, LoyaltyInfoResponse.class).getBody();
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
        HttpEntity<PaymentPut> entity = new HttpEntity<>(paymentPut, headers);
        return restTemplate.exchange(uri, HttpMethod.PUT, entity, PaymentResponse.class, paymentUid).getBody();
    }

    @GetMapping(value = "/payments/{paymentUid}")
    public PaymentResponse getPayment(@PathVariable("paymentUid") UUID paymentUid) {
        String uri = "http://localhost:8060/api/v1/payments/{paymentUid}";
        return restTemplate.getForObject(uri, PaymentResponse.class, paymentUid);
    }


    @GetMapping(value = "/reservations")
    public List<ReservationShortResponse> getReservations(@RequestHeader("X-User-Name") String xUserName) {
        String uri = "http://localhost:8070/api/v1/reservations";
        HttpHeaders headers = createHeader(xUserName);
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);
        ReservationResponse[] reservationResponses = restTemplate.exchange(uri, HttpMethod.GET, request, ReservationResponse[].class).getBody();
        List<ReservationShortResponse> results = new ArrayList<>();
        assert reservationResponses != null;
        for (ReservationResponse reservationResponse : reservationResponses) {
            PaymentResponse payment = restTemplate.getForObject(baseUrl + "/payments/{paymentUid}",
                    PaymentResponse.class, reservationResponse.getPaymentUid());
            assert payment != null;
            results.add(ReservationShortResponse
                    .builder()
                    .reservationUid(reservationResponse.getReservationUid())
                    .endDate(reservationResponse.getEndDate())
                    .hotel(HotelShortResponse.builder()
                            .hotelUid(reservationResponse.getHotel().getHotelUid())
                            .fullAddress(buildFullAddress(reservationResponse.getHotel()))
                            .stars(reservationResponse.getHotel().getStars())
                            .name(reservationResponse.getHotel().getName())
                            .build())
                    .payment(PaymentInfoResponse
                            .builder()
                            .price(payment.getPrice())
                            .status(payment.getStatus())
                            .build())
                    .startDate(reservationResponse.getStartDate())
                    .status(reservationResponse.getStatus())
                    .build());
        }
        return results;
    }

    private String buildFullAddress(Hotel hotel){
        return hotel.getCountry().concat(", ").concat(hotel.getCity()).concat(", ").concat(hotel.getAddress());
    }

    @GetMapping(value = "/reservations/{reservationUid}")
    public ReservationShortResponse getReservation(@RequestHeader("X-User-Name") String xUserName,
                                                   @PathVariable("reservationUid") UUID reservationUid) {
        ReservationResponse reservation = getReservationLong(xUserName, reservationUid);
        assert reservation != null;
        PaymentResponse payment = restTemplate.getForObject(baseUrl + "/payments/{paymentUid}",
                PaymentResponse.class, reservation.getPaymentUid());

        Hotel hotel = reservation.getHotel();
        String fullAddress = hotel.getCountry() + ", " + hotel.getCity() + ", " + hotel.getAddress();

        assert payment != null;
        return ReservationShortResponse
                .builder()
                .reservationUid(reservationUid)
                .hotel(HotelShortResponse
                        .builder()
                        .hotelUid(hotel.getHotelUid())
                        .name(hotel.getName())
                        .fullAddress(fullAddress)
                        .stars(hotel.getStars())
                        .build())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .status(reservation.getStatus())
                .payment(PaymentInfoResponse
                        .builder()
                        .status(payment.getStatus())
                        .price(payment.getPrice())
                        .build()
                )
                .build();
    }

    private ReservationResponse getReservationLong(String xUserName, UUID reservationUid){
        String uri = "http://localhost:8070/api/v1/reservations/{reservationUid}";
        HttpHeaders headers = createHeader(xUserName);
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);
        return restTemplate
                .exchange(uri, HttpMethod.GET, request, ReservationResponse.class, reservationUid).getBody();
    }

    @PostMapping(value = "/reservations", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CreateReservationResponse> createReservation(@RequestHeader("X-User-Name") String username,
                                                                       @RequestBody CreateReservationRequest reservationRequest) {
        String uri = "http://localhost:8070/api/v1/reservations";
        Period period = Period.between(reservationRequest.getStartDate(),
                reservationRequest.getEndDate());

        HotelResponse hotelResponse = restTemplate
                .getForObject(baseUrl + "/hotels/{hotelUid}", HotelResponse.class, reservationRequest.getHotelUid());


        assert hotelResponse != null;
        Hotel hotel = buildHotel(hotelResponse);
        Integer price = hotel.getPrice();
        int costWithoutDiscount = period.getDays() * price;

        HttpHeaders headers = createHeader(username);

        HttpEntity<HttpHeaders> loyaltyEntity = new HttpEntity<>(headers);

        LoyaltyInfoResponse loyaltyResponse = restTemplate.exchange(baseUrl + "/loyalty",
                HttpMethod.GET, loyaltyEntity, LoyaltyInfoResponse.class).getBody();
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
        restTemplate.exchange(baseUrl + "/loyalty", HttpMethod.PUT, entityLoyaltyPut, LoyaltyInfoResponse.class);

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

        ReservationResponse reservationResponse =
                restTemplate.exchange(uri, HttpMethod.POST, request, ReservationResponse.class).getBody();

        assert reservationResponse != null;
        CreateReservationResponse createReservationResponse =
                CreateReservationResponse.builder()
                        .reservationUid(reservationResponse.getReservationUid())
                        .hotelUid(hotel.getHotelUid())
                        .startDate(reservationResponse.getStartDate())
                        .endDate(reservationResponse.getEndDate())
                        .discount(loyaltyResponse.getDiscount())
                        .status(reservationResponse.getStatus())
                        .payment(PaymentInfoResponse.builder()
                                .status(paymentResponse.getStatus())
                                .price(paymentResponse.getPrice())
                                .build())
                        .build();

        return ResponseEntity.ok(createReservationResponse);

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/reservations/{reservationUid}")
    public void cancelReservation(@RequestHeader("X-User-Name") String xUserName,
                                  @PathVariable("reservationUid") UUID reservationUid) {
        HttpHeaders headers = createHeader(xUserName);
        ReservationResponse reservation = getReservationLong(xUserName, reservationUid);
        assert reservation != null;
        PaymentResponse payment = restTemplate.getForObject(baseUrl + "/payments/{paymentUid}",
                PaymentResponse.class, reservation.getPaymentUid());

        assert payment != null;
        UUID paymentUid = payment.getPaymentUid();

        HttpHeaders paymentHeaders = createHeader();

        PaymentPut paymentPut = new PaymentPut();
        paymentPut.setStatus("CANCELED");
        HttpEntity<PaymentPut> paymentRequest = new HttpEntity<>(paymentPut, paymentHeaders);

        restTemplate.exchange(baseUrl + "/payments/{paymentUid}", HttpMethod.PUT, paymentRequest, PaymentResponse.class, paymentUid);

        HttpEntity<HttpHeaders> loyaltyEntity = new HttpEntity<>(headers);

        LoyaltyInfoResponse loyaltyResponse = restTemplate.exchange(baseUrl + "/loyalty",
                HttpMethod.GET, loyaltyEntity, LoyaltyInfoResponse.class).getBody();
        assert loyaltyResponse != null;
        LoyaltyPut loyaltyPut = LoyaltyPut.builder()
                .reservationCount(loyaltyResponse.getReservationCount() - 1)
                .status(loyaltyResponse.getStatus())
                .build();
        var loyaltyRequest = new HttpEntity<>(loyaltyPut, headers);

        restTemplate.exchange(baseUrl + "/loyalty", HttpMethod.PUT, loyaltyRequest, LoyaltyInfoResponse.class);

        String uriToDelete = "http://localhost:8070/api/v1/reservations/{reservationUid}";
        restTemplate.delete(uriToDelete, reservationUid);

    }


}
