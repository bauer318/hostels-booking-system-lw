package ru.bmstu.kibamba.gateway.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoyaltyShortResponse {
    private String status;
    private Integer discount;
}
