package com.coffeeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoffeeOrderingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoffeeOrderingApplication.class, args);
        System.out.println("\n========================================================");
        System.out.println(" ☕ COFFEE ORDERING SYSTEM API RUNNING");
        System.out.println(" 🌐 Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println(" 🗄️ H2 Console: http://localhost:8080/h2-console");
        System.out.println("========================================================\n");
    }
}
