package ru.bmstu.kibamba.reservation.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.kibamba.reservation.payload.request.ReservationRequest;
import ru.bmstu.kibamba.reservation.payload.response.ReservationResponse;
import ru.bmstu.kibamba.reservation.service.ReservationServiceImpl;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    private final ReservationServiceImpl reservationService;

    @Autowired
    public ReservationController(ReservationServiceImpl reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping(produces = "application/json")
    public ReservationResponse getReservation(@RequestHeader("X-User-Name") String xUserName) {
        return reservationService.getReservation(xUserName);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ReservationResponse create(@RequestHeader("X-User-Name") String xUserName, @RequestBody @Valid ReservationRequest reservationRequest) {
        return reservationService.createReservation(xUserName, reservationRequest);
    }


    @DeleteMapping("/{reservationUid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("reservationUid") UUID reservationUid) {
        reservationService.deleteReservation(reservationUid);
    }
}
