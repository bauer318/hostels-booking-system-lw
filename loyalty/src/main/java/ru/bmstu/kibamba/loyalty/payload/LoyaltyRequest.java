package ru.bmstu.kibamba.loyalty.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoyaltyRequest {
    private String username;
    private Integer reservationCount;
    private String status;
    private Integer discount;
}
