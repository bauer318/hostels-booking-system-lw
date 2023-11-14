package ru.bmstu.kibamba.reservation.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.kibamba.reservation.entity.Hotel;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HotelRepository extends PagingAndSortingRepository<Hotel, Integer> {
    Optional<Hotel> findByHotelUid(UUID hotel);

    boolean existsByHotelUid(UUID hostelUid);
}
