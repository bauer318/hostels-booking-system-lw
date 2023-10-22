package ru.bmstu.kibamba.reservation.service;

import org.springframework.stereotype.Component;
import ru.bmstu.kibamba.reservation.entity.Hotel;
import ru.bmstu.kibamba.reservation.payload.response.HotelResponse;

import java.util.List;
import java.util.UUID;

@Component
public interface HotelService {
    List<HotelResponse> getHotels(Integer page, Integer size);

    boolean existsByHostelUid(UUID hostelUid);

    HotelResponse getHotel(UUID hostelUid);

}
