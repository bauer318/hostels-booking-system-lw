package ru.bmstu.kibamba.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "payment_uid", nullable = false)
    private UUID paymentUid;

    @Column(nullable = false, columnDefinition = "CHECK (status IN ('PAID', 'CANCELED'))")
    private String status;

    @Column(nullable = false)
    private Integer price;
}