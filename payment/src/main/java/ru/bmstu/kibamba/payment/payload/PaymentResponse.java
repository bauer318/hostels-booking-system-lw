package ru.bmstu.kibamba.payment.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Integer id;
    private UUID paymentUid;
    private String status;
    private Integer price;
}
