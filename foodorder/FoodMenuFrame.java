import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FoodMenuFrame extends JFrame implements ActionListener {
    private Connection connection;
    private JPanel foodPanel;
    private JTextField[] quantityFields; // Array to store quantity text fields

    public FoodMenuFrame(Connection connection) {
        this.connection = connection;

        setTitle("Food Menu");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create panel for food menu
        foodPanel = new JPanel(new GridLayout(10, 3));
        foodPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Sample food items and prices
        String[] foodItems = {"Burger", "Pizza", "Pasta", "Salad", "Sandwich", "Steak", "Sushi", "Soup", "Tacos", "Fries"};
        double[] prices = {10.99, 12.99, 8.99, 7.99, 6.99, 15.99, 14.99, 5.99, 9.99, 3.99};

        quantityFields = new JTextField[foodItems.length]; // Initialize quantity fields array

        // Add food items to panel
        for (int i = 0; i < foodItems.length; i++) {
            JLabel itemLabel = new JLabel(foodItems[i] + " ($" + prices[i] + ")");
            JTextField quantityField = new JTextField(5); // Text field for quantity
            quantityFields[i] = quantityField; // Store quantity field in array
            JButton orderButton = new JButton("Order");
            orderButton.setActionCommand(foodItems[i]); // Use food item name as command
            orderButton.addActionListener(this);

            foodPanel.add(itemLabel);
            foodPanel.add(quantityField);
            foodPanel.add(orderButton);
        }

        // Add clear button at the top for quantities
        JButton clearButton = new JButton("Clear Quantities");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearQuantities();
            }
        });

        // Add view orders button at the top
        JButton viewOrdersButton = new JButton("View Orders");
        viewOrdersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewOrders();
            }
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(clearButton);
        topPanel.add(viewOrdersButton);

        // Add components to the frame
        add(topPanel, BorderLayout.NORTH);
        add(foodPanel, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        String foodItem = e.getActionCommand();

        // Retrieve quantity input
        int index = getFoodItemIndex(foodItem);
        if (index != -1) {
            JTextField quantityField = quantityFields[index];
            String quantityStr = quantityField.getText();
            int quantity = 0;
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Save order to the database with quantity
                PreparedStatement statement = connection.prepareStatement("INSERT INTO orders (food_item, quantity, price) VALUES (?, ?, ?)");
                statement.setString(1, foodItem);
                statement.setInt(2, quantity);
                statement.setDouble(3, getPriceForItem(foodItem) * quantity); // Calculate total price based on quantity
                statement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Order placed: " + foodItem + " (Quantity: " + quantity + ")", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to place order", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper method to retrieve price for a food item
    private double getPriceForItem(String item) {
        // Implement logic to fetch the price for the given food item
        // This could be from a predefined array or database query
        // For demonstration purposes, using a predefined array
        String[] foodItems = {"Burger", "Pizza", "Pasta", "Salad", "Sandwich", "Steak", "Sushi", "Soup", "Tacos", "Fries"};
        double[] prices = {10.99, 12.99, 8.99, 7.99, 6.99, 15.99, 14.99, 5.99, 9.99, 3.99};

        for (int i = 0; i < foodItems.length; i++) {
            if (foodItems[i].equals(item)) {
                return prices[i];
            }
        }
        return 0.0; // Default fallback
    }

    // Helper method to clear all quantity fields
    private void clearQuantities() {
        for (JTextField field : quantityFields) {
            field.setText(""); // Clear each quantity field
        }
    }

    // Helper method to view orders
    private void viewOrders() {
        try {
            // Fetch and display orders from the database
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM orders");

            StringBuilder ordersInfo = new StringBuilder();
            ordersInfo.append("----- Orders -----\n");
            while (resultSet.next()) {
                String foodItem = resultSet.getString("food_item");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");

                ordersInfo.append(foodItem).append(" (Quantity: ").append(quantity).append(") - $").append(price).append("\n");
            }

            JOptionPane.showMessageDialog(this, ordersInfo.toString(), "Orders", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve orders", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to get index of a food item in the array
    private int getFoodItemIndex(String item) {
        String[] foodItems = {"Burger", "Pizza", "Pasta", "Salad", "Sandwich", "Steak", "Sushi", "Soup", "Tacos", "Fries"};
        for (int i = 0; i < foodItems.length; i++) {
            if (foodItems[i].equals(item)) {
                return i;
            }
        }
        return -1; // Item not found
    }
}
