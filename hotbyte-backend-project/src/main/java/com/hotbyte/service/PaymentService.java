package com.hotbyte.service;

import com.hotbyte.dto.request.PaymentVerifyRequest;
import java.util.Map;

public interface PaymentService {
    Map<String, Object> createOrder(Long amountInPaise);
    void verifyPayment(PaymentVerifyRequest request);
}