import dao.DatabaseInitializer;
import ui.UserAuthUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();
        SwingUtilities.invokeLater(() -> {
            new UserAuthUI().setVisible(true);
        });
    }
} 
