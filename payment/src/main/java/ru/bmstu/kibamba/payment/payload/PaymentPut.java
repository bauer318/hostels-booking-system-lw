package ru.bmstu.kibamba.payment.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentPut {
    private String status;
}
