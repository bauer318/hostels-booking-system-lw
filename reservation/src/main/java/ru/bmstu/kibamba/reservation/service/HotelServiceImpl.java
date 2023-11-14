package ru.bmstu.kibamba.reservation.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.bmstu.kibamba.reservation.entity.Hotel;
import ru.bmstu.kibamba.reservation.payload.request.HotelRequest;
import ru.bmstu.kibamba.reservation.payload.response.HotelResponse;
import ru.bmstu.kibamba.reservation.payload.response.PaginationResponse;
import ru.bmstu.kibamba.reservation.repository.HotelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class HotelServiceImpl implements HotelService {
    private final HotelRepository hotelRepository;

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    private HotelResponse buildHotelResponse(Hotel hotel) {
        return HotelResponse.builder()
                .price(hotel.getPrice())
                .id(hotel.getId())
                .stars(hotel.getStars())
                .city(hotel.getCity())
                .country(hotel.getCountry())
                .address(hotel.getAddress())
                .name(hotel.getName())
                .hotelUid(hotel.getHotelUid())
                .build();
    }

    private Hotel buildHotel(HotelRequest request) {
        Hotel hotel = new Hotel();
        hotel.setAddress(request.getAddress());
        hotel.setHotelUid(request.getHotelUid());
        hotel.setName(request.getName());
        hotel.setCity(request.getCity());
        hotel.setCountry(request.getCountry());
        hotel.setPrice(request.getPrice());
        hotel.setStars(request.getStars());
        return hotel;
    }

    @Override
    public PaginationResponse getHotels(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<HotelResponse> pageResponse = hotelRepository.findAll(pageable).map(this::buildHotelResponse);
        List<HotelResponse> items = new ArrayList<>(pageResponse.getContent());

        return PaginationResponse.builder()
                .items(items)
                .page(page)
                .pageSize(size)
                .totalElements(pageResponse.getTotalElements())
                .build();
    }

    @Override
    public boolean existsByHostelUid(UUID hostelUid) {
        return hotelRepository.existsByHotelUid(hostelUid);
    }

    @Override
    public HotelResponse getHotel(UUID hostelUid) {
        return buildHotelResponse(hotelRepository.findByHotelUid(hostelUid).orElseThrow(
                () -> new EntityNotFoundException("Not found hotel uid " + hostelUid)
        ));
    }
}
