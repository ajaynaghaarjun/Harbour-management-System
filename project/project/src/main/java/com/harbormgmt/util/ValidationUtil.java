package com.harbormgmt.util;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Utility class for validation throughout the application
 */
public class ValidationUtil {
    
    /**
     * Validates that a text field is not empty
     * @param field the field to validate
     * @param fieldName the name of the field for error message
     * @return true if valid, false otherwise
     */
    public static boolean validateRequired(JTextField field, String fieldName) {
        if (field.getText().trim().isEmpty()) {
            highlightError(field);
            JOptionPane.showMessageDialog(null, 
                fieldName + " is required.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        resetHighlight(field);
        return true;
    }
    
    /**
     * Validates that a text field contains a valid integer
     * @param field the field to validate
     * @param fieldName the name of the field for error message
     * @return true if valid, false otherwise
     */
    public static boolean validateInteger(JTextField field, String fieldName) {
        try {
            if (!field.getText().trim().isEmpty()) {
                Integer.parseInt(field.getText().trim());
                resetHighlight(field);
                return true;
            } else {
                highlightError(field);
                JOptionPane.showMessageDialog(null, 
                    fieldName + " must be a valid integer.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            highlightError(field);
            JOptionPane.showMessageDialog(null, 
                fieldName + " must be a valid integer.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Validates that a text field contains a valid decimal number
     * @param field the field to validate
     * @param fieldName the name of the field for error message
     * @return true if valid, false otherwise
     */
    public static boolean validateDecimal(JTextField field, String fieldName) {
        try {
            if (!field.getText().trim().isEmpty()) {
                Double.parseDouble(field.getText().trim());
                resetHighlight(field);
                return true;
            } else {
                highlightError(field);
                JOptionPane.showMessageDialog(null, 
                    fieldName + " must be a valid decimal number.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            highlightError(field);
            JOptionPane.showMessageDialog(null, 
                fieldName + " must be a valid decimal number.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Validates that a text field contains a valid date in the format yyyy-MM-dd
     * @param field the field to validate
     * @param fieldName the name of the field for error message
     * @return true if valid, false otherwise
     */
    public static boolean validateDate(JTextField field, String fieldName) {
        if (!field.getText().trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                sdf.parse(field.getText().trim());
                resetHighlight(field);
                return true;
            } catch (ParseException e) {
                highlightError(field);
                JOptionPane.showMessageDialog(null, 
                    fieldName + " must be a valid date in format yyyy-MM-dd.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            highlightError(field);
            JOptionPane.showMessageDialog(null, 
                fieldName + " must be a valid date in format yyyy-MM-dd.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Validates that a text field contains a valid email address
     * @param field the field to validate
     * @param fieldName the name of the field for error message
     * @return true if valid, false otherwise
     */
    public static boolean validateEmail(JTextField field, String fieldName) {
        String email = field.getText().trim();
        if (!email.isEmpty()) {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            if (email.matches(emailRegex)) {
                resetHighlight(field);
                return true;
            } else {
                highlightError(field);
                JOptionPane.showMessageDialog(null, 
                    fieldName + " must be a valid email address.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true; // Allow empty email
    }
    
    /**
     * Highlight a field with error styling
     * @param field the field to highlight
     */
    private static void highlightError(JTextField field) {
        field.setBackground(new Color(255, 221, 221));
        field.setBorder(BorderFactory.createLineBorder(Color.RED));
    }
    
    /**
     * Reset the styling of a field
     * @param field the field to reset
     */
    private static void resetHighlight(JTextField field) {
        field.setBackground(Color.WHITE);
        field.setBorder(UIManager.getBorder("TextField.border"));
    }
}