package ru.bmstu.kibamba.loyalty.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoyaltyResponse {
    private String username;
    private Integer reservationCount;
    private String status;
    private Integer discount;
}
