package ui;

import javax.swing.*;
import java.awt.*;

public class UserAuthUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Pharma App - User Login & Registration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 350);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Registration Panel
        JPanel registerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField regName = new JTextField(20);
        JTextField regEmail = new JTextField(20);
        JPasswordField regPass = new JPasswordField(20);
        JComboBox<String> regRole = new JComboBox<>(new String[]{"patient", "pharmacy"});
        JButton regBtn = new JButton("Register");

        gbc.gridx = 0; gbc.gridy = 0; registerPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; registerPanel.add(regName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; registerPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; registerPanel.add(regEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2; registerPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; registerPanel.add(regPass, gbc);

        gbc.gridx = 0; gbc.gridy = 3; registerPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; registerPanel.add(regRole, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        registerPanel.add(regBtn, gbc);

        // Login Panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        JTextField loginEmail = new JTextField(20);
        JPasswordField loginPass = new JPasswordField(20);
        JButton loginBtn = new JButton("Login");

        gbc.gridx = 0; gbc.gridy = 0; loginPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; loginPanel.add(loginEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 1; loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; loginPanel.add(loginPass, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginBtn, gbc);

        // Add both tabs
        tabbedPane.addTab("Register", registerPanel);
        tabbedPane.addTab("Login", loginPanel);

        frame.add(tabbedPane);
        frame.setVisible(true);

        // Dummy Action Listeners
        regBtn.addActionListener(e -> {
            String name = regName.getText();
            String email = regEmail.getText();
            String pass = new String(regPass.getPassword());
            String role = regRole.getSelectedItem().toString();

            JOptionPane.showMessageDialog(frame,
                    "Registered:\nName: " + name + "\nEmail: " + email + "\nRole: " + role);
        });

        loginBtn.addActionListener(e -> {
            String email = loginEmail.getText();
            String pass = new String(loginPass.getPassword());

            JOptionPane.showMessageDialog(frame,
                    "Login attempted for:\nEmail: " + email);
        });
    }
}

