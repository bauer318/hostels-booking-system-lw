package ru.bmstu.kibamba.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.kibamba.reservation.entity.Reservation;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    Optional<Reservation> findByUsername(String username);
    Optional<Reservation> findByReservationUid(UUID reservationUid);
}
