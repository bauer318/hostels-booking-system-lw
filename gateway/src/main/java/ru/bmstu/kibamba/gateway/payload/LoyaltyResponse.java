package ru.bmstu.kibamba.gateway.payload;

import lombok.Data;

@Data
public class LoyaltyResponse {
    private Integer id;
    private String username;
    private Integer reservationCount;
    private String status;
    private Integer discount;
}
