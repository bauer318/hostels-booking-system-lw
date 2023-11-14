package ru.bmstu.kibamba.gateway.payload;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class HotelShortResponse {
    private UUID hotelUid;
    private String name;
    private String fullAddress;
    private Integer stars;
}
