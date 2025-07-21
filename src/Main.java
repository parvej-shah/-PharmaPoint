import dao.DatabaseInitializer;
import ui.UserAuthUI;

import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Initialize the database first
        DatabaseInitializer.initialize();
        
        // Start the application with the login screen
        SwingUtilities.invokeLater(() -> {
            // Open the login/authentication window
            new UserAuthUI().setVisible(true);
        });
    }
}