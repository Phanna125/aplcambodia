package com.coffeeshop.config;

import com.coffeeshop.model.*;
import com.coffeeshop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.math.BigDecimal;

// @Component // Disabled so we can rely on data.sql instead of auto-seeding
public class DataLoader implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            // Users
            userRepository.save(new User(null, User.Role.ADMIN, "Shop Owner Admin", "admin@coffeeshop.com", "pass123"));
            userRepository.save(new User(null, User.Role.CASHIER, "Dara Cashier", "cashier@coffeeshop.com", "pass123"));
            userRepository.save(new User(null, User.Role.BARISTA, "Sokha Barista", "barista@coffeeshop.com", "pass123"));
            userRepository.save(new User(null, User.Role.CUSTOMER, "Bopha Customer", "bopha@gmail.com", "pass123"));

            // Categories
            categoryRepository.save(new Category(null, "Hot Drinks"));
            categoryRepository.save(new Category(null, "Iced Drinks"));
            categoryRepository.save(new Category(null, "Frappes"));
            categoryRepository.save(new Category(null, "Pastries & Bakery"));
            
            System.out.println("✅ Initial Coffee Shop database records seeded successfully.");
        }
    }
}
