package com.harbormgmt.app;

import com.formdev.flatlaf.FlatLightLaf;
import com.harbormgmt.ui.MainFrame;
import com.harbormgmt.util.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Main entry point for the Harbor Management System application
 */
public class HarborManagementApp {
    
    public static void main(String[] args) {
        // Set the look and feel to FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        
        // Show a splash screen while loading
        SplashScreen splash = new SplashScreen(3000);
        splash.showSplash();
        
        // Test database connection
        try {
            Connection conn = DatabaseManager.getConnection();
            if (conn != null) {
                System.out.println("Database connection successful!");
                conn.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to database: " + e.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Launch the main application frame
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}

/**
 * Simple splash screen shown while the application is loading
 */
class SplashScreen {
    private final int duration;
    private JWindow splashWindow;
    
    public SplashScreen(int duration) {
        this.duration = duration;
    }
    
    public void showSplash() {
        splashWindow = new JWindow();
        
        // Load splash image
        ImageIcon splashIcon;
        try {
            splashIcon = new ImageIcon(getClass().getResource("/images/splash.png"));
        } catch (Exception e) {
            // Create a default splash if image isn't available
            JPanel panel = new JPanel();
            panel.setBackground(new Color(0, 102, 153));
            panel.setLayout(new BorderLayout());
            
            JLabel title = new JLabel("Harbor Management System", JLabel.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 24));
            title.setForeground(Color.WHITE);
            
            JLabel loading = new JLabel("Loading...", JLabel.CENTER);
            loading.setFont(new Font("Arial", Font.PLAIN, 16));
            loading.setForeground(Color.WHITE);
            
            panel.add(title, BorderLayout.CENTER);
            panel.add(loading, BorderLayout.SOUTH);
            panel.setPreferredSize(new Dimension(450, 300));
            
            splashWindow.add(panel);
            splashWindow.pack();
            splashWindow.setLocationRelativeTo(null);
            splashWindow.setVisible(true);
            
            // Close splash after duration
            new Thread(() -> {
                try {
                    Thread.sleep(duration);
                    splashWindow.dispose();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }).start();
            
            return;
        }
        
        // Create splash with image
        JLabel splashLabel = new JLabel(splashIcon);
        splashWindow.add(splashLabel);
        splashWindow.pack();
        splashWindow.setLocationRelativeTo(null);
        splashWindow.setVisible(true);
        
        // Close splash after duration
        new Thread(() -> {
            try {
                Thread.sleep(duration);
                splashWindow.dispose();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}