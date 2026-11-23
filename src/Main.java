import gui.Dashboard;

import javax.swing.*;

/**
 * Entry point for the HashMap-Based Longest Zero-Sum Subarray Visualizer.
 */
public class Main {
    public static void main(String[] args) {
        // Apply system look and feel hints before creating any GUI components
        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                // Use cross-platform dark-friendly L&F base
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}

            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
        });
    }
}
