package ru.bmstu.kibamba.loyalty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.kibamba.loyalty.payload.LoyaltyPut;
import ru.bmstu.kibamba.loyalty.payload.LoyaltyResponse;
import ru.bmstu.kibamba.loyalty.service.LoyaltyServiceImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/loyalty")
public class LoyaltyController {
    private final LoyaltyServiceImpl loyaltyService;

    @Autowired
    public LoyaltyController(LoyaltyServiceImpl loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping(produces = "application/json")
    public LoyaltyResponse getLoyalty(@RequestHeader("X-User-Name") String username) {
        return loyaltyService.getLoyalty(username);
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    public LoyaltyResponse updateLoyalty(@RequestHeader("X-User-Name") String xUserName, @RequestBody @Valid LoyaltyPut loyaltyPut) {
        return loyaltyService.update(xUserName, loyaltyPut);
    }
}
