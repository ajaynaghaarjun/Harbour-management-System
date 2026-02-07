package com.harbormgmt.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class with helper methods for Swing components
 */
public class SwingUtils {
    
    /**
     * Set up a JTable with consistent styling
     * @param table the table to style
     */
    public static void setupTable(JTable table) {
        // Set row height
        table.setRowHeight(25);
        
        // Set selection mode
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set header styling
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(0, 102, 153));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Set cell alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setCellRenderer(centerRenderer);
        }
    }
    
    /**
     * Create a styled JButton with icon
     * @param text the button text
     * @param iconPath the path to the icon resource
     * @return the styled button
     */
    public static JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text);
        
        // Try to load icon
        try {
            ImageIcon icon = new ImageIcon(SwingUtils.class.getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // Continue without icon
        }
        
        // Style button
        button.setFocusPainted(false);
        
        return button;
    }
    
    /**
     * Format a date using the specified pattern
     * @param date the date to format
     * @param pattern the pattern to use
     * @return the formatted date string
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
    
    /**
     * Create a panel with a bordered title
     * @param title the title text
     * @param component the component to add to the panel
     * @return the panel with border and title
     */
    public static JPanel createTitledPanel(String title, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Create a status label with color based on status
     * @param status the status text
     * @return the styled label
     */
    public static JLabel createStatusLabel(String status) {
        JLabel label = new JLabel(status, JLabel.CENTER);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        if (status.equals("Available")) {
            label.setBackground(new Color(46, 204, 113)); // Green
            label.setForeground(Color.WHITE);
        } else if (status.equals("Occupied") || status.equals("Docked")) {
            label.setBackground(new Color(52, 152, 219)); // Blue
            label.setForeground(Color.WHITE);
        } else if (status.equals("Under Maintenance") || status.contains("Maintenance")) {
            label.setBackground(new Color(231, 76, 60)); // Red
            label.setForeground(Color.WHITE);
        } else if (status.equals("At Sea") || status.equals("Sailing")) {
            label.setBackground(new Color(155, 89, 182)); // Purple
            label.setForeground(Color.WHITE);
        } else {
            label.setBackground(new Color(149, 165, 166)); // Gray
            label.setForeground(Color.WHITE);
        }
        
        return label;
    }
    
    /**
     * Show a confirmation dialog with Yes/No options
     * @param parent the parent component
     * @param message the message to display
     * @param title the dialog title
     * @return true if Yes was selected, false otherwise
     */
    public static boolean showConfirmDialog(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        
        return result == JOptionPane.YES_OPTION;
    }
}