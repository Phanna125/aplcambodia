package com.coffeeshop.controller;

import com.coffeeshop.dto.OrderRequestDTO;
import com.coffeeshop.dto.OrderStatusUpdateDTO;
import com.coffeeshop.model.Order;
import com.coffeeshop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@Tag(name = "Order Management & Queue", description = "Endpoints for Order Creation, Order Queue, and Status State Machine")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "Get all orders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/queue")
    @Operation(summary = "Get real-time order queue for Barista (PENDING & BREWING orders)")
    public List<Order> getOrderQueue() {
        List<Order> queue = orderService.getOrdersByStatus(Order.Status.PENDING);
        queue.addAll(orderService.getOrdersByStatus(Order.Status.BREWING));
        return queue;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID (Live order status tracking)")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    @Operation(summary = "Create a new order (Cashier / Customer)")
    public Order createOrder(@RequestBody OrderRequestDTO dto) {
        return orderService.createOrder(dto);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status (PENDING -> BREWING -> COMPLETED)")
    public Order updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatusUpdateDTO dto) {
        return orderService.updateOrderStatus(id, dto.getStatus());
    }
}
