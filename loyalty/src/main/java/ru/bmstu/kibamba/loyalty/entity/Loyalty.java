package ru.bmstu.kibamba.loyalty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "loyalty")
@Getter
@Setter
public class Loyalty {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false, length = 80, unique = true)
    private String username;

    @Column(name = "reservation_count",nullable = false, columnDefinition = "integer default 0")
    private Integer reservationCount;

    @Column(nullable = false, columnDefinition = "VARCHAR(20) CHECK (status IN ('BRONZE', 'SILVER', 'GOLD'))")
    private String status;

    @Column(nullable = false)
    private Integer discount;
}
