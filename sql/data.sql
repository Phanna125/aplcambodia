-- ============================================================
-- Seed Data: Coffee Ordering System (APL Assignment 1 & 2)
-- ============================================================

USE `coffee_shop`;

-- 1. Insert Initial Users
INSERT INTO `users` (`id`, `role`, `name`, `email`, `password_hash`) VALUES
(1, 'ADMIN', 'Shop Owner Admin', 'admin@coffeeshop.com', 'admin123'),
(2, 'BARISTA', 'Sokha Barista', 'barista@coffeeshop.com', 'barista123'),
(3, 'CASHIER', 'Dara Cashier', 'cashier@coffeeshop.com', 'cashier123'),
(4, 'CUSTOMER', 'Bopha Customer', 'bopha@gmail.com', 'customer123');

-- 2. Insert Categories
INSERT INTO `categories` (`id`, `name`) VALUES
(1, 'Hot Drinks'),
(2, 'Iced Drinks'),
(3, 'Frappes'),
(4, 'Pastries & Bakery');

-- 3. Insert Menu Items
INSERT INTO `menu_items` (`id`, `name`, `base_price`, `category_id`, `is_active`) VALUES
(1, 'Espresso Single', 2.00, 1, 1),
(2, 'Hot Americano', 2.50, 1, 1),
(3, 'Hot Latte', 3.00, 1, 1),
(4, 'Hot Cappuccino', 3.00, 1, 1),
(5, 'Iced Latte', 3.50, 2, 1),
(6, 'Iced Americano', 3.00, 2, 1),
(7, 'Iced Matcha Latte', 4.00, 2, 1),
(8, 'Caramel Frappe', 4.50, 3, 1),
(9, 'Chocolate Chip Frappe', 4.75, 3, 1),
(10, 'Butter Croissant', 2.25, 4, 1),
(11, 'Chocolate Muffin', 2.50, 4, 1);

-- 4. Insert Customizations
INSERT INTO `customizations` (`id`, `name`, `price_impact`) VALUES
(1, 'Extra Espresso Shot', 0.75),
(2, 'Oat Milk Substitution', 0.50),
(3, 'Less Ice (50%)', 0.00),
(4, 'No Sugar (0%)', 0.00),
(5, 'Less Sugar (50%)', 0.00),
(6, 'Extra Syrup (Caramel)', 0.50);

-- 5. Insert Sample Orders
INSERT INTO `orders` (`id`, `customer_id`, `status`, `total_amount`, `created_at`) VALUES
(1, 4, 'COMPLETED', 4.25, NOW() - INTERVAL 2 HOUR),
(2, NULL, 'BREWING', 7.50, NOW() - INTERVAL 15 MINUTE),
(3, 4, 'PENDING', 3.50, NOW() - INTERVAL 5 MINUTE);

-- 6. Insert Sample Order Items
INSERT INTO `order_items` (`id`, `order_id`, `menu_item_id`, `quantity`) VALUES
(1, 1, 5, 1), -- Iced Latte ($3.50) + Extra Shot ($0.75) = $4.25
(2, 2, 8, 1), -- Caramel Frappe ($4.50)
(3, 2, 4, 1), -- Hot Cappuccino ($3.00) = Total $7.50
(4, 3, 5, 1); -- Iced Latte ($3.50)

-- 7. Insert Item Customizations
INSERT INTO `item_customizations` (`order_item_id`, `custom_id`) VALUES
(1, 1), -- Extra Espresso Shot
(1, 5), -- Less Sugar (50%)
(4, 3); -- Less Ice (50%)

-- 8. Insert Payments
INSERT INTO `payments` (`id`, `order_id`, `method`, `status`, `paid_at`) VALUES
(1, 1, 'QR_CODE', 'COMPLETED', NOW() - INTERVAL 2 HOUR),
(2, 2, 'CASH', 'COMPLETED', NOW() - INTERVAL 15 MINUTE),
(3, 3, 'CASH', 'PENDING', NOW() - INTERVAL 5 MINUTE);
