package ui;

import models.User;
import services.UserService;
import utils.Validator;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class UserAuthUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Pharma App - User Login & Registration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 400);

        JTabbedPane tabbedPane = new JTabbedPane();

        // === Registration Panel ===
        JPanel registerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField regName = new JTextField(20);
        JTextField regEmail = new JTextField(20);
        JPasswordField regPass = new JPasswordField(20);

        // Date of Birth Components
        JComboBox<String> dayCombo = new JComboBox<>();
        JComboBox<String> monthCombo = new JComboBox<>(new String[]{
                "01", "02", "03", "04", "05", "06",
                "07", "08", "09", "10", "11", "12"
        });
        JComboBox<String> yearCombo = new JComboBox<>();

        for (int i = 1; i <= 31; i++) {
            dayCombo.addItem(String.format("%02d", i));
        }
        for (int i = 2024; i >= 1900; i--) {
            yearCombo.addItem(Integer.toString(i));
        }

        JComboBox<String> regRole = new JComboBox<>(new String[]{"patient", "pharmacist"});
        JButton regBtn = new JButton("Register");

        gbc.gridx = 0; gbc.gridy = 0; registerPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; registerPanel.add(regName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; registerPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; registerPanel.add(regEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2; registerPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; registerPanel.add(regPass, gbc);

        gbc.gridx = 0; gbc.gridy = 3; registerPanel.add(new JLabel("Date of Birth:"), gbc);
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        dobPanel.add(dayCombo);
        dobPanel.add(monthCombo);
        dobPanel.add(yearCombo);
        gbc.gridx = 1; gbc.gridy = 3; registerPanel.add(dobPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 4; registerPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; registerPanel.add(regRole, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        registerPanel.add(regBtn, gbc);

        // === Login Panel ===
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

        // === Add Tabs ===
        tabbedPane.addTab("Register", registerPanel);
        tabbedPane.addTab("Login", loginPanel);

        frame.add(tabbedPane);
        frame.setVisible(true);

        // === Register Button Action ===
        regBtn.addActionListener(e -> {
            String name = regName.getText();
            String email = regEmail.getText();
            String pass = new String(regPass.getPassword());
            String role = Objects.requireNonNull(regRole.getSelectedItem()).toString();
            String dob = yearCombo.getSelectedItem() + "-" +
                    monthCombo.getSelectedItem() + "-" +
                    dayCombo.getSelectedItem();
            User  user = new User(name, email, pass, role, dob);
            if (Validator.validateUser(user)) {
                UserService userService = new UserService();
                if (userService.registerUser(user)) {
                    JOptionPane.showMessageDialog(frame,
                            "Registered:\nName: " + name +
                                    "\nEmail: " + email +
                                    "\nDOB: " + dob +
                                    "\nRole: " + role);
                }
                else{
                    JOptionPane.showMessageDialog(frame,
                            "Registered failed:\nName: " + name +
                                    "\nEmail: " + email +
                                    "\nDOB: " + dob +
                                    "\nRole: " + role);
                }
            }
            else {
                JOptionPane.showMessageDialog(frame,
                        "Registered failed:\nName: " + name +
                                "\nEmail: " + email +
                                "\nDOB: " + dob +
                                "\nRole: " + role);
            }
        });

        // === Login Button Action ===
        loginBtn.addActionListener(e -> {
            String email = loginEmail.getText();
            String pass = new String(loginPass.getPassword());

            JOptionPane.showMessageDialog(frame,
                    "Login attempted for:\nEmail: " + email);
        });
    }
}
