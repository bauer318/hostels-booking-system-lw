package ru.bmstu.kibamba.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.bmstu.kibamba.gateway.exception.ServiceUnavailableException;
import ru.bmstu.kibamba.gateway.model.Hotel;
import ru.bmstu.kibamba.gateway.payload.*;
import ru.bmstu.kibamba.gateway.service.GatewayService;
import ru.bmstu.kibamba.gateway.service.LoyaltyService;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ReservationServiceController {
    private final RestTemplate restTemplate;
    private final GatewayService gatewayService;
    private final String reservationServiceBaseUrl = "http://localhost:8070/api/v1/reservations";

    private final String paymentBaseUrl = "http://localhost:8080/api/v1/payments";

    private final String loyaltyBaseUrl = "http://localhost:8080/api/v1/loyalty";
    private int count = 0;
    private final LoyaltyService loyaltyService;


    @Autowired
    public ReservationServiceController(RestTemplate restTemplate,
                                        GatewayService gatewayService,
                                        LoyaltyService loyaltyService) {
        this.restTemplate = restTemplate;
        this.gatewayService = gatewayService;
        this.loyaltyService = loyaltyService;
    }

    private String buildFullAddress(Hotel hotel) {
        return hotel.getCountry().concat(", ").concat(hotel.getCity()).concat(", ").concat(hotel.getAddress());
    }

    private CreateReservationResponse buildCreateReservationResponse(Hotel hotel,
                                                                     ReservationResponse reservationResponse,
                                                                     LoyaltyInfoResponse loyaltyResponse,
                                                                     PaymentResponse paymentResponse) {
        return CreateReservationResponse.builder()
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

    }

    private ReservationResponse getReservationLong(String xUserName, UUID reservationUid) {
        String uri = reservationServiceBaseUrl.concat("/{reservationUid}");
        HttpHeaders headers = gatewayService.createHeader(xUserName);
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);
        return restTemplate
                .exchange(uri, HttpMethod.GET, request, ReservationResponse.class, reservationUid).getBody();
    }


    private ReservationRequestFull buildReservationRequestFull(CreateReservationRequest reservationRequest,
                                                               PaymentResponse paymentResponse,
                                                               Hotel hotel) {
        return ReservationRequestFull
                .builder()
                .reservationUid(UUID.randomUUID())
                .startDate(reservationRequest.getStartDate())
                .endDate(reservationRequest.getEndDate())
                .paymentUid(paymentResponse.getPaymentUid())
                .status("PAID")
                .hotel(hotel)
                .build();

    }

    private LoyaltyInfoResponse getLoyaltyResponse(HttpEntity<HttpHeaders> loyaltyEntity) {
        LoyaltyInfoResponse loyaltyResponse = null;
        try {
            loyaltyResponse = restTemplate.exchange(loyaltyBaseUrl,
                    HttpMethod.GET, loyaltyEntity, LoyaltyInfoResponse.class).getBody();
        } catch (Exception e) {
            throw new ServiceUnavailableException("Loyalty Service unavailable");
        }

        return loyaltyResponse;
    }

    @GetMapping("/manage/health")
    public ResponseEntity<String> manageHealth() {
        try {
            String healthUri = reservationServiceBaseUrl.concat("/manage/health");
            return restTemplate.getForEntity(healthUri, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Reservation service unavailable");
        }
    }

    @GetMapping(value = "/reservations", produces = "application/json")
    public List<ReservationShortResponse> getReservations(@RequestHeader("X-User-Name") String xUserName) {
        HttpHeaders headers = gatewayService.createHeader(xUserName);
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);
        ReservationResponse[] reservationResponses = restTemplate.exchange(reservationServiceBaseUrl,
                HttpMethod.GET, request, ReservationResponse[].class).getBody();
        List<ReservationShortResponse> results = new ArrayList<>();
        assert reservationResponses != null;
        for (ReservationResponse reservationResponse : reservationResponses) {
            PaymentResponse payment = restTemplate.getForObject(paymentBaseUrl.concat("/{paymentUid}"),
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

    @GetMapping(value = "/reservations/{reservationUid}", produces = "application/json")
    public ReservationShortResponse getReservation(@RequestHeader("X-User-Name") String xUserName,
                                                   @PathVariable("reservationUid") UUID reservationUid) {
        ReservationResponse reservation = getReservationLong(xUserName, reservationUid);
        assert reservation != null;
        PaymentResponse payment = restTemplate.getForObject(paymentBaseUrl.concat("/{paymentUid}"),
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

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/reservations/{reservationUid}")
    public void cancelReservation(@RequestHeader("X-User-Name") String xUserName,
                                  @PathVariable("reservationUid") UUID reservationUid) {
        HttpHeaders headers = gatewayService.createHeader(xUserName);
        ReservationResponse reservation = getReservationLong(xUserName, reservationUid);
        assert reservation != null;
        PaymentResponse payment = restTemplate.getForObject(paymentBaseUrl.concat("/{paymentUid}"),
                PaymentResponse.class, reservation.getPaymentUid());

        assert payment != null;
        UUID paymentUid = payment.getPaymentUid();

        HttpHeaders paymentHeaders = gatewayService.createHeader();

        PaymentPut paymentPut = new PaymentPut();
        paymentPut.setStatus("CANCELED");
        HttpEntity<PaymentPut> paymentRequest = new HttpEntity<>(paymentPut, paymentHeaders);

        restTemplate.exchange(paymentBaseUrl.concat("/{paymentUid}"), HttpMethod.PUT, paymentRequest, PaymentResponse.class, paymentUid);

        restTemplate.exchange("http://localhost:8070/api/v1/reservations/{reservationUid}",HttpMethod.PUT,paymentRequest, ReservationResponse.class, reservationUid);

        HttpEntity<HttpHeaders> loyaltyEntity = new HttpEntity<>(headers);

        LoyaltyInfoResponse responseLoyalty = restTemplate.exchange(loyaltyBaseUrl, HttpMethod.PUT, loyaltyEntity, LoyaltyInfoResponse.class).getBody();
        if (responseLoyalty != null) {
            String uriToDelete = "http://localhost:8070/api/v1/reservations/{reservationUid}";
            restTemplate.delete(uriToDelete, reservationUid);
        }

    }

    @PostMapping(value = "/reservations", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CreateReservationResponse> createReservation(@RequestHeader("X-User-Name") String username,
                                                                       @RequestBody CreateReservationRequest reservationRequest) {
        String uri = "http://localhost:8070/api/v1/reservations";
        Period period = Period.between(reservationRequest.getStartDate(),
                reservationRequest.getEndDate());

        String baseUrl = "http://localhost:8080/api/v1";
        HotelResponse hotelResponse = restTemplate
                .getForObject(baseUrl + "/hotels/{hotelUid}", HotelResponse.class, reservationRequest.getHotelUid());


        assert hotelResponse != null;
        Hotel hotel = gatewayService.buildHotel(hotelResponse);
        Integer price = hotel.getPrice();
        int costWithoutDiscount = period.getDays() * price;

        HttpHeaders headers = gatewayService.createHeader(username);

        HttpEntity<HttpHeaders> loyaltyEntity = new HttpEntity<>(headers);
        LoyaltyInfoResponse loyaltyResponse = getLoyaltyResponse(loyaltyEntity);
        assert loyaltyResponse != null;
        Integer constWithDiscount = costWithoutDiscount - (costWithoutDiscount * loyaltyResponse.getDiscount() / 100);

        PaymentRequest paymentRequest = gatewayService.createPayment(constWithDiscount);

        HttpEntity<PaymentRequest> entityPayment = new HttpEntity<>(paymentRequest);

        PaymentResponse paymentResponse = restTemplate
                .exchange(paymentBaseUrl, HttpMethod.POST, entityPayment, PaymentResponse.class).getBody();
        LoyaltyPut loyaltyPut = LoyaltyPut.builder()
                .reservationCount(loyaltyResponse.getReservationCount() + 1)
                .status(loyaltyResponse.getStatus())
                .build();

        HttpEntity<LoyaltyPut> entityLoyaltyPut = new HttpEntity<>(loyaltyPut, headers);
        restTemplate.exchange(loyaltyBaseUrl.concat("/up"), HttpMethod.PUT, entityLoyaltyPut, LoyaltyInfoResponse.class);

        assert paymentResponse != null;
        ReservationRequestFull reservationRequestFull = buildReservationRequestFull(reservationRequest, paymentResponse, hotel);
        var request = new HttpEntity<>(reservationRequestFull, headers);

        ReservationResponse reservationResponse =
                restTemplate.exchange(uri, HttpMethod.POST, request, ReservationResponse.class).getBody();

        assert reservationResponse != null;
        CreateReservationResponse createReservationResponse = buildCreateReservationResponse(
                hotel, reservationResponse, loyaltyResponse, paymentResponse
        );

        return ResponseEntity.ok(createReservationResponse);

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

}
