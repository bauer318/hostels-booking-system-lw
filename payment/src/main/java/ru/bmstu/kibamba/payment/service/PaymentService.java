package ru.bmstu.kibamba.payment.service;

import ru.bmstu.kibamba.payment.payload.PaymentPut;
import ru.bmstu.kibamba.payment.payload.PaymentRequest;
import ru.bmstu.kibamba.payment.payload.PaymentResponse;

import java.util.UUID;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest paymentRequest);

    PaymentResponse updatePayment(UUID paymentUid, PaymentPut paymentPut);

    PaymentResponse getPayment(UUID paymentUid);
}
