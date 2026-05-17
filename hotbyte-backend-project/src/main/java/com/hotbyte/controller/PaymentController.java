package com.hotbyte.controller;

import com.hotbyte.dto.request.PaymentVerifyRequest;
import com.hotbyte.dto.response.ApiResponse;
import com.hotbyte.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Razorpay payment APIs")
public class PaymentController {

    private final PaymentService paymentService;

    // Step 1: Create Razorpay order
    @PostMapping("/create-order")
    @Operation(summary = "Create a Razorpay order")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrder(
            @RequestBody Map<String, Object> body) {

        Long amount = Long.valueOf(body.get("amount").toString());
        Map<String, Object> order = paymentService.createOrder(amount);

        return ResponseEntity.ok(ApiResponse.success("Order created", order));
    }

    // Step 2: Verify payment signature
    @PostMapping("/verify")
    @Operation(summary = "Verify Razorpay payment signature")
    public ResponseEntity<ApiResponse<String>> verifyPayment(
            @RequestBody PaymentVerifyRequest request) {

        paymentService.verifyPayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment verified", "OK"));
    }
}