package com.coffeeshop.dto;

import java.util.ArrayList;
import java.util.List;

public class OrderRequestDTO {

    private Long customerId;
    private List<OrderItemRequestDTO> items = new ArrayList<>();

    public OrderRequestDTO() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public List<OrderItemRequestDTO> getItems() { return items; }
    public void setItems(List<OrderItemRequestDTO> items) { this.items = items; }
}
