package ru.bmstu.kibamba.loyalty.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoyaltyPut {
    private String status;
    private Integer reservationCount;
}
