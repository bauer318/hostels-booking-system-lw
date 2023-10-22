package ru.bmstu.kibamba.reservation.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.bmstu.kibamba.reservation.entity.Hotel;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ReservationRequest {
    @NotNull
    private UUID reservationUid;
    @NotNull
    private UUID paymentUid;
    @NotNull
    private Hotel hotel;
    @NotBlank
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
