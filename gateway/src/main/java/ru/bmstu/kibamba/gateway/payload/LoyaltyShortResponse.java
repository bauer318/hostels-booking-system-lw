package ru.bmstu.kibamba.gateway.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoyaltyShortResponse {
    private String status;
    private Integer discount;
}
