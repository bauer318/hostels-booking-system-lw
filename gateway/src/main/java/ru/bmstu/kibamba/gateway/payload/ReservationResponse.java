package ru.bmstu.kibamba.gateway.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.bmstu.kibamba.gateway.model.Hotel;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
public class ReservationResponse {
    private Integer id;
    private UUID reservationUid;
    private String username;
    private UUID paymentUid;
    private Hotel hotel;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
