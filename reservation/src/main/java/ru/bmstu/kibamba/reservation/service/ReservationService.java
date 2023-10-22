package ru.bmstu.kibamba.reservation.service;

import ru.bmstu.kibamba.reservation.payload.request.ReservationRequest;
import ru.bmstu.kibamba.reservation.payload.response.ReservationResponse;

import java.util.UUID;

public interface ReservationService {
    ReservationResponse getReservation(String username);

    ReservationResponse createReservation(String username,ReservationRequest reservationRequest);

    void deleteReservation(UUID reservationUid);
}
