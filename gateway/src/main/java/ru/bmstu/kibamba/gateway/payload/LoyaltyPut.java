package ru.bmstu.kibamba.gateway.payload;

import lombok.Data;

@Data
public class LoyaltyPut {
    private String status;
    private Integer reservationCount;
}
