package com.coffeeshop.dto;

import com.coffeeshop.model.Order;

public class OrderStatusUpdateDTO {

    private Order.Status status;

    public OrderStatusUpdateDTO() {}

    public Order.Status getStatus() { return status; }
    public void setStatus(Order.Status status) { this.status = status; }
}
