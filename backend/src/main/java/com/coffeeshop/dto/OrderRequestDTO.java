package com.coffeeshop.dto;

import java.util.ArrayList;
import java.util.List;

public class OrderRequestDTO {

    private Long customerId;
    private String initialStatus;
    private String guestName;
    private List<OrderItemRequestDTO> items = new ArrayList<>();

    public OrderRequestDTO() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getInitialStatus() { return initialStatus; }
    public void setInitialStatus(String initialStatus) { this.initialStatus = initialStatus; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public List<OrderItemRequestDTO> getItems() { return items; }
    public void setItems(List<OrderItemRequestDTO> items) { this.items = items; }
}
