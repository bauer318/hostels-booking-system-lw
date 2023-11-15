package ru.bmstu.kibamba.gateway.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.bmstu.kibamba.gateway.model.Hotel;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
public class ReservationFullResponse {
    private UUID reservationUid;
    private HotelShortResponse hotel;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private PaymentShortResponse payment;
}
