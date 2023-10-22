package ru.bmstu.kibamba.reservation.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.bmstu.kibamba.reservation.entity.Hotel;
import ru.bmstu.kibamba.reservation.payload.request.HotelRequest;
import ru.bmstu.kibamba.reservation.payload.response.HotelResponse;
import ru.bmstu.kibamba.reservation.repository.HotelRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService {
    private final HotelRepository hotelRepository;

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    private HotelResponse buildHotelResponse(Hotel hotel) {
        return HotelResponse.builder()
                .id(hotel.getId())
                .price(hotel.getPrice())
                .stars(hotel.getStars())
                .city(hotel.getCity())
                .country(hotel.getCountry())
                .address(hotel.getAddress())
                .name(hotel.getName())
                .hostelUid(hotel.getHostelUid())
                .build();
    }

    private Hotel buildHotel(HotelRequest request) {
        Hotel hotel = new Hotel();
        hotel.setAddress(request.getAddress());
        hotel.setHostelUid(request.getHostelUid());
        hotel.setName(request.getName());
        hotel.setCity(request.getCity());
        hotel.setCountry(request.getCountry());
        hotel.setPrice(request.getPrice());
        hotel.setStars(request.getStars());
        return hotel;
    }

    @Override
    public List<HotelResponse> getHotels(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return hotelRepository.findAll(pageable).stream().map(this::buildHotelResponse).collect(Collectors.toList());
    }

    @Override
    public boolean existsByHostelUid(UUID hostelUid) {
        return hotelRepository.existsByHostelUid(hostelUid);
    }

    @Override
    public HotelResponse getHotel(UUID hostelUid) {
        return buildHotelResponse(hotelRepository.findByHostelUid(hostelUid).orElseThrow(
                () -> new EntityNotFoundException("Not found hotel uid " + hostelUid)
        ));
    }
}
