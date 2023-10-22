package ru.bmstu.kibamba.loyalty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.kibamba.loyalty.entity.Loyalty;

import java.util.Optional;

@Repository
public interface LoyaltyRepository extends JpaRepository<Loyalty, Integer> {
    Optional<Loyalty> findByUsername(String username);
}
