package ru.bmstu.kibamba.reservation.service;

import ru.bmstu.kibamba.reservation.payload.request.ReservationRequest;
import ru.bmstu.kibamba.reservation.payload.response.ReservationResponse;

import java.util.List;
import java.util.UUID;

public interface ReservationService {
    ReservationResponse getReservation(String username);

    ReservationResponse getReservation(UUID reservationUid);

    List<ReservationResponse> getReservations(String username);

    ReservationResponse createReservation(String username, ReservationRequest reservationRequest);

    void deleteReservation(UUID reservationUid);
}
