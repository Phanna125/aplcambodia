package com.coffeeshop.controller;

import com.coffeeshop.model.Category;
import com.coffeeshop.model.Customization;
import com.coffeeshop.model.MenuItem;
import com.coffeeshop.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.coffeeshop.security.RequireRole;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*")
@Tag(name = "Menu & Catalog Management", description = "Endpoints for Categories, Menu Items, and Drink Customizations")
public class MenuController {

    @Autowired
    private MenuService menuService;

    // --- Categories ---
    @GetMapping("/categories")
    @Operation(summary = "Get all menu categories")
    public List<Category> getCategories() {
        return menuService.getAllCategories();
    }

    @PostMapping("/categories")
    @Operation(summary = "Create a new menu category (Admin)")
    @RequireRole({"ADMIN"})
    public Category createCategory(@RequestBody Category category) {
        return menuService.createCategory(category);
    }

    // --- Menu Items ---
    @GetMapping("/items")
    @Operation(summary = "Get all active menu items")
    public List<MenuItem> getActiveMenuItems() {
        return menuService.getActiveMenuItems();
    }

    @GetMapping("/items/all")
    @Operation(summary = "Get all menu items (including inactive)")
    public List<MenuItem> getAllMenuItems() {
        return menuService.getAllMenuItems();
    }

    @PostMapping("/items")
    @Operation(summary = "Create a new menu item (Admin)")
    @RequireRole({"ADMIN"})
    public MenuItem createMenuItem(@RequestBody MenuItem item) {
        return menuService.createMenuItem(item);
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "Update menu item details or availability (Admin)")
    @RequireRole({"ADMIN"})
    public MenuItem updateMenuItem(@PathVariable Long id, @RequestBody MenuItem itemDetails) {
        return menuService.updateMenuItem(id, itemDetails);
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Delete a menu item (Admin)")
    @RequireRole({"ADMIN"})
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.ok().build();
    }

    // --- Customizations ---
    @GetMapping("/customizations")
    @Operation(summary = "Get all available customizations (Less Ice, Extra Shot, Sugar %)")
    public List<Customization> getCustomizations() {
        return menuService.getAllCustomizations();
    }

    @PostMapping("/customizations")
    @Operation(summary = "Create a customization option")
    @RequireRole({"ADMIN"})
    public Customization createCustomization(@RequestBody Customization customization) {
        return menuService.createCustomization(customization);
    }
}
