package com.coffeeshop.service;

import com.coffeeshop.model.Category;
import com.coffeeshop.model.Customization;
import com.coffeeshop.model.MenuItem;
import com.coffeeshop.repository.CategoryRepository;
import com.coffeeshop.repository.CustomizationRepository;
import com.coffeeshop.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private CustomizationRepository customizationRepository;

    // Categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Menu Items
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public List<MenuItem> getActiveMenuItems() {
        return menuItemRepository.findByActiveTrue();
    }

    public MenuItem createMenuItem(MenuItem item) {
        return menuItemRepository.save(item);
    }

    public MenuItem updateMenuItem(Long id, MenuItem itemDetails) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));
        item.setName(itemDetails.getName());
        item.setBasePrice(itemDetails.getBasePrice());
        item.setCategory(itemDetails.getCategory());
        item.setActive(itemDetails.isActive());
        return menuItemRepository.save(item);
    }

    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    // Customizations
    public List<Customization> getAllCustomizations() {
        return customizationRepository.findAll();
    }

    public Customization createCustomization(Customization customization) {
        return customizationRepository.save(customization);
    }
}
