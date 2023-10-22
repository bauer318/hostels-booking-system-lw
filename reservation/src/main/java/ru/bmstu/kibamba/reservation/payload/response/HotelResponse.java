package ru.bmstu.kibamba.reservation.payload.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
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