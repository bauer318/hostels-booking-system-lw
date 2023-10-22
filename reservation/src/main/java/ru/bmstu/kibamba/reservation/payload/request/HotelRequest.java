package ru.bmstu.kibamba.reservation.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class HotelRequest {
    @NotNull
    private UUID hostelUid;
    @NotBlank
    private String name;
    @NotBlank
    private String country;
    @NotBlank
    private String city;
    @NotBlank
    private String address;
    @NotNull
    private Integer stars;
    @NotNull
    private Integer price;
}
