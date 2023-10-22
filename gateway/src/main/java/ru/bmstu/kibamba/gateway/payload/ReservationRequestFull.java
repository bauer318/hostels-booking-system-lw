package ru.bmstu.kibamba.gateway.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.bmstu.kibamba.gateway.model.Hotel;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
public class ReservationRequestFull {
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
