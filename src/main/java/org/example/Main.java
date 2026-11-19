
package org.example;

import java.sql.*;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);

    // Database connection
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/ecommerce_db";
        String username = "root";
        String password = "root";

        return DriverManager.getConnection(url, username, password);
    }

    // Display order details
    public static void displayOrder(ResultSet rs) throws SQLException {
        System.out.println("--------------------------------------------");
        System.out.println("Order ID      : " + rs.getInt("order_id"));
        System.out.println("Customer Name : " + rs.getString("customer_name"));
        System.out.println("Product Name  : " + rs.getString("product_name"));
        System.out.println("Quantity      : " + rs.getInt("quantity"));
        System.out.println("Unit Price    : " + rs.getDouble("unit_price"));
        System.out.println("Status        : " + rs.getString("status"));
        System.out.println("--------------------------------------------");
    }

    // (a) Insert at least three orders
    public static void insertOrders() {

        String sql = "INSERT INTO Orders VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 1; i <= 3; i++) {

                System.out.println("\nEnter details for Order " + i);

                System.out.print("Order ID: ");
                int id = sc.nextInt();
                sc.nextLine();

                System.out.print("Customer Name: ");
                String customer = sc.nextLine();

                System.out.print("Product Name: ");
                String product = sc.nextLine();

                System.out.print("Quantity: ");
                int quantity = sc.nextInt();

                System.out.print("Unit Price: ");
                double price = sc.nextDouble();
                sc.nextLine();

                System.out.print("Status: ");
                String status = sc.nextLine();

                ps.setInt(1, id);
                ps.setString(2, customer);
                ps.setString(3, product);
                ps.setInt(4, quantity);
                ps.setDouble(5, price);
                ps.setString(6, status);

                try {
                    ps.executeUpdate();
                    System.out.println("Order inserted successfully!");
                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Duplicate Order ID! This order already exists.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // (b) Search orders by status and quantity > 2
    public static void searchOrders() {

        String sql = "SELECT * FROM Orders WHERE status = ? AND quantity > 2 ORDER BY unit_price DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter order status: ");
            String status = sc.nextLine();

            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                while (rs.next()) {
                    displayOrder(rs);
                    found = true;
                }

                if (!found) {
                    System.out.println("No matching orders found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // (c) Increase quantity of an existing order
    public static void increaseQuantity() {

        String sql = "UPDATE Orders SET quantity = quantity + ? WHERE order_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter Order ID: ");
            int id = sc.nextInt();

            System.out.print("Enter additional quantity: ");
            int additional = sc.nextInt();
            sc.nextLine();

            if (additional <= 0) {
                System.out.println("Additional quantity must be positive.");
                return;
            }

            ps.setInt(1, additional);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Order quantity updated successfully!");
            } else {
                System.out.println("Order does not exist.");
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // (d) Update order status if valid
    public static void updateStatus() {

        String[] validStatuses = {"Pending", "Shipped", "Delivered"};

        System.out.print("Enter Order ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter new status: ");
        String newStatus = sc.nextLine();

        boolean valid = false;

        for (String s : validStatuses) {
            if (s.equalsIgnoreCase(newStatus)) {
                newStatus = s;
                valid = true;
                break;
            }
        }

        if (!valid) {
            System.out.println("Invalid status! Use Pending, Shipped, or Delivered.");
            return;
        }

        String selectSql = "SELECT * FROM Orders WHERE order_id = ?";
        String updateSql = "UPDATE Orders SET status = ? WHERE order_id = ?";

        try (Connection con = getConnection();
             PreparedStatement selectPs = con.prepareStatement(selectSql)) {

            selectPs.setInt(1, id);

            try (ResultSet rs = selectPs.executeQuery()) {

                if (!rs.next()) {
                    System.out.println("Order does not exist.");
                    return;
                }

                try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {

                    updatePs.setString(1, newStatus);
                    updatePs.setInt(2, id);

                    updatePs.executeUpdate();

                    System.out.println("Order status updated successfully!");

                }
            }

            // Display updated details
            try (PreparedStatement ps = con.prepareStatement(selectSql)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("\nUpdated Order Details:");
                        displayOrder(rs);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // (e) Display total order value
    public static void displayTotalValue() {

        String sql = "SELECT SUM(quantity * unit_price) AS total_value FROM Orders";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                System.out.println("Total Order Value: " + rs.getDouble("total_value"));
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // (e) Delete only if order status is Cancelled
    public static void deleteOrder() {

        String sql = "DELETE FROM Orders WHERE order_id = ? AND status = 'Cancelled'";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter Order ID to delete: ");
            int id = sc.nextInt();
            sc.nextLine();

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Cancelled order deleted successfully!");
            } else {
                System.out.println("Order cannot be deleted. It either does not exist or is not Cancelled.");
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // Main menu
    public static void main(String[] args) {

        try (Connection con = getConnection()) {
            System.out.println("Database Connected Successfully!");
        } catch (SQLException e) {
            System.out.println("Connection Failed: " + e.getMessage());
            return;
        }

        int choice;

        do {
            System.out.println("\n========== ORDER MANAGEMENT SYSTEM ==========");
            System.out.println("1. Insert Three Orders");
            System.out.println("2. Search Orders by Status");
            System.out.println("3. Increase Order Quantity");
            System.out.println("4. Update Order Status");
            System.out.println("5. Display Total Order Value");
            System.out.println("6. Delete Cancelled Order");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    insertOrders();
                    break;

                case 2:
                    searchOrders();
                    break;

                case 3:
                    increaseQuantity();
                    break;

                case 4:
                    updateStatus();
                    break;

                case 5:
                    displayTotalValue();
                    break;

                case 6:
                    deleteOrder();
                    break;

                case 0:
                    System.out.println("Exiting application...");
                    break;

                default:
                    System.out.println("Invalid choice! Try again.");
            }

        } while (choice != 0);

        sc.close();
    }
}
