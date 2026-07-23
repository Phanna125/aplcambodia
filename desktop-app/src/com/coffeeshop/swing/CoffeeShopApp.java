package com.coffeeshop.swing;

import com.google.gson.reflect.TypeToken;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CoffeeShopApp extends JFrame {
    
    // Auth State
    private static String currentUserRole = null;
    private static String currentUserName = null;

    // Theme Colors
    private static final Color COLOR_PRIMARY = new Color(111, 78, 55);
    private static final Color COLOR_SECONDARY = new Color(195, 155, 119);
    private static final Color COLOR_BG = new Color(248, 245, 240);
    private static final Color COLOR_ACCENT = new Color(40, 167, 69);
    private static final Color COLOR_DANGER = new Color(220, 53, 69);

    // API Data Models
    static class Category {
        Long id; String name;
        @Override public String toString() { return name; }
    }

    static class MenuItem {
        Long id; String name; double basePrice; Category category; boolean active;
        @Override public String toString() { return name + " ($" + String.format("%.2f", basePrice) + ")"; }
    }

    static class Customization {
        Long id; String name; double priceImpact;
    }

    static class User {
        Long id; String name;
    }

    static class Order {
        Long id; User customer; String status; double totalAmount;
    }

    static class OrderItemRequestDTO {
        Long menuItemId; int quantity = 1; List<Long> customizationIds = new ArrayList<>();
    }

    static class OrderRequestDTO {
        Long customerId; List<OrderItemRequestDTO> items = new ArrayList<>();
    }
    
    static class OrderStatusUpdateDTO {
        String status;
        OrderStatusUpdateDTO(String status) { this.status = status; }
    }

    // UI Internal Models
    static class CartItem {
        MenuItem menuItem; int quantity; List<Customization> customizations; double itemTotal;
        CartItem(MenuItem menuItem, int quantity, List<Customization> customizations, double itemTotal) {
            this.menuItem = menuItem; this.quantity = quantity; this.customizations = customizations; this.itemTotal = itemTotal;
        }
    }

    // In-memory lists (sync with API)
    private List<Category> categories = new ArrayList<>();
    private List<MenuItem> menuItems = new ArrayList<>();
    private List<Customization> allCustomizations = new ArrayList<>();
    private List<CartItem> currentCart = new ArrayList<>();
    private List<Order> orderQueue = new ArrayList<>();

    // UI Components
    private JTabbedPane mainTabbedPane;
    private DefaultTableModel cartTableModel;
    private JLabel cartTotalLabel;
    private JPanel drinkGridPanel;
    private JComboBox<String> categoryCombo;
    private DefaultTableModel queueTableModel;
    private DefaultTableModel adminMenuTableModel;
    private JLabel revenueMetricLabel;
    private JLabel totalOrdersMetricLabel;

    public CoffeeShopApp() {
        setTitle("☕ Coffee Ordering System - Royal University of Phnom Penh (APL)");
        setSize(1100, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load initial data from API
        loadDataFromApi();

        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("☕ COFFEE ORDERING SYSTEM - Logged in as: " + currentUserName + " (" + currentUserRole + ")");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainTabbedPane.setBackground(COLOR_BG);

        add(headerPanel, BorderLayout.NORTH);
        add(mainTabbedPane, BorderLayout.CENTER);
        
        // Build UI based on role
        if ("ADMIN".equals(currentUserRole)) {
            mainTabbedPane.addTab("🛒 Cashier POS", createCashierPOSPanel());
            mainTabbedPane.addTab("☕ Barista Queue", createBaristaQueuePanel());
            mainTabbedPane.addTab("⚙️ Admin Dashboard & Menu", createAdminPanel());
        } else if ("CASHIER".equals(currentUserRole)) {
            mainTabbedPane.addTab("🛒 Cashier POS", createCashierPOSPanel());
        } else if ("BARISTA".equals(currentUserRole)) {
            mainTabbedPane.addTab("☕ Barista Queue", createBaristaQueuePanel());
        } else {
            mainTabbedPane.addTab("🛒 Menu", createCashierPOSPanel());
        }
        
        // Start background poll for barista queue
        Timer timer = new Timer(5000, e -> {
            if ("ADMIN".equals(currentUserRole) || "BARISTA".equals(currentUserRole)) {
                fetchOrderQueue();
            }
            if ("ADMIN".equals(currentUserRole)) {
                updateAdminMetrics();
            }
        });
        timer.start();
    }

    private void loadDataFromApi() {
        try {
            categories = ApiClient.get("/menu/categories", new TypeToken<List<Category>>(){});
            if ("ADMIN".equals(currentUserRole)) {
                menuItems = ApiClient.get("/menu/items/all", new TypeToken<List<MenuItem>>(){});
            } else {
                menuItems = ApiClient.get("/menu/items", new TypeToken<List<MenuItem>>(){});
            }
            allCustomizations = ApiClient.get("/menu/customizations", new TypeToken<List<Customization>>(){});
            
            if ("ADMIN".equals(currentUserRole) || "BARISTA".equals(currentUserRole)) {
                fetchOrderQueue();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load data from server: " + e.getMessage(), "API Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchOrderQueue() {
        try {
            orderQueue = ApiClient.get("/orders/queue", new TypeToken<List<Order>>(){});
            refreshQueueTable();
        } catch (Exception e) {
            System.err.println("Failed to fetch queue: " + e.getMessage());
        }
    }

    // --- 1. CASHIER POS PANEL ---
    private JPanel createCashierPOSPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);

        JPanel categoryBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        categoryBar.setOpaque(false);
        categoryBar.add(new JLabel("Category:"));

        categoryCombo = new JComboBox<>();
        categoryCombo.addItem("All Items");
        for (Category c : categories) categoryCombo.addItem(c.name);
        categoryCombo.addActionListener(e -> refreshDrinkGrid((String) categoryCombo.getSelectedItem()));
        categoryBar.add(categoryCombo);

        JButton refreshMenuBtn = new JButton("🔄 Refresh Menu");
        refreshMenuBtn.addActionListener(e -> { loadDataFromApi(); refreshDrinkGrid((String) categoryCombo.getSelectedItem()); });
        categoryBar.add(refreshMenuBtn);

        leftPanel.add(categoryBar, BorderLayout.NORTH);

        drinkGridPanel = new JPanel(new GridLayout(0, 3, 12, 12));
        drinkGridPanel.setOpaque(false);
        refreshDrinkGrid("All Items");

        JScrollPane drinkScrollPane = new JScrollPane(drinkGridPanel);
        drinkScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_SECONDARY), "Menu Catalog"));
        leftPanel.add(drinkScrollPane, BorderLayout.CENTER);

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
        checkoutCashBtn.addActionListener(e -> processCheckout("CASH"));

        JButton checkoutQRBtn = new JButton("📱 Checkout (KHQR Code)");
        checkoutQRBtn.setBackground(COLOR_ACCENT);
        checkoutQRBtn.setForeground(Color.WHITE);
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
            if (!"All Items".equals(categoryFilter) && item.category != null && !item.category.name.equals(categoryFilter)) continue;

            JButton btn = new JButton("<html><center><b>" + item.name + "</b><br/><font color='#6F4E37'>$" + String.format("%.2f", item.basePrice) + "</font></center></html>");
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
        dialog.setSize(340, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.add(new JLabel("Select Customizations:"));

        List<JCheckBox> checkBoxes = new ArrayList<>();
        List<Customization> boundCustoms = new ArrayList<>();

        for (Customization c : allCustomizations) {
            JCheckBox cb = new JCheckBox(c.name + (c.priceImpact > 0 ? " (+$" + c.priceImpact + ")" : ""));
            checkBoxes.add(cb);
            boundCustoms.add(c);
            panel.add(cb);
        }

        JButton addBtn = new JButton("Add to Cart");
        addBtn.setBackground(COLOR_PRIMARY);
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> {
            List<Customization> selected = new ArrayList<>();
            double itemTotal = item.basePrice;

            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    Customization c = boundCustoms.get(i);
                    selected.add(c);
                    itemTotal += c.priceImpact;
                }
            }
            currentCart.add(new CartItem(item, 1, selected, itemTotal));
            updateCartTable();
            dialog.dispose();
        });

        dialog.add(new JScrollPane(panel), BorderLayout.CENTER);
        dialog.add(addBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void updateCartTable() {
        cartTableModel.setRowCount(0);
        double grandTotal = 0.0;
        for (CartItem ci : currentCart) {
            List<String> names = ci.customizations.stream().map(c -> c.name).collect(Collectors.toList());
            String details = ci.menuItem.name + (names.isEmpty() ? "" : " (" + String.join(", ", names) + ")");
            cartTableModel.addRow(new Object[]{details, ci.quantity, "$" + String.format("%.2f", ci.menuItem.basePrice), "$" + String.format("%.2f", ci.itemTotal)});
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
            JOptionPane.showMessageDialog(this, "Cart is empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        OrderRequestDTO req = new OrderRequestDTO();
        for (CartItem ci : currentCart) {
            OrderItemRequestDTO itemReq = new OrderItemRequestDTO();
            itemReq.menuItemId = ci.menuItem.id;
            itemReq.quantity = ci.quantity;
            itemReq.customizationIds = ci.customizations.stream().map(c -> c.id).collect(Collectors.toList());
            req.items.add(itemReq);
        }

        try {
            Order newOrder = ApiClient.post("/orders", req, Order.class);
            fetchOrderQueue();
            updateAdminMetrics();
            JOptionPane.showMessageDialog(this, "✅ Order #" + newOrder.id + " Created Successfully!\nPayment: " + paymentMethod + "\nTotal: $" + newOrder.totalAmount, "Receipt", JOptionPane.INFORMATION_MESSAGE);
            clearCart();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to create order: " + ex.getMessage(), "API Error", JOptionPane.ERROR_MESSAGE);
        }
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

        JButton refreshBtn = new JButton("🔄 Refresh Queue");
        refreshBtn.addActionListener(e -> fetchOrderQueue());

        JButton brewBtn = new JButton("▶️ Mark BREWING");
        brewBtn.addActionListener(e -> changeSelectedOrderStatus(queueTable, "BREWING"));

        JButton completeBtn = new JButton("✅ Mark COMPLETED");
        completeBtn.setBackground(COLOR_ACCENT);
        completeBtn.setForeground(Color.WHITE);
        completeBtn.addActionListener(e -> changeSelectedOrderStatus(queueTable, "COMPLETED"));

        controlBar.add(refreshBtn);
        controlBar.add(brewBtn);
        controlBar.add(completeBtn);
        panel.add(controlBar, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshQueueTable() {
        if (queueTableModel == null) return;
        queueTableModel.setRowCount(0);
        for (Order o : orderQueue) {
            String cName = o.customer != null ? o.customer.name : "Walk-in";
            queueTableModel.addRow(new Object[]{ "#" + o.id, cName, "$" + String.format("%.2f", o.totalAmount), o.status,
                    o.status.equals("PENDING") ? "Brew Drink" : o.status.equals("BREWING") ? "Complete Order" : "Done"
            });
        }
    }

    private void changeSelectedOrderStatus(JTable queueTable, String newStatus) {
        int row = queueTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order first.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Order order = orderQueue.get(row);
        try {
            ApiClient.patch("/orders/" + order.id + "/status", new OrderStatusUpdateDTO(newStatus), Order.class);
            fetchOrderQueue();
            updateAdminMetrics();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage(), "API Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- 3. ADMIN DASHBOARD & MENU PANEL ---
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

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

        String[] columns = {"ID", "Item Name", "Base Price", "Category", "Status"};
        adminMenuTableModel = new DefaultTableModel(columns, 0);
        JTable menuTable = new JTable(adminMenuTableModel);
        menuTable.setRowHeight(26);
        refreshAdminMenuTable();

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRIMARY), "Menu Items & Base Prices"));
        tablePanel.add(new JScrollPane(menuTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setOpaque(false);

        JButton addDrinkBtn = new JButton("➕ Add Drink");
        addDrinkBtn.addActionListener(e -> openAddDrinkDialog());

        JButton editDrinkBtn = new JButton("✏️ Edit Drink");
        editDrinkBtn.addActionListener(e -> {
            int row = menuTable.getSelectedRow();
            if (row >= 0) openEditDrinkDialog(menuItems.get(row));
        });

        JButton manageCatBtn = new JButton("📁 Manage Categories");
        manageCatBtn.addActionListener(e -> openManageCategoriesDialog());

        JButton toggleStatusBtn = new JButton("🔄 Toggle Stock");
        toggleStatusBtn.addActionListener(e -> {
            int row = menuTable.getSelectedRow();
            if (row >= 0) {
                MenuItem item = menuItems.get(row);
                item.active = !item.active;
                try {
                    ApiClient.put("/menu/items/" + item.id, item, MenuItem.class);
                    loadDataFromApi();
                    refreshAdminMenuTable();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage(), "API Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton deleteDrinkBtn = new JButton("🗑️ Delete Drink");
        deleteDrinkBtn.setBackground(COLOR_DANGER);
        deleteDrinkBtn.setForeground(Color.WHITE);
        deleteDrinkBtn.addActionListener(e -> {
            int row = menuTable.getSelectedRow();
            if (row >= 0) {
                MenuItem item = menuItems.get(row);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + item.name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        ApiClient.delete("/menu/items/" + item.id);
                        loadDataFromApi();
                        refreshAdminMenuTable();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage(), "API Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        actionPanel.add(manageCatBtn);
        actionPanel.add(addDrinkBtn);
        actionPanel.add(editDrinkBtn);
        actionPanel.add(toggleStatusBtn);
        actionPanel.add(deleteDrinkBtn);
        tablePanel.add(actionPanel, BorderLayout.SOUTH);

        panel.add(tablePanel, BorderLayout.CENTER);

        updateAdminMetrics();
        return panel;
    }

    private void refreshAdminMenuTable() {
        if (adminMenuTableModel == null) return;
        adminMenuTableModel.setRowCount(0);
        for (MenuItem item : menuItems) {
            String catName = item.category != null ? item.category.name : "Uncategorized";
            adminMenuTableModel.addRow(new Object[]{ item.id, item.name, "$" + String.format("%.2f", item.basePrice), catName, item.active ? "In Stock" : "Out of Stock" });
        }
    }

    private void openAddDrinkDialog() {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JComboBox<Category> catBox = new JComboBox<>(categories.toArray(new Category[0]));

        Object[] message = { "Drink Name:", nameField, "Base Price ($):", priceField, "Category:", catBox };
        int option = JOptionPane.showConfirmDialog(this, message, "Add Menu Item", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                MenuItem newItem = new MenuItem();
                newItem.name = nameField.getText().trim();
                newItem.basePrice = Double.parseDouble(priceField.getText().trim());
                newItem.category = (Category) catBox.getSelectedItem();
                newItem.active = true;

                ApiClient.post("/menu/items", newItem, MenuItem.class);
                loadDataFromApi();
                refreshAdminMenuTable();
                refreshCategoryCombo();
                refreshDrinkGrid("All Items");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openEditDrinkDialog(MenuItem item) {
        JTextField nameField = new JTextField(item.name);
        JTextField priceField = new JTextField(String.valueOf(item.basePrice));
        JComboBox<Category> catBox = new JComboBox<>(categories.toArray(new Category[0]));
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).id.equals(item.category.id)) catBox.setSelectedIndex(i);
        }

        Object[] message = { "Drink Name:", nameField, "Base Price ($):", priceField, "Category:", catBox };
        int option = JOptionPane.showConfirmDialog(this, message, "Edit Menu Item", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                item.name = nameField.getText().trim();
                item.basePrice = Double.parseDouble(priceField.getText().trim());
                item.category = (Category) catBox.getSelectedItem();

                ApiClient.put("/menu/items/" + item.id, item, MenuItem.class);
                loadDataFromApi();
                refreshAdminMenuTable();
                refreshCategoryCombo();
                refreshDrinkGrid("All Items");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openManageCategoriesDialog() {
        String catName = JOptionPane.showInputDialog(this, "Enter New Category Name:");
        if (catName != null && !catName.trim().isEmpty()) {
            try {
                Category newCat = new Category();
                newCat.name = catName.trim();
                ApiClient.post("/menu/categories", newCat, Category.class);
                loadDataFromApi();
                refreshCategoryCombo();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to create category: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshCategoryCombo() {
        if (categoryCombo != null) {
            categoryCombo.removeAllItems();
            categoryCombo.addItem("All Items");
            for (Category c : categories) categoryCombo.addItem(c.name);
        }
    }

    private void updateAdminMetrics() {
        if (revenueMetricLabel == null) return;
        try {
            Map<String, Object> metrics = ApiClient.get("/reports/dashboard", new TypeToken<Map<String, Object>>(){});
            if (metrics.containsKey("totalRevenue")) {
                double rev = ((Number) metrics.get("totalRevenue")).doubleValue();
                revenueMetricLabel.setText("$" + String.format("%.2f", rev));
            }
            if (metrics.containsKey("totalOrders")) {
                int count = ((Number) metrics.get("totalOrders")).intValue();
                totalOrdersMetricLabel.setText(String.valueOf(count));
            }
        } catch (Exception ex) {
            System.err.println("Dashboard metric fetch failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            if (showLoginDialog()) {
                new CoffeeShopApp().setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }

    private static boolean showLoginDialog() {
        JDialog loginDialog = new JDialog((Frame) null, "Login - Coffee Shop", true);
        loginDialog.setSize(350, 220);
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField emailField = new JTextField("admin@coffeeshop.com");
        JPasswordField passField = new JPasswordField("admin123");

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(COLOR_PRIMARY);
        loginBtn.setForeground(Color.WHITE);
        formPanel.add(new JLabel("")); // spacer
        formPanel.add(loginBtn);

        loginDialog.add(formPanel, BorderLayout.CENTER);

        final boolean[] success = {false};

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            
            try {
                URL url = URI.create("http://localhost:8080/api/auth/login").toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                
                String jsonInput = "{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}";
                try(OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                if (conn.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) response.append(line.trim());
                    String res = response.toString();
                    
                    if (res.contains("\"token\"") && res.contains("\"role\"")) {
                        String token = res.split("\"token\":\"")[1].split("\"")[0];
                        ApiClient.setJwtToken(token); // Set in API Client
                        currentUserRole = res.split("\"role\":\"")[1].split("\"")[0];
                        currentUserName = res.split("\"name\":\"")[1].split("\"")[0];
                        success[0] = true;
                        loginDialog.dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(loginDialog, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(loginDialog, "Server error: " + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginDialog.setVisible(true);
        return success[0];
    }
}
