package com.coffeeshop.service;

import com.coffeeshop.dto.OrderItemRequestDTO;
import com.coffeeshop.dto.OrderRequestDTO;
import com.coffeeshop.model.*;
import com.coffeeshop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private CustomizationRepository customizationRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByStatus(Order.Status status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Transactional
    public Order createOrder(OrderRequestDTO dto) {
        Order order = new Order();
        if (dto.getGuestName() != null && !dto.getGuestName().trim().isEmpty()) {
            String guestName = "Guest: " + dto.getGuestName().trim();
            User guestUser = userRepository.findByName(guestName).orElse(null);
            if (guestUser == null) {
                guestUser = new User();
                guestUser.setName(guestName);
                String emailPrefix = dto.getGuestName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                if (emailPrefix.isEmpty()) emailPrefix = "guest";
                guestUser.setEmail(emailPrefix + "_" + System.currentTimeMillis() + "@guest.local");
                guestUser.setRole(User.Role.CUSTOMER);
                guestUser.setPasswordHash("guest");
                guestUser = userRepository.save(guestUser);
            }
            order.setCustomer(guestUser);
        } else if (dto.getCustomerId() != null) {
            User customer = userRepository.findById(dto.getCustomerId()).orElse(null);
            order.setCustomer(customer);
        }
        if (dto.getInitialStatus() != null) {
            try {
                order.setStatus(Order.Status.valueOf(dto.getInitialStatus()));
            } catch (IllegalArgumentException e) {
                order.setStatus(Order.Status.PENDING);
            }
        } else {
            order.setStatus(Order.Status.PENDING);
        }

        BigDecimal calculatedTotal = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemDto : dto.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found: " + itemDto.getMenuItemId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemDto.getQuantity());

            BigDecimal itemTotal = menuItem.getBasePrice();

            Set<Customization> customizationSet = new HashSet<>();
            if (itemDto.getCustomizationIds() != null) {
                for (Long customId : itemDto.getCustomizationIds()) {
                    Customization custom = customizationRepository.findById(customId)
                            .orElseThrow(() -> new RuntimeException("Customization not found: " + customId));
                    customizationSet.add(custom);
                    itemTotal = itemTotal.add(custom.getPriceImpact());
                }
            }
            orderItem.setCustomizations(customizationSet);

            calculatedTotal = calculatedTotal.add(itemTotal.multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            order.getOrderItems().add(orderItem);
        }

        order.setTotalAmount(calculatedTotal);
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long id, Order.Status newStatus) {
        Order order = getOrderById(id);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}
