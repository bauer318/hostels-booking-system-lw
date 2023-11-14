package ru.bmstu.kibamba.gateway.payload;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class CreateReservationResponse {
    private UUID reservationUid;
    private UUID hotelUid;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private PaymentInfoResponse payment;
    private Integer discount;
}
