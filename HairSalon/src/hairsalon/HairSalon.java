package hairsalon;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class HairSalon extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField nameField;
    private JComboBox<String> sessionComboBox;
    private JComboBox<String> paymentComboBox;
    private JButton confirmButton;
    private JTextArea receiptArea;

    private String[] services = {"Haircut", "Shampoo & Blowdry", "Hair Color", "Bleaching", "Treatment", "Rebonding", "Perm", "Hair and Make-up", "Full make-up", "Hairstyling"};
    private double[] prices = {150, 120, 500, 1500, 350, 3000, 1200, 999.99, 779.99, 559.99};

    private List<String> selectedServices = new ArrayList<>();
    private double totalCost = 0.0;

    public HairSalon() {
        setTitle("Hair-itage Boutique");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add login and appointment screens to the main panel
        mainPanel.add(new LoginPanel(), "Login");
        mainPanel.add(new AppointmentPanel(), "Appointment");

        add(mainPanel);
    }

    // Authentication method
    private boolean authenticate(String email, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials.length == 2) {
                    String storedEmail = credentials[0].trim();
                    String storedPassword = credentials[1].trim();
                    if (storedEmail.equals(email) && storedPassword.equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error reading user database: " + ex.getMessage());
        }
        return false;
    }

    private class LoginPanel extends JPanel {
        private JTextField emailField;
        private JPasswordField passwordField;
        private JButton loginButton;
        private Image backgroundImage;

        public LoginPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            // Load the background image
            backgroundImage = new ImageIcon("C:\\Users\\user\\Downloads\\hairsalon.jpg").getImage();

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;

            // Set color to white for "Email" label
            JLabel emailLabel = new JLabel("Email: ");
            emailLabel.setForeground(Color.WHITE);  // Change label color to white
            add(emailLabel, gbc);

            gbc.gridx = 1;
            emailField = new JTextField(20);
            add(emailField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;

            // Set color to white for "Password" label
            JLabel passwordLabel = new JLabel("Password: ");
            passwordLabel.setForeground(Color.WHITE);  // Change label color to white
            add(passwordLabel, gbc);

            gbc.gridx = 1;
            passwordField = new JPasswordField(20);
            add(passwordField, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.EAST;
            loginButton = new JButton("Login");
            loginButton.addActionListener(new LoginButtonListener());
            add(loginButton, gbc);
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }

        private class LoginButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                char[] password = passwordField.getPassword();

                // Simple validation for demonstration purposes
                if (email.isEmpty() || password.length == 0) {
                    JOptionPane.showMessageDialog(null, "Please enter both email and password.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Authenticate against the users.txt file
                if (authenticate(email, String.valueOf(password))) {
                    cardLayout.show(mainPanel, "Appointment");
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class AppointmentPanel extends JPanel {
        public AppointmentPanel() {
            setLayout(new BorderLayout());

            // Name Panel
            JPanel namePanel = new JPanel();
            namePanel.add(new JLabel("Input user's name: "));
            nameField = new JTextField(20);
            namePanel.add(nameField);
            add(namePanel, BorderLayout.NORTH);

            // Services Panel
            JPanel servicesPanel = new JPanel();
            servicesPanel.setLayout(new GridLayout(services.length, 1, 5, 5));
            servicesPanel.setBorder(BorderFactory.createTitledBorder("Available Services"));

            for (int i = 0; i < services.length; i++) {
                JButton serviceButton = new JButton(services[i] + " - ₱" + prices[i]);
                int index = i;
                serviceButton.addActionListener(e -> {
                    if (!selectedServices.contains(services[index])) {
                        selectedServices.add(services[index]);
                        totalCost += prices[index];
                        serviceButton.setBackground(Color.GREEN);
                    } else {
                        selectedServices.remove(services[index]);
                        totalCost -= prices[index];
                        serviceButton.setBackground(null);
                    }
                });
                servicesPanel.add(serviceButton);
            }

            add(new JScrollPane(servicesPanel), BorderLayout.CENTER);

            // Session and Payment Panel
            JPanel sessionPaymentPanel = new JPanel();
            sessionPaymentPanel.setLayout(new GridLayout(2, 2, 5, 5));
            sessionPaymentPanel.setBorder(BorderFactory.createTitledBorder("Appointment Details"));

            sessionPaymentPanel.add(new JLabel("Choose appointment session:"));
            String[] sessions = {"AM", "PM"};
            sessionComboBox = new JComboBox<>(sessions);
            sessionPaymentPanel.add(sessionComboBox);

            sessionPaymentPanel.add(new JLabel("Select payment method:"));
            String[] payments = {"Cash", "Credit Card", "Mobile Payment"};
            paymentComboBox = new JComboBox<>(payments);
            sessionPaymentPanel.add(paymentComboBox);

            add(sessionPaymentPanel, BorderLayout.SOUTH);

            // Confirm Button
            confirmButton = new JButton("Confirm");
            confirmButton.addActionListener(new ConfirmButtonListener());
            add(confirmButton, BorderLayout.PAGE_END);

            // Receipt Area
            receiptArea = new JTextArea();
            receiptArea.setEditable(false);
            receiptArea.setBorder(BorderFactory.createTitledBorder("Official Receipt"));
            add(new JScrollPane(receiptArea), BorderLayout.EAST);
        }
    }

    private class ConfirmButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String session = (String) sessionComboBox.getSelectedItem();
            String paymentMethod = (String) paymentComboBox.getSelectedItem();

            LocalDateTime myDateObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
            String formattedDate = myDateObj.format(myFormatObj);

            if (selectedServices.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select at least one service.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int response = JOptionPane.showConfirmDialog(null, "Do you want to confirm this transaction?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.NO_OPTION) {
                receiptArea.setText("Transaction Canceled");
                return;
            }

            receiptArea.setText("Official Receipt\n");
            receiptArea.append("Name: " + name + "\n");
            receiptArea.append("Services:\n");
            for (String service : selectedServices) {
                receiptArea.append("- " + service + "\n");
            }
            receiptArea.append("Total Cost: ₱" + totalCost + "\n");
            receiptArea.append("Session: " + session + "\n");
            receiptArea.append("Payment Method: " + paymentMethod + "\n");
            receiptArea.append("Date: " + formattedDate + "\n");

            if (!paymentMethod.equals("Cash")) {
                JOptionPane.showMessageDialog(null, "Please proceed with the " + paymentMethod + " payment process.", "Payment", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Please pay ₱" + totalCost + " in cash.", "Payment", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HairSalon().setVisible(true);
        });
    }
}
