package ru.bmstu.kibamba.gateway.payload;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentRequest {
    private UUID paymentUid;
    private String status;
    private Integer price;
}
