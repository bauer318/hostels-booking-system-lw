package ru.bmstu.kibamba.gateway.payload;

import lombok.Builder;
import lombok.Data;
import ru.bmstu.kibamba.gateway.model.Hotel;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ReservationLongResponse {
    private UUID reservationUid;
    private PaymentResponse payment;
    private Hotel hotel;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
