package com.coffeeshop.controller;

import com.coffeeshop.model.Order;
import com.coffeeshop.service.OrderService;
import com.coffeeshop.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
@Tag(name = "Admin Reports & Analytics", description = "Financial Dashboards, Daily Revenue, and Sales Reports")
public class ReportController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get daily summary metrics for Admin Dashboard")
    public Map<String, Object> getDashboardMetrics() {
        List<Order> orders = orderService.getAllOrders();
        
        int totalOrders = orders.size();
        BigDecimal totalRevenue = orders.stream()
                .filter(o -> o.getStatus() == Order.Status.COMPLETED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingOrders = orders.stream().filter(o -> o.getStatus() == Order.Status.PENDING).count();
        long brewingOrders = orders.stream().filter(o -> o.getStatus() == Order.Status.BREWING).count();
        long completedOrders = orders.stream().filter(o -> o.getStatus() == Order.Status.COMPLETED).count();

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalOrders", totalOrders);
        metrics.put("totalRevenue", totalRevenue);
        metrics.put("pendingCount", pendingOrders);
        metrics.put("brewingCount", brewingOrders);
        metrics.put("completedCount", completedOrders);

        return metrics;
    }
}
