package com.harbormgmt.ui.panels;

import com.harbormgmt.dao.HarborDAO;
import com.harbormgmt.model.Harbor;
import com.harbormgmt.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Panel for viewing and managing harbor data
 */
public class HarborPanel extends JPanel {
    
    private final HarborDAO harborDAO;
    private JTable harborTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    /**
     * Constructor to initialize the harbor panel
     */
    public HarborPanel() {
        this.harborDAO = new HarborDAO();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        refreshData();
    }
    
    /**
     * Initialize panel components
     */
    private void initComponents() {
        // Create the table model with column names
        String[] columnNames = {"ID", "Location", "Manager Name", "Contact Info", "Capacity"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 4) {
                    return Integer.class; // Set column class for sorting
                }
                return String.class;
            }
        };
        
        // Create the table
        harborTable = new JTable(tableModel);
        harborTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        harborTable.setRowHeight(25);
        harborTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        harborTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        harborTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        harborTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        harborTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        harborTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        // Add sorting capability
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        harborTable.setRowSorter(sorter);
        
        // Create scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(harborTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Create search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Search harbors...");
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        
        searchField.addActionListener(e -> performSearch());
        
        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchControlsPanel.add(new JLabel("Search:"));
        searchControlsPanel.add(searchField);
        searchControlsPanel.add(searchButton);
        
        searchPanel.add(searchControlsPanel, BorderLayout.WEST);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton addButton = new JButton("Add Harbor");
        JButton editButton = new JButton("Edit Harbor");
        JButton deleteButton = new JButton("Delete Harbor");
        JButton refreshButton = new JButton("Refresh");
        
        // Add action listeners
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteHarbor());
        refreshButton.addActionListener(e -> refreshData());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        searchPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Add components to panel
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Refresh the harbor data in the table
     */
    public void refreshData() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get all harbors and add to table
        List<Harbor> harbors = harborDAO.getAllHarbors();
        for (Harbor harbor : harbors) {
            Object[] rowData = {
                    harbor.getHarborId(),
                    harbor.getLocation(),
                    harbor.getManagerName(),
                    harbor.getContactInfo(),
                    harbor.getCapacity()
            };
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Perform search on harbor data
     */
    private void performSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        
        if (searchTerm.isEmpty()) {
            refreshData();
            return;
        }
        
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get all harbors and filter
        List<Harbor> harbors = harborDAO.getAllHarbors();
        for (Harbor harbor : harbors) {
            if (harbor.getLocation().toLowerCase().contains(searchTerm) ||
                    harbor.getManagerName().toLowerCase().contains(searchTerm) ||
                    harbor.getContactInfo().toLowerCase().contains(searchTerm)) {
                
                Object[] rowData = {
                        harbor.getHarborId(),
                        harbor.getLocation(),
                        harbor.getManagerName(),
                        harbor.getContactInfo(),
                        harbor.getCapacity()
                };
                tableModel.addRow(rowData);
            }
        }
    }
    
    /**
     * Show dialog for adding a new harbor
     */
    public void showAddDialog() {
        // Create dialog components
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Harbor", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel locationLabel = new JLabel("Location:");
        JTextField locationField = new JTextField(20);
        
        JLabel managerLabel = new JLabel("Manager Name:");
        JTextField managerField = new JTextField(20);
        
        JLabel contactLabel = new JLabel("Contact Info:");
        JTextField contactField = new JTextField(20);
        
        JLabel capacityLabel = new JLabel("Capacity:");
        JTextField capacityField = new JTextField(20);
        
        formPanel.add(locationLabel);
        formPanel.add(locationField);
        formPanel.add(managerLabel);
        formPanel.add(managerField);
        formPanel.add(contactLabel);
        formPanel.add(contactField);
        formPanel.add(capacityLabel);
        formPanel.add(capacityField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (!ValidationUtil.validateRequired(locationField, "Location") ||
                    !ValidationUtil.validateRequired(managerField, "Manager Name") ||
                    !ValidationUtil.validateInteger(capacityField, "Capacity")) {
                return;
            }
            
            // Create new harbor object
            Harbor harbor = new Harbor();
            harbor.setLocation(locationField.getText().trim());
            harbor.setManagerName(managerField.getText().trim());
            harbor.setContactInfo(contactField.getText().trim());
            harbor.setCapacity(Integer.parseInt(capacityField.getText().trim()));
            
            // Save to database
            int newId = harborDAO.addHarbor(harbor);
            if (newId > 0) {
                JOptionPane.showMessageDialog(dialog,
                        "Harbor added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to add harbor. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Add components to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    /**
     * Show dialog for editing an existing harbor
     */
    private void showEditDialog() {
        // Get selected row
        int selectedRow = harborTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a harbor to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get harbor ID from selected row
        int harborId = (int) harborTable.getValueAt(harborTable.convertRowIndexToModel(selectedRow), 0);
        
        // Get harbor data
        Harbor harbor = harborDAO.getHarborById(harborId);
        if (harbor == null) {
            JOptionPane.showMessageDialog(this,
                    "Failed to retrieve harbor data.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create dialog components
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Harbor", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel locationLabel = new JLabel("Location:");
        JTextField locationField = new JTextField(harbor.getLocation(), 20);
        
        JLabel managerLabel = new JLabel("Manager Name:");
        JTextField managerField = new JTextField(harbor.getManagerName(), 20);
        
        JLabel contactLabel = new JLabel("Contact Info:");
        JTextField contactField = new JTextField(harbor.getContactInfo(), 20);
        
        JLabel capacityLabel = new JLabel("Capacity:");
        JTextField capacityField = new JTextField(String.valueOf(harbor.getCapacity()), 20);
        
        formPanel.add(locationLabel);
        formPanel.add(locationField);
        formPanel.add(managerLabel);
        formPanel.add(managerField);
        formPanel.add(contactLabel);
        formPanel.add(contactField);
        formPanel.add(capacityLabel);
        formPanel.add(capacityField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (!ValidationUtil.validateRequired(locationField, "Location") ||
                    !ValidationUtil.validateRequired(managerField, "Manager Name") ||
                    !ValidationUtil.validateInteger(capacityField, "Capacity")) {
                return;
            }
            
            // Update harbor object
            harbor.setLocation(locationField.getText().trim());
            harbor.setManagerName(managerField.getText().trim());
            harbor.setContactInfo(contactField.getText().trim());
            harbor.setCapacity(Integer.parseInt(capacityField.getText().trim()));
            
            // Save to database
            boolean success = harborDAO.updateHarbor(harbor);
            if (success) {
                JOptionPane.showMessageDialog(dialog,
                        "Harbor updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to update harbor. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Add components to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    /**
     * Delete the selected harbor
     */
    private void deleteHarbor() {
        // Get selected row
        int selectedRow = harborTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a harbor to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get harbor ID from selected row
        int harborId = (int) harborTable.getValueAt(harborTable.convertRowIndexToModel(selectedRow), 0);
        
        // Confirm deletion
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this harbor?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            // Delete from database
            boolean success = harborDAO.deleteHarbor(harborId);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Harbor deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete harbor. It may be referenced by other records.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}