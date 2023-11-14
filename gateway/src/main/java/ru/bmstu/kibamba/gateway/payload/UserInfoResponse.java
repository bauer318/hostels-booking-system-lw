package ru.bmstu.kibamba.gateway.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private List<ReservationLongResponse> reservations;
    private LoyaltyInfoResponse loyalty;
}
