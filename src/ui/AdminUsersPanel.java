package ui;

import dao.UserDAO;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * AdminUsersPanel - Panel to display and manage all users in a table
 */
public class AdminUsersPanel extends JPanel {
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private UserDAO userDAO;
    private JTextField searchField;
    private JLabel totalUsersLabel;

    public AdminUsersPanel() {
        this.userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }

    private void initializeComponents() {
        // Create table model with columns
        String[] columnNames = {"ID", "Name", "Email", "Date of Birth", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        usersTable = new JTable(tableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.setRowHeight(30);
        usersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        usersTable.setFont(new Font("Arial", Font.PLAIN, 13));

        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        usersTable.setRowSorter(sorter);

        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Total users label
        totalUsersLabel = new JLabel("Total Users: 0");
        totalUsersLabel.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel with search and stats
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search Users:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        
        JButton refreshButton = new JButton("🔄 Refresh");
        styleButton(refreshButton, new Color(52, 152, 219));
        searchPanel.add(refreshButton);
        
        // Stats panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.add(totalUsersLabel);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(statsPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Registered Users"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with actions
        JPanel bottomPanel = new JPanel(new FlowLayout());
        
        JButton viewDetailsButton = new JButton("👁 View Details");
        JButton exportButton = new JButton("📄 Export Data");
        
        styleButton(viewDetailsButton, new Color(155, 89, 182));
        styleButton(exportButton, new Color(39, 174, 96));
        
        bottomPanel.add(viewDetailsButton);
        bottomPanel.add(exportButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // Search functionality
        searchField.addActionListener(e -> filterTable());
        
        // Real-time search as user types
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        // Refresh button
        Component[] components = ((JPanel) getComponent(0)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                for (Component subComp : ((JPanel) comp).getComponents()) {
                    if (subComp instanceof JButton && ((JButton) subComp).getText().contains("Refresh")) {
                        ((JButton) subComp).addActionListener(e -> refreshData());
                        break;
                    }
                }
            }
        }

        // View details button
        Component[] bottomComponents = ((JPanel) getComponent(2)).getComponents();
        for (Component comp : bottomComponents) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getText().contains("View Details")) {
                    button.addActionListener(e -> viewUserDetails());
                } else if (button.getText().contains("Export")) {
                    button.addActionListener(e -> exportUserData());
                }
            }
        }
    }

    private void filterTable() {
        String searchText = searchField.getText().trim().toLowerCase();
        @SuppressWarnings("unchecked")
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) usersTable.getRowSorter();
        
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
        
        updateStats();
    }

    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Clear existing data
                tableModel.setRowCount(0);
                
                // Get all users from database
                List<User> users = userDAO.findAll();
                
                // Add users to table
                for (User user : users) {
                    Object[] rowData = {
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getDateOfBirth(),
                        capitalize(user.getRole())
                    };
                    tableModel.addRow(rowData);
                }
                
                updateStats();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading users: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void updateStats() {
        int totalUsers = usersTable.getRowCount();
        int visibleUsers = usersTable.getRowSorter() != null ? 
            usersTable.getRowSorter().getViewRowCount() : totalUsers;
        
        if (visibleUsers == totalUsers) {
            totalUsersLabel.setText("Total Users: " + totalUsers);
        } else {
            totalUsersLabel.setText("Showing: " + visibleUsers + " of " + totalUsers + " users");
        }
    }

    private void viewUserDetails() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a user to view details.", 
                "No Selection", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Get user data from selected row
        int modelRow = usersTable.convertRowIndexToModel(selectedRow);
        Object[] rowData = new Object[tableModel.getColumnCount()];
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            rowData[i] = tableModel.getValueAt(modelRow, i);
        }

        // Show details dialog
        showUserDetailsDialog(rowData);
    }

    private void showUserDetailsDialog(Object[] userData) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "User Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"ID:", "Name:", "Email:", "Date of Birth:", "Role:"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            panel.add(label, gbc);
            gbc.gridx = 1;
            JLabel value = new JLabel(userData[i] != null ? userData[i].toString() : "N/A");
            value.setFont(new Font("Arial", Font.PLAIN, 12));
            panel.add(value, gbc);
        }

        dialog.add(panel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void exportUserData() {
        JOptionPane.showMessageDialog(this, 
            "Export functionality will be implemented in future version.", 
            "Coming Soon", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(130, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
