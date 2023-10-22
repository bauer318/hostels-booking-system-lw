package ru.bmstu.kibamba.gateway.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@ToString
@Getter
@Setter
public class HotelResponse {
    private Integer id;

    private UUID hostelUid;

    private String name;

    private String country;

    private String city;

    private String address;

    private Integer stars;

    private Integer price;
}
