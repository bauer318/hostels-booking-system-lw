package ru.bmstu.kibamba.payment.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentRequest {
    private UUID paymentUid;
    private String status;
    private Integer price;
}
