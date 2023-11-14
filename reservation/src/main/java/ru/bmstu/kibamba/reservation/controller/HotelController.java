package ru.bmstu.kibamba.reservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.kibamba.reservation.payload.response.HotelResponse;
import ru.bmstu.kibamba.reservation.payload.response.PaginationResponse;
import ru.bmstu.kibamba.reservation.service.HotelServiceImpl;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {
    private final HotelServiceImpl hotelService;

    @Autowired
    public HotelController(HotelServiceImpl hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping(produces = "application/json")
    public PaginationResponse getHotels(@RequestParam(required = false, defaultValue = "0") int page,
                                        @RequestParam(required = false, defaultValue = "5") int size) {
        return hotelService.getHotels(page, size);
    }

    @GetMapping(value = "/{hotelUid}", produces = "application/json")
    public HotelResponse getHotel(@PathVariable("hotelUid") UUID hotelUid) {
        return hotelService.getHotel(hotelUid);
    }

}
