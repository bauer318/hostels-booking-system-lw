package ru.bmstu.kibamba.reservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "hostels")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "hotel_uid",nullable = false, unique = true)
    private UUID hotelUid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 80)
    private String country;

    @Column(nullable = false,length = 80)
    private String city;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Integer stars;

    @Column(nullable = false)
    private Integer price;
}
