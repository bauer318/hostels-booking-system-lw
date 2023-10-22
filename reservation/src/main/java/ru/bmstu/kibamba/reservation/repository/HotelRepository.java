package ru.bmstu.kibamba.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.kibamba.reservation.entity.Hotel;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HotelRepository extends PagingAndSortingRepository<Hotel, Integer> {
    Optional<Hotel> findByHostelUid(UUID hotel);
    boolean existsByHostelUid(UUID hostelUid);
}
