package com.coffeeshop.service;

import com.coffeeshop.dto.PaymentRequestDTO;
import com.coffeeshop.model.Order;
import com.coffeeshop.model.Payment;
import com.coffeeshop.repository.OrderRepository;
import com.coffeeshop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment processPayment(PaymentRequestDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + dto.getOrderId()));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(dto.getMethod());
        payment.setStatus(Payment.Status.COMPLETED);

        return paymentRepository.save(payment);
    }
}
