package com.hotbyte.service.impl;

import com.hotbyte.dto.request.PaymentVerifyRequest;
import com.hotbyte.exception.BadRequestException;
import com.hotbyte.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    public Map<String, Object> createOrder(Long amountInPaise) {
        try {
            RazorpayClient client = new RazorpayClient(
                    razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise); // already in paise from frontend
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "hotbyte_" + System.currentTimeMillis());
            orderRequest.put("payment_capture", 1);

            Order order = client.orders.create(orderRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("id", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));

            return response;

        } catch (RazorpayException e) {
            throw new BadRequestException(
                    "Failed to create payment order: " + e.getMessage());
        }
    }

    @Override
    public void verifyPayment(PaymentVerifyRequest request) {
        try {
            // Construct the string to sign
            String payload = request.getRazorpayOrderId()
                    + "|" + request.getRazorpayPaymentId();

            // Generate HMAC SHA256 signature
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    razorpayKeySecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(
                    payload.getBytes(StandardCharsets.UTF_8));

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            String generatedSignature = hexString.toString();

            // Compare with received signature
            if (!generatedSignature.equals(
                    request.getRazorpaySignature())) {
                throw new BadRequestException(
                        "Payment verification failed: Invalid signature");
            }

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException(
                    "Payment verification error: " + e.getMessage());
        }
    }
}