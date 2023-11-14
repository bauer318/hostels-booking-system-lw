package ru.bmstu.kibamba.gateway.payload;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ReservationShortResponse {
    private UUID reservationUid;
    private HotelShortResponse hotel;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private PaymentInfoResponse payment;
}
