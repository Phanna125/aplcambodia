package com.coffeeshop.config;

import com.coffeeshop.model.*;
import com.coffeeshop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private MenuItemRepository menuItemRepository;
    @Autowired private CustomizationRepository customizationRepository;
    @Autowired private OrderRepository orderRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            // Users
            User admin = userRepository.save(new User(null, User.Role.ADMIN, "Shop Owner Admin", "admin@coffeeshop.com", "pass123"));
            User cashier = userRepository.save(new User(null, User.Role.CASHIER, "Dara Cashier", "cashier@coffeeshop.com", "pass123"));
            User barista = userRepository.save(new User(null, User.Role.BARISTA, "Sokha Barista", "barista@coffeeshop.com", "pass123"));
            User customer = userRepository.save(new User(null, User.Role.CUSTOMER, "Bopha Customer", "bopha@gmail.com", "pass123"));

            // Categories
            Category hot = categoryRepository.save(new Category(null, "Hot Drinks"));
            Category iced = categoryRepository.save(new Category(null, "Iced Drinks"));
            Category frappe = categoryRepository.save(new Category(null, "Frappes"));
            Category pastry = categoryRepository.save(new Category(null, "Pastries & Bakery"));

            // Menu Items
            menuItemRepository.save(new MenuItem(null, "Hot Espresso", new BigDecimal("2.00"), hot, true));
            menuItemRepository.save(new MenuItem(null, "Hot Americano", new BigDecimal("2.50"), hot, true));
            menuItemRepository.save(new MenuItem(null, "Hot Latte", new BigDecimal("3.00"), hot, true));

            menuItemRepository.save(new MenuItem(null, "Iced Latte", new BigDecimal("3.50"), iced, true));
            menuItemRepository.save(new MenuItem(null, "Iced Americano", new BigDecimal("3.00"), iced, true));
            menuItemRepository.save(new MenuItem(null, "Iced Matcha Latte", new BigDecimal("4.00"), iced, true));

            menuItemRepository.save(new MenuItem(null, "Caramel Frappe", new BigDecimal("4.50"), frappe, true));
            menuItemRepository.save(new MenuItem(null, "Chocolate Frappe", new BigDecimal("4.75"), frappe, true));

            menuItemRepository.save(new MenuItem(null, "Butter Croissant", new BigDecimal("2.25"), pastry, true));
            menuItemRepository.save(new MenuItem(null, "Chocolate Muffin", new BigDecimal("2.50"), pastry, true));

            // Customizations
            customizationRepository.save(new Customization(null, "Extra Espresso Shot", new BigDecimal("0.75")));
            customizationRepository.save(new Customization(null, "Oat Milk", new BigDecimal("0.50")));
            customizationRepository.save(new Customization(null, "Less Ice (50%)", new BigDecimal("0.00")));
            customizationRepository.save(new Customization(null, "Less Sugar (50%)", new BigDecimal("0.00")));

            System.out.println("✅ Initial Coffee Shop database records seeded successfully.");
        }
    }
}
