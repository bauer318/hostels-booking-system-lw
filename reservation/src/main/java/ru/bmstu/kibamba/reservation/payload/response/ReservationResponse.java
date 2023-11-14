package ru.bmstu.kibamba.reservation.payload.response;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.bmstu.kibamba.reservation.entity.Hotel;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ReservationResponse {
    private UUID reservationUid;
    private String username;
    private UUID paymentUid;
    private Hotel hotel;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
