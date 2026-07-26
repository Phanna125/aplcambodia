package com.coffeeshop.controller;

import com.coffeeshop.model.Order;
import com.coffeeshop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.coffeeshop.security.RequireRole;

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

    @GetMapping("/dashboard")
    @Operation(summary = "Get daily summary metrics for Admin Dashboard")
    @RequireRole({"ADMIN"})
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

        // Top Selling Items (from completed orders)
        Map<String, Integer> itemCounts = orders.stream()
                .filter(o -> o.getStatus() == Order.Status.COMPLETED)
                .flatMap(o -> o.getOrderItems().stream())
                .collect(java.util.stream.Collectors.groupingBy(
                        oi -> oi.getMenuItem().getName(),
                        java.util.stream.Collectors.summingInt(oi -> oi.getQuantity())
                ));

        List<Map<String, Object>> topItems = itemCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", e.getKey());
                    map.put("quantity", e.getValue());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
        metrics.put("topItems", topItems);

        // Peak Business Hours (from completed orders)
        Map<Integer, Long> hourCounts = orders.stream()
                .filter(o -> o.getStatus() == Order.Status.COMPLETED && o.getCreatedAt() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        o -> o.getCreatedAt().getHour(),
                        java.util.stream.Collectors.counting()
                ));

        List<Map<String, Object>> peakHours = hourCounts.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("hour", e.getKey());
                    map.put("orders", e.getValue());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
        metrics.put("peakHours", peakHours);

        return metrics;
    }
}
