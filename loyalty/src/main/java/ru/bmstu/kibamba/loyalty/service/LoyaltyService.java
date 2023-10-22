package ru.bmstu.kibamba.loyalty.service;

import ru.bmstu.kibamba.loyalty.payload.LoyaltyPut;
import ru.bmstu.kibamba.loyalty.payload.LoyaltyResponse;

public interface LoyaltyService {
    LoyaltyResponse update(String username,LoyaltyPut loyaltyPut);

    LoyaltyResponse getLoyalty(String username);
}
