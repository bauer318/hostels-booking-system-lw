package ru.bmstu.kibamba.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class Hotel {
    private Integer id;

    private UUID hotelUid;

    private String name;

    private String country;

    private String city;

    private String address;

    private Integer stars;

    private Integer price;
}
