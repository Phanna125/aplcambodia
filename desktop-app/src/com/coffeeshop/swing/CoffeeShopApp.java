package com.coffeeshop.swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CoffeeShopApp extends JFrame {

    // Theme Colors
    private static final Color COLOR_PRIMARY = new Color(111, 78, 55);    // Coffee Brown
    private static final Color COLOR_SECONDARY = new Color(195, 155, 119); // Latte Tan
    private static final Color COLOR_BG = new Color(248, 245, 240);       // Warm Cream
    private static final Color COLOR_ACCENT = new Color(40, 167, 69);      // Success Green
    private static final Color COLOR_TEXT = new Color(40, 30, 20);

    // Mock Data Models
    static class Category {
        int id; String name;
        Category(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }

    static class MenuItem {
        int id; String name; double price; Category category; boolean active;
        MenuItem(int id, String name, double price, Category category, boolean active) {
            this.id = id; this.name = name; this.price = price; this.category = category; this.active = active;
        }
        @Override public String toString() { return name + " ($" + String.format("%.2f", price) + ")"; }
    }

    static class CartItem {
        MenuItem menuItem;
        int quantity;
        List<String> customizations;
        double itemTotal;

        CartItem(MenuItem menuItem, int quantity, List<String> customizations, double itemTotal) {
            this.menuItem = menuItem;
            this.quantity = quantity;
            this.customizations = customizations;
            this.itemTotal = itemTotal;
        }
    }

    static class Order {
        int orderId;
        String customerName;
        String status; // PENDING, BREWING, COMPLETED
        double totalAmount;
        List<CartItem> items;

        Order(int orderId, String customerName, String status, double totalAmount, List<CartItem> items) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.status = status;
            this.totalAmount = totalAmount;
            this.items = items;
        }
    }

    // In-memory lists
    private List<Category> categories = new ArrayList<>();
    private List<MenuItem> menuItems = new ArrayList<>();
    private List<CartItem> currentCart = new ArrayList<>();
    private List<Order> orderQueue = new ArrayList<>();

    private int nextOrderId = 101;

    // UI Components
    private JTabbedPane mainTabbedPane;
    private DefaultTableModel cartTableModel;
    private JLabel cartTotalLabel;
    private JPanel drinkGridPanel;
    private DefaultTableModel queueTableModel;
    private DefaultTableModel adminMenuTableModel;
    private JLabel revenueMetricLabel;
    private JLabel totalOrdersMetricLabel;

    public CoffeeShopApp() {
        setTitle("☕ Coffee Ordering System - Royal University of Phnom Penh (APL)");
        setSize(1100, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initSampleData();

        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("☕ COFFEE ORDERING SYSTEM");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Role-Based Management & POS System (Assignment 1 & 2)");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(COLOR_SECONDARY);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(subtitleLabel, BorderLayout.EAST);

        // Main Tabbed Interface
        mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainTabbedPane.setBackground(COLOR_BG);

        mainTabbedPane.addTab("🛒 Cashier POS", createCashierPOSPanel());
        mainTabbedPane.addTab("☕ Barista Queue", createBaristaQueuePanel());
        mainTabbedPane.addTab("⚙️ Admin Dashboard & Menu", createAdminPanel());

        add(headerPanel, BorderLayout.NORTH);
        add(mainTabbedPane, BorderLayout.CENTER);
    }

    private void initSampleData() {
        Category hot = new Category(1, "Hot Drinks");
        Category iced = new Category(2, "Iced Drinks");
        Category frappe = new Category(3, "Frappes");
        Category pastry = new Category(4, "Pastries");

        categories.add(hot); categories.add(iced); categories.add(frappe); categories.add(pastry);

        menuItems.add(new MenuItem(1, "Hot Espresso", 2.00, hot, true));
        menuItems.add(new MenuItem(2, "Hot Americano", 2.50, hot, true));
        menuItems.add(new MenuItem(3, "Hot Cappuccino", 3.00, hot, true));
        menuItems.add(new MenuItem(4, "Iced Latte", 3.50, iced, true));
        menuItems.add(new MenuItem(5, "Iced Matcha Latte", 4.00, iced, true));
        menuItems.add(new MenuItem(6, "Caramel Frappe", 4.50, frappe, true));
        menuItems.add(new MenuItem(7, "Butter Croissant", 2.25, pastry, true));

        // Sample Orders
        orderQueue.add(new Order(nextOrderId++, "Walk-in Customer", "PENDING", 4.25, new ArrayList<>()));
        orderQueue.add(new Order(nextOrderId++, "Bopha (Remote)", "BREWING", 7.50, new ArrayList<>()));
    }

    // --- 1. CASHIER POS PANEL ---
    private JPanel createCashierPOSPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Left Side: Drink Selection
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);

        // Category Filter Toolbar
        JPanel categoryBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        categoryBar.setOpaque(false);
        categoryBar.add(new JLabel("Category:"));

        JComboBox<String> categoryCombo = new JComboBox<>();
        categoryCombo.addItem("All Items");
        for (Category c : categories) categoryCombo.addItem(c.name);
        categoryCombo.addActionListener(e -> refreshDrinkGrid((String) categoryCombo.getSelectedItem()));
        categoryBar.add(categoryCombo);

        leftPanel.add(categoryBar, BorderLayout.NORTH);

        // Drink Buttons Grid
        drinkGridPanel = new JPanel(new GridLayout(0, 3, 12, 12));
        drinkGridPanel.setOpaque(false);
        refreshDrinkGrid("All Items");

        JScrollPane drinkScrollPane = new JScrollPane(drinkGridPanel);
        drinkScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_SECONDARY), "Menu Catalog"));
        leftPanel.add(drinkScrollPane, BorderLayout.CENTER);

        // Right Side: Shopping Cart & Checkout
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setPreferredSize(new Dimension(380, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRIMARY), "Current Cart"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        String[] columns = {"Item", "Qty", "Price", "Subtotal"};
        cartTableModel = new DefaultTableModel(columns, 0);
        JTable cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(24);
        rightPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // Cart Actions & Checkout Summary
        JPanel checkoutBottomPanel = new JPanel(new GridLayout(4, 1, 8, 8));
        checkoutBottomPanel.setOpaque(false);

        cartTotalLabel = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        cartTotalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        cartTotalLabel.setForeground(COLOR_PRIMARY);

        JButton clearBtn = new JButton("🗑️ Clear Cart");
        clearBtn.addActionListener(e -> clearCart());

        JButton checkoutCashBtn = new JButton("💵 Checkout (Cash)");
        checkoutCashBtn.setBackground(COLOR_PRIMARY);
        checkoutCashBtn.setForeground(Color.WHITE);
        checkoutCashBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        checkoutCashBtn.addActionListener(e -> processCheckout("CASH"));

        JButton checkoutQRBtn = new JButton("📱 Checkout (KHQR Code)");
        checkoutQRBtn.setBackground(COLOR_ACCENT);
        checkoutQRBtn.setForeground(Color.WHITE);
        checkoutQRBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        checkoutQRBtn.addActionListener(e -> processCheckout("QR_CODE"));

        checkoutBottomPanel.add(cartTotalLabel);
        checkoutBottomPanel.add(clearBtn);
        checkoutBottomPanel.add(checkoutCashBtn);
        checkoutBottomPanel.add(checkoutQRBtn);

        rightPanel.add(checkoutBottomPanel, BorderLayout.SOUTH);

        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private void refreshDrinkGrid(String categoryFilter) {
        drinkGridPanel.removeAll();

        for (MenuItem item : menuItems) {
            if (!item.active) continue;
            if (!"All Items".equals(categoryFilter) && !item.category.name.equals(categoryFilter)) continue;

            JButton btn = new JButton("<html><center><b>" + item.name + "</b><br/><font color='#6F4E37'>$" + String.format("%.2f", item.price) + "</font></center></html>");
            btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
            btn.setBackground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> openCustomizationDialog(item));
            drinkGridPanel.add(btn);
        }
        drinkGridPanel.revalidate();
        drinkGridPanel.repaint();
    }

    private void openCustomizationDialog(MenuItem item) {
        JDialog dialog = new JDialog(this, "Customize " + item.name, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(340, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JCheckBox extraShot = new JCheckBox("Extra Espresso Shot (+$0.75)");
        JCheckBox oatMilk = new JCheckBox("Oat Milk Substitution (+$0.50)");
        JCheckBox lessIce = new JCheckBox("Less Ice (50%)");
        JCheckBox lessSugar = new JCheckBox("Less Sugar (50%)");

        panel.add(new JLabel("Select Customizations:"));
        panel.add(extraShot);
        panel.add(oatMilk);
        panel.add(lessIce);
        panel.add(lessSugar);

        JButton addBtn = new JButton("Add to Cart");
        addBtn.setBackground(COLOR_PRIMARY);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        addBtn.addActionListener(e -> {
            List<String> customs = new ArrayList<>();
            double itemTotal = item.price;

            if (extraShot.isSelected()) { customs.add("Extra Shot"); itemTotal += 0.75; }
            if (oatMilk.isSelected()) { customs.add("Oat Milk"); itemTotal += 0.50; }
            if (lessIce.isSelected()) { customs.add("Less Ice"); }
            if (lessSugar.isSelected()) { customs.add("Less Sugar"); }

            currentCart.add(new CartItem(item, 1, customs, itemTotal));
            updateCartTable();
            dialog.dispose();
        });

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(addBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void updateCartTable() {
        cartTableModel.setRowCount(0);
        double grandTotal = 0.0;

        for (CartItem ci : currentCart) {
            String details = ci.menuItem.name + (ci.customizations.isEmpty() ? "" : " (" + String.join(", ", ci.customizations) + ")");
            cartTableModel.addRow(new Object[]{details, ci.quantity, "$" + String.format("%.2f", ci.menuItem.price), "$" + String.format("%.2f", ci.itemTotal)});
            grandTotal += ci.itemTotal;
        }

        cartTotalLabel.setText("Total: $" + String.format("%.2f", grandTotal));
    }

    private void clearCart() {
        currentCart.clear();
        updateCartTable();
    }

    private void processCheckout(String paymentMethod) {
        if (currentCart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty! Select drinks first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = currentCart.stream().mapToDouble(c -> c.itemTotal).sum();
        Order newOrder = new Order(nextOrderId++, "Walk-in Customer", "PENDING", total, new ArrayList<>(currentCart));
        orderQueue.add(newOrder);

        refreshQueueTable();
        updateAdminMetrics();

        JOptionPane.showMessageDialog(this,
                "✅ Order #" + newOrder.orderId + " Created Successfully!\n" +
                "Payment Method: " + paymentMethod + "\n" +
                "Total Paid: $" + String.format("%.2f", total) + "\n" +
                "Order status set to PENDING for Barista.",
                "Order Receipt", JOptionPane.INFORMATION_MESSAGE);

        clearCart();
    }

    // --- 2. BARISTA QUEUE PANEL ---
    private JPanel createBaristaQueuePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("☕ Real-time Barista Order Queue");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"Order ID", "Customer", "Total", "Status", "Action"};
        queueTableModel = new DefaultTableModel(columns, 0);
        JTable queueTable = new JTable(queueTableModel);
        queueTable.setRowHeight(30);
        refreshQueueTable();

        panel.add(new JScrollPane(queueTable), BorderLayout.CENTER);

        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        controlBar.setOpaque(false);

        JButton brewBtn = new JButton("▶️ Mark BREWING");
        brewBtn.addActionListener(e -> changeSelectedOrderStatus(queueTable, "BREWING"));

        JButton completeBtn = new JButton("✅ Mark COMPLETED");
        completeBtn.setBackground(COLOR_ACCENT);
        completeBtn.setForeground(Color.WHITE);
        completeBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        completeBtn.addActionListener(e -> changeSelectedOrderStatus(queueTable, "COMPLETED"));

        controlBar.add(brewBtn);
        controlBar.add(completeBtn);
        panel.add(controlBar, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshQueueTable() {
        if (queueTableModel == null) return;
        queueTableModel.setRowCount(0);

        for (Order o : orderQueue) {
            queueTableModel.addRow(new Object[]{
                    "#" + o.orderId,
                    o.customerName,
                    "$" + String.format("%.2f", o.totalAmount),
                    o.status,
                    o.status.equals("PENDING") ? "Brew Drink" : o.status.equals("BREWING") ? "Complete Order" : "Done"
            });
        }
    }

    private void changeSelectedOrderStatus(JTable queueTable, String newStatus) {
        int selectedRow = queueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order from the queue table.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Order order = orderQueue.get(selectedRow);
        order.status = newStatus;
        refreshQueueTable();
        updateAdminMetrics();
    }

    // --- 3. ADMIN DASHBOARD & MENU PANEL ---
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Metrics Cards
        JPanel metricsPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        metricsPanel.setOpaque(false);

        JPanel revCard = new JPanel(new BorderLayout());
        revCard.setBackground(COLOR_PRIMARY);
        revCard.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel revTitle = new JLabel("Total Completed Revenue");
        revTitle.setForeground(COLOR_SECONDARY);
        revenueMetricLabel = new JLabel("$0.00");
        revenueMetricLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        revenueMetricLabel.setForeground(Color.WHITE);
        revCard.add(revTitle, BorderLayout.NORTH);
        revCard.add(revenueMetricLabel, BorderLayout.CENTER);

        JPanel orderCard = new JPanel(new BorderLayout());
        orderCard.setBackground(COLOR_SECONDARY);
        orderCard.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel orderTitle = new JLabel("Total Orders Managed");
        orderTitle.setForeground(COLOR_PRIMARY);
        totalOrdersMetricLabel = new JLabel("0");
        totalOrdersMetricLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        totalOrdersMetricLabel.setForeground(Color.WHITE);
        orderCard.add(orderTitle, BorderLayout.NORTH);
        orderCard.add(totalOrdersMetricLabel, BorderLayout.CENTER);

        metricsPanel.add(revCard);
        metricsPanel.add(orderCard);
        panel.add(metricsPanel, BorderLayout.NORTH);

        // Center: Menu Item Manager Table
        String[] columns = {"ID", "Item Name", "Base Price", "Category", "Status"};
        adminMenuTableModel = new DefaultTableModel(columns, 0);
        JTable menuTable = new JTable(adminMenuTableModel);
        menuTable.setRowHeight(26);
        refreshAdminMenuTable();

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRIMARY), "Menu Items & Base Prices"));
        tablePanel.add(new JScrollPane(menuTable), BorderLayout.CENTER);

        // Add / Edit Controls
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setOpaque(false);

        JButton addDrinkBtn = new JButton("➕ Add New Drink");
        addDrinkBtn.addActionListener(e -> openAddDrinkDialog());

        JButton toggleStatusBtn = new JButton("🔄 Toggle Stock Availability");
        toggleStatusBtn.addActionListener(e -> {
            int selectedRow = menuTable.getSelectedRow();
            if (selectedRow >= 0) {
                MenuItem item = menuItems.get(selectedRow);
                item.active = !item.active;
                refreshAdminMenuTable();
                refreshDrinkGrid("All Items");
            }
        });

        actionPanel.add(addDrinkBtn);
        actionPanel.add(toggleStatusBtn);
        tablePanel.add(actionPanel, BorderLayout.SOUTH);

        panel.add(tablePanel, BorderLayout.CENTER);

        updateAdminMetrics();
        return panel;
    }

    private void refreshAdminMenuTable() {
        if (adminMenuTableModel == null) return;
        adminMenuTableModel.setRowCount(0);

        for (MenuItem item : menuItems) {
            adminMenuTableModel.addRow(new Object[]{
                    item.id,
                    item.name,
                    "$" + String.format("%.2f", item.price),
                    item.category.name,
                    item.active ? "In Stock" : "Out of Stock"
            });
        }
    }

    private void openAddDrinkDialog() {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JComboBox<Category> categoryBox = new JComboBox<>(categories.toArray(new Category[0]));

        Object[] message = {
                "Drink Name:", nameField,
                "Base Price ($):", priceField,
                "Category:", categoryBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Menu Item", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                Category cat = (Category) categoryBox.getSelectedItem();

                MenuItem newItem = new MenuItem(menuItems.size() + 1, name, price, cat, true);
                menuItems.add(newItem);
                refreshAdminMenuTable();
                refreshDrinkGrid("All Items");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid inputs. Price must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateAdminMetrics() {
        if (revenueMetricLabel == null || totalOrdersMetricLabel == null) return;

        double totalRev = orderQueue.stream()
                .filter(o -> o.status.equals("COMPLETED"))
                .mapToDouble(o -> o.totalAmount)
                .sum();

        revenueMetricLabel.setText("$" + String.format("%.2f", totalRev));
        totalOrdersMetricLabel.setText(String.valueOf(orderQueue.size()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new CoffeeShopApp().setVisible(true);
        });
    }
}
