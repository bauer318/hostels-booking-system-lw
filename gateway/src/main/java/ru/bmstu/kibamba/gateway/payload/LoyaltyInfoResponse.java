package ru.bmstu.kibamba.gateway.payload;

import lombok.Data;

@Data
public class LoyaltyInfoResponse {
    private Integer reservationCount;
    private String status;
    private Integer discount;
}
