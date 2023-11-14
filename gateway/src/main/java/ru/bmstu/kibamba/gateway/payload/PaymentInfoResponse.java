package ru.bmstu.kibamba.gateway.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentInfoResponse {
    private String status;
    private Integer price;
}
