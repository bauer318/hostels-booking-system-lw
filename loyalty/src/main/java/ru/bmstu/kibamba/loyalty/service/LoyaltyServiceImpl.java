package ru.bmstu.kibamba.loyalty.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bmstu.kibamba.loyalty.entity.Loyalty;
import ru.bmstu.kibamba.loyalty.payload.LoyaltyPut;
import ru.bmstu.kibamba.loyalty.payload.LoyaltyResponse;
import ru.bmstu.kibamba.loyalty.repository.LoyaltyRepository;

@Service
public class LoyaltyServiceImpl implements LoyaltyService {
    private final LoyaltyRepository loyaltyRepository;

    @Autowired
    public LoyaltyServiceImpl(LoyaltyRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
    }

    private Loyalty getOne(String username) {
        return loyaltyRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Not found loyalty by username " + username)
        );
    }

    private LoyaltyResponse buildLoyaltyResponse(Loyalty loyalty) {
        return LoyaltyResponse.builder()
                .reservationCount(loyalty.getReservationCount())
                .discount(loyalty.getDiscount())
                .id(loyalty.getId())
                .status(loyalty.getStatus())
                .username(loyalty.getUsername())
                .build();
    }

    @Override
    public LoyaltyResponse update(String username, LoyaltyPut loyaltyPut) {
        Loyalty loyalty = getOne(username);
        if (loyaltyPut.getReservationCount() < 0) {
            loyalty.setReservationCount(0);
        }
        if (loyaltyPut.getReservationCount() < 10) {
            loyalty.setDiscount(5);
            loyalty.setStatus("BRONZE");
        } else if (loyaltyPut.getReservationCount() < 20) {
            loyalty.setDiscount(7);
            loyalty.setStatus("SILVER");
        } else {
            loyalty.setDiscount(10);
            loyalty.setStatus("GOLD");
        }
        return buildLoyaltyResponse(loyaltyRepository.save(loyalty));
    }

    @Override
    public LoyaltyResponse getLoyalty(String username) {
        return buildLoyaltyResponse(getOne(username));
    }
}
