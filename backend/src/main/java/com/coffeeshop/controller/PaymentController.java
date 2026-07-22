package com.coffeeshop.controller;

import com.coffeeshop.dto.PaymentRequestDTO;
import com.coffeeshop.model.Payment;
import com.coffeeshop.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
@Tag(name = "Financial & Payments", description = "Endpoints for Payment Processing (Cash, Card, QR Code)")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    @Operation(summary = "Get all payment transactions")
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @PostMapping
    @Operation(summary = "Process order payment and generate transaction record")
    public Payment processPayment(@RequestBody PaymentRequestDTO dto) {
        return paymentService.processPayment(dto);
    }
}
