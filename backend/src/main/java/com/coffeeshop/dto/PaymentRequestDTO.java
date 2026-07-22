package com.coffeeshop.dto;

import com.coffeeshop.model.Payment;

public class PaymentRequestDTO {

    private Long orderId;
    private Payment.Method method;

    public PaymentRequestDTO() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Payment.Method getMethod() { return method; }
    public void setMethod(Payment.Method method) { this.method = method; }
}
