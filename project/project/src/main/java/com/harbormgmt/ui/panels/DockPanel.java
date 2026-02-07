package com.harbormgmt.ui.panels;

import com.harbormgmt.dao.DockDAO;
import com.harbormgmt.model.Dock;
import com.harbormgmt.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Panel for viewing and managing dock data
 */
public class DockPanel extends JPanel {
    
    private final DockDAO dockDAO;
    private JTable dockTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    
    /**
     * Constructor to initialize the dock panel
     */
    public DockPanel() {
        this.dockDAO = new DockDAO();
        
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
        String[] columnNames = {"ID", "Dock Number", "Length", "Status", "Location ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 2 || columnIndex == 4) {
                    return Integer.class; // Set column class for sorting
                }
                return String.class;
            }
        };
        
        // Create the table
        dockTable = new JTable(tableModel);
        dockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dockTable.setRowHeight(25);
        dockTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        dockTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        dockTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        dockTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        dockTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        dockTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        // Add sorting capability
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        dockTable.setRowSorter(sorter);
        
        // Create scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(dockTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Create search and filter panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Search docks...");
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        
        searchField.addActionListener(e -> performSearch());
        
        // Create status filter combo
        statusFilterCombo = new JComboBox<>(new String[]{"All Statuses", "Available", "Occupied", "Under Maintenance"});
        statusFilterCombo.addActionListener(e -> performSearch());
        
        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchControlsPanel.add(new JLabel("Search:"));
        searchControlsPanel.add(searchField);
        searchControlsPanel.add(searchButton);
        searchControlsPanel.add(new JLabel("Status:"));
        searchControlsPanel.add(statusFilterCombo);
        
        searchPanel.add(searchControlsPanel, BorderLayout.WEST);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton addButton = new JButton("Add Dock");
        JButton editButton = new JButton("Edit Dock");
        JButton deleteButton = new JButton("Delete Dock");
        JButton refreshButton = new JButton("Refresh");
        
        // Add action listeners
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteDock());
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
     * Refresh the dock data in the table
     */
    public void refreshData() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get all docks and add to table
        List<Dock> docks = dockDAO.getAllDocks();
        for (Dock dock : docks) {
            Object[] rowData = {
                    dock.getDockId(),
                    dock.getDockNumber(),
                    dock.getLength(),
                    dock.getStatus(),
                    dock.getLocationId()
            };
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Perform search on dock data
     */
    private void performSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        String statusFilter = statusFilterCombo.getSelectedItem().toString();
        
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get all docks and filter
        List<Dock> docks = dockDAO.getAllDocks();
        for (Dock dock : docks) {
            String status = dock.getStatus();
            
            // Check status filter
            if (!statusFilter.equals("All Statuses") && !statusFilter.equals(status)) {
                continue;
            }
            
            // Check search term
            if (!searchTerm.isEmpty()) {
                if (!dock.getDockNumber().toLowerCase().contains(searchTerm)) {
                    continue;
                }
            }
            
            // Dock passed filters, add to table
            Object[] rowData = {
                    dock.getDockId(),
                    dock.getDockNumber(),
                    dock.getLength(),
                    dock.getStatus(),
                    dock.getLocationId()
            };
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Show dialog for adding a new dock
     */
    public void showAddDialog() {
        // Create dialog components
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Dock", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel numberLabel = new JLabel("Dock Number:");
        JTextField numberField = new JTextField(20);
        
        JLabel lengthLabel = new JLabel("Length:");
        JTextField lengthField = new JTextField(20);
        
        JLabel statusLabel = new JLabel("Status:");
        String[] statuses = {"Available", "Occupied", "Under Maintenance"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        
        JLabel locationLabel = new JLabel("Location ID:");
        JTextField locationField = new JTextField(20);
        
        formPanel.add(numberLabel);
        formPanel.add(numberField);
        formPanel.add(lengthLabel);
        formPanel.add(lengthField);
        formPanel.add(statusLabel);
        formPanel.add(statusCombo);
        formPanel.add(locationLabel);
        formPanel.add(locationField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (!ValidationUtil.validateRequired(numberField, "Dock Number") ||
                    !ValidationUtil.validateInteger(lengthField, "Length")) {
                return;
            }
            
            if (!locationField.getText().trim().isEmpty() && 
                    !ValidationUtil.validateInteger(locationField, "Location ID")) {
                return;
            }
            
            // Create new dock object
            Dock dock = new Dock();
            dock.setDockNumber(numberField.getText().trim());
            dock.setLength(Integer.parseInt(lengthField.getText().trim()));
            dock.setStatus((String) statusCombo.getSelectedItem());
            
            if (!locationField.getText().trim().isEmpty()) {
                dock.setLocationId(Integer.parseInt(locationField.getText().trim()));
            }
            
            // Save to database
            int newId = dockDAO.addDock(dock);
            if (newId > 0) {
                JOptionPane.showMessageDialog(dialog,
                        "Dock added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to add dock. Please try again.",
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
     * Show dialog for editing an existing dock
     */
    private void showEditDialog() {
        // Get selected row
        int selectedRow = dockTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a dock to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get dock ID from selected row
        int dockId = (int) dockTable.getValueAt(dockTable.convertRowIndexToModel(selectedRow), 0);
        
        // Get dock data
        Dock dock = dockDAO.getDockById(dockId);
        if (dock == null) {
            JOptionPane.showMessageDialog(this,
                    "Failed to retrieve dock data.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create dialog components
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Dock", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel numberLabel = new JLabel("Dock Number:");
        JTextField numberField = new JTextField(dock.getDockNumber(), 20);
        
        JLabel lengthLabel = new JLabel("Length:");
        JTextField lengthField = new JTextField(String.valueOf(dock.getLength()), 20);
        
        JLabel statusLabel = new JLabel("Status:");
        String[] statuses = {"Available", "Occupied", "Under Maintenance"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(dock.getStatus());
        
        JLabel locationLabel = new JLabel("Location ID:");
        JTextField locationField = new JTextField(dock.getLocationId() != null ? dock.getLocationId().toString() : "", 20);
        
        formPanel.add(numberLabel);
        formPanel.add(numberField);
        formPanel.add(lengthLabel);
        formPanel.add(lengthField);
        formPanel.add(statusLabel);
        formPanel.add(statusCombo);
        formPanel.add(locationLabel);
        formPanel.add(locationField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (!ValidationUtil.validateRequired(numberField, "Dock Number") ||
                    !ValidationUtil.validateInteger(lengthField, "Length")) {
                return;
            }
            
            if (!locationField.getText().trim().isEmpty() && 
                    !ValidationUtil.validateInteger(locationField, "Location ID")) {
                return;
            }
            
            // Update dock object
            dock.setDockNumber(numberField.getText().trim());
            dock.setLength(Integer.parseInt(lengthField.getText().trim()));
            dock.setStatus((String) statusCombo.getSelectedItem());
            
            if (!locationField.getText().trim().isEmpty()) {
                dock.setLocationId(Integer.parseInt(locationField.getText().trim()));
            } else {
                dock.setLocationId(null);
            }
            
            // Check if dock is under maintenance
            if (dock.getStatus().equals("Under Maintenance")) {
                JOptionPane.showMessageDialog(dialog,
                        "Warning: There is a database trigger that prevents updates to docks under maintenance.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
            
            // Save to database
            boolean success = dockDAO.updateDock(dock);
            if (success) {
                JOptionPane.showMessageDialog(dialog,
                        "Dock updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to update dock. It may be under maintenance.",
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
     * Delete the selected dock
     */
    private void deleteDock() {
        // Get selected row
        int selectedRow = dockTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a dock to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get dock ID from selected row
        int dockId = (int) dockTable.getValueAt(dockTable.convertRowIndexToModel(selectedRow), 0);
        
        // Confirm deletion
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this dock?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            // Delete from database
            boolean success = dockDAO.deleteDock(dockId);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Dock deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete dock. It may be referenced by other records.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}