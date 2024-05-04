import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame implements ActionListener {
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton;
    JLabel messageLabel;
    Connection connection;

    public LoginFrame() {
        // Set frame properties
        setTitle("Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame

        // Create a panel for login components
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Initialize components
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        messageLabel = new JLabel();

        // Add components to the panel
        loginPanel.add(new JLabel("Username: "));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password: "));
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel()); // Empty label for spacing
        loginPanel.add(loginButton);

        // Add panel and message label to the frame
        add(loginPanel, BorderLayout.CENTER);
        add(messageLabel, BorderLayout.SOUTH);

        // Register ActionListener for the login button
        loginButton.addActionListener(this);

        // Connect to MySQL database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ams", "root", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                // Execute query to check login credentials
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
                statement.setString(1, username);
                statement.setString(2, password);
                ResultSet resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    // Display successful login message
                    JOptionPane.showMessageDialog(this, "Login successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Open FoodMenu frame
                    openFoodMenuFrame();
                } else {
                    messageLabel.setText("Invalid username or password");
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void openFoodMenuFrame() {
        FoodMenuFrame foodMenuFrame = new FoodMenuFrame(connection);
        foodMenuFrame.setVisible(true);
        this.dispose(); // Close the current login frame
    }
    

    public static void main(String[] args) {
        // Create and display the login frame
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
