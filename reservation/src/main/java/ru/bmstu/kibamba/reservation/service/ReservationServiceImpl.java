package ru.bmstu.kibamba.reservation.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bmstu.kibamba.reservation.entity.Reservation;
import ru.bmstu.kibamba.reservation.payload.request.ReservationRequest;
import ru.bmstu.kibamba.reservation.payload.response.ReservationResponse;
import ru.bmstu.kibamba.reservation.repository.ReservationRepository;

import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final HotelServiceImpl hotelService;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  HotelServiceImpl hotelService) {
        this.reservationRepository = reservationRepository;
        this.hotelService = hotelService;
    }

    private ReservationResponse buildReservationResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .paymentUid(reservation.getPaymentUid())
                .startDate(reservation.getStartDate())
                .status(reservation.getStatus())
                .hotel(reservation.getHotel())
                .id(reservation.getId())
                .endDate(reservation.getEndDate())
                .reservationUid(reservation.getReservationUid())
                .username(reservation.getUsername())
                .build();
    }

    private Reservation buildReservation(String username, ReservationRequest request) {
        Reservation reservation = new Reservation();
        reservation.setReservationUid(request.getReservationUid());
        reservation.setHotel(request.getHotel());
        reservation.setStatus(request.getStatus());
        reservation.setUsername(username);
        reservation.setEndDate(request.getEndDate());
        reservation.setStartDate(request.getStartDate());
        reservation.setPaymentUid(request.getPaymentUid());
        return reservation;
    }

    @Override
    public ReservationResponse getReservation(String username) {
        return buildReservationResponse(reservationRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Not found reservation for user " + username)
        ));
    }

    @Override
    public ReservationResponse createReservation(String username, ReservationRequest reservationRequest) {
        UUID hotelUid = reservationRequest.getHotel().getHostelUid();
        if (!hotelService.existsByHostelUid(hotelUid)) {
            throw new EntityNotFoundException("Not exist hostel with uid " + hotelUid);
        }
        return buildReservationResponse(reservationRepository.save(buildReservation(username, reservationRequest)));
    }

    @Override
    public void deleteReservation(UUID reservationUid) {
        Reservation reservation = reservationRepository.findByReservationUid(reservationUid).orElseThrow(
                () -> new EntityNotFoundException("Not found reservation by uid " + reservationUid)
        );
        reservationRepository.delete(reservation);
    }
}
