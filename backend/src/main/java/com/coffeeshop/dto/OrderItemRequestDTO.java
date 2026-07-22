package com.coffeeshop.dto;

import java.util.ArrayList;
import java.util.List;

public class OrderItemRequestDTO {

    private Long menuItemId;
    private Integer quantity = 1;
    private List<Long> customizationIds = new ArrayList<>();

    public OrderItemRequestDTO() {}

    public Long getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public List<Long> getCustomizationIds() { return customizationIds; }
    public void setCustomizationIds(List<Long> customizationIds) { this.customizationIds = customizationIds; }
}
