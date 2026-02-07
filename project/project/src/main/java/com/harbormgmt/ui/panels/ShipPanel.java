package com.harbormgmt.ui.panels;

import com.harbormgmt.dao.HarborDAO;
import com.harbormgmt.dao.ShipDAO;
import com.harbormgmt.model.Harbor;
import com.harbormgmt.model.Ship;
import com.harbormgmt.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Panel for viewing and managing ship data
 */
public class ShipPanel extends JPanel {
    
    private final ShipDAO shipDAO;
    private final HarborDAO harborDAO;
    private JTable shipTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    
    /**
     * Constructor to initialize the ship panel
     */
    public ShipPanel() {
        this.shipDAO = new ShipDAO();
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
        String[] columnNames = {"ID", "Name", "Contact Info", "Capacity", "Harbor", "Status", "Max Weight"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 3) {
                    return Integer.class; // Set column class for sorting
                }
                return String.class;
            }
        };
        
        // Create the table
        shipTable = new JTable(tableModel);
        shipTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        shipTable.setRowHeight(25);
        shipTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        shipTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        shipTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        shipTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        shipTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        shipTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        shipTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        shipTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        
        // Add sorting capability
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        shipTable.setRowSorter(sorter);
        
        // Create scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(shipTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Create search and filter panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Search ships...");
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        
        searchField.addActionListener(e -> performSearch());
        
        // Create status filter combo
        statusFilterCombo = new JComboBox<>(new String[]{"All Statuses", "Docked", "At Sea", "Sailing", "Maintenance"});
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
        
        JButton addButton = new JButton("Add Ship");
        JButton editButton = new JButton("Edit Ship");
        JButton deleteButton = new JButton("Delete Ship");
        JButton refreshButton = new JButton("Refresh");
        
        // Add action listeners
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteShip());
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
     * Refresh the ship data in the table
     */
    public void refreshData() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get all ships and add to table
        List<Ship> ships = shipDAO.getAllShips();
        for (Ship ship : ships) {
            addShipToTable(ship);
        }
    }
    
    /**
     * Add a ship to the table
     * @param ship the ship to add
     */
    private void addShipToTable(Ship ship) {
        // Get harbor name if harbor ID is set
        String harborName = "";
        if (ship.getHarborId() > 0) {
            Harbor harbor = harborDAO.getHarborById(ship.getHarborId());
            if (harbor != null) {
                harborName = harbor.getLocation();
            }
        }
        
        Object[] rowData = {
                ship.getShipId(),
                ship.getName(),
                ship.getContactInfo(),
                ship.getCapacity(),
                harborName,
                ship.getStatus(),
                ship.getMaxWeight()
        };
        tableModel.addRow(rowData);
    }
    
    /**
     * Perform search on ship data
     */
    private void performSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        String statusFilter = statusFilterCombo.getSelectedItem().toString();
        
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get all ships and filter
        List<Ship> ships = shipDAO.getAllShips();
        for (Ship ship : ships) {
            String status = ship.getStatus();
            
            // Check status filter
            if (!statusFilter.equals("All Statuses") && !statusFilter.equals(status)) {
                continue;
            }
            
            // Check search term
            if (!searchTerm.isEmpty()) {
                if (!(ship.getName().toLowerCase().contains(searchTerm) ||
                        (ship.getContactInfo() != null && ship.getContactInfo().toLowerCase().contains(searchTerm)))) {
                    continue;
                }
            }
            
            // Ship passed filters, add to table
            addShipToTable(ship);
        }
    }
    
    /**
     * Show dialog for adding a new ship
     */
    public void showAddDialog() {
        // Create dialog components
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Ship", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        
        JLabel contactLabel = new JLabel("Contact Info:");
        JTextField contactField = new JTextField(20);
        
        JLabel capacityLabel = new JLabel("Capacity:");
        JTextField capacityField = new JTextField(20);
        
        JLabel harborLabel = new JLabel("Harbor:");
        JComboBox<Harbor> harborCombo = new JComboBox<>();
        harborCombo.addItem(null); // Add empty option
        for (Harbor harbor : harborDAO.getAllHarbors()) {
            harborCombo.addItem(harbor);
        }
        
        JLabel statusLabel = new JLabel("Status:");
        String[] statuses = {"Docked", "At Sea", "Sailing", "Maintenance"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        
        JLabel maxWeightLabel = new JLabel("Max Weight:");
        JTextField maxWeightField = new JTextField(20);
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(contactLabel);
        formPanel.add(contactField);
        formPanel.add(capacityLabel);
        formPanel.add(capacityField);
        formPanel.add(harborLabel);
        formPanel.add(harborCombo);
        formPanel.add(statusLabel);
        formPanel.add(statusCombo);
        formPanel.add(maxWeightLabel);
        formPanel.add(maxWeightField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (!ValidationUtil.validateRequired(nameField, "Name") ||
                    !ValidationUtil.validateInteger(capacityField, "Capacity")) {
                return;
            }
            
            if (!maxWeightField.getText().trim().isEmpty() && 
                    !ValidationUtil.validateDecimal(maxWeightField, "Max Weight")) {
                return;
            }
            
            // Create new ship object
            Ship ship = new Ship();
            ship.setName(nameField.getText().trim());
            ship.setContactInfo(contactField.getText().trim());
            ship.setCapacity(Integer.parseInt(capacityField.getText().trim()));
            
            Harbor selectedHarbor = (Harbor) harborCombo.getSelectedItem();
            if (selectedHarbor != null) {
                ship.setHarborId(selectedHarbor.getHarborId());
            }
            
            ship.setStatus((String) statusCombo.getSelectedItem());
            
            if (!maxWeightField.getText().trim().isEmpty()) {
                ship.setMaxWeight(new BigDecimal(maxWeightField.getText().trim()));
            }
            
            // Save to database
            int newId = shipDAO.addShip(ship);
            if (newId > 0) {
                JOptionPane.showMessageDialog(dialog,
                        "Ship added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to add ship. Please try again.",
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
     * Show dialog for editing an existing ship
     */
    private void showEditDialog() {
        // Get selected row
        int selectedRow = shipTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a ship to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get ship ID from selected row
        int shipId = (int) shipTable.getValueAt(shipTable.convertRowIndexToModel(selectedRow), 0);
        
        // Get ship data
        Ship ship = shipDAO.getShipById(shipId);
        if (ship == null) {
            JOptionPane.showMessageDialog(this,
                    "Failed to retrieve ship data.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create dialog components
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Ship", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(ship.getName(), 20);
        
        JLabel contactLabel = new JLabel("Contact Info:");
        JTextField contactField = new JTextField(ship.getContactInfo() != null ? ship.getContactInfo() : "", 20);
        
        JLabel capacityLabel = new JLabel("Capacity:");
        JTextField capacityField = new JTextField(String.valueOf(ship.getCapacity()), 20);
        
        JLabel harborLabel = new JLabel("Harbor:");
        JComboBox<Harbor> harborCombo = new JComboBox<>();
        harborCombo.addItem(null); // Add empty option
        
        List<Harbor> harbors = harborDAO.getAllHarbors();
        for (Harbor harbor : harbors) {
            harborCombo.addItem(harbor);
            if (ship.getHarborId() > 0 && harbor.getHarborId() == ship.getHarborId()) {
                harborCombo.setSelectedItem(harbor);
            }
        }
        
        JLabel statusLabel = new JLabel("Status:");
        String[] statuses = {"Docked", "At Sea", "Sailing", "Maintenance"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(ship.getStatus());
        
        JLabel maxWeightLabel = new JLabel("Max Weight:");
        JTextField maxWeightField = new JTextField(ship.getMaxWeight() != null ? ship.getMaxWeight().toString() : "", 20);
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(contactLabel);
        formPanel.add(contactField);
        formPanel.add(capacityLabel);
        formPanel.add(capacityField);
        formPanel.add(harborLabel);
        formPanel.add(harborCombo);
        formPanel.add(statusLabel);
        formPanel.add(statusCombo);
        formPanel.add(maxWeightLabel);
        formPanel.add(maxWeightField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (!ValidationUtil.validateRequired(nameField, "Name") ||
                    !ValidationUtil.validateInteger(capacityField, "Capacity")) {
                return;
            }
            
            if (!maxWeightField.getText().trim().isEmpty() && 
                    !ValidationUtil.validateDecimal(maxWeightField, "Max Weight")) {
                return;
            }
            
            // Update ship object
            ship.setName(nameField.getText().trim());
            ship.setContactInfo(contactField.getText().trim());
            ship.setCapacity(Integer.parseInt(capacityField.getText().trim()));
            
            Harbor selectedHarbor = (Harbor) harborCombo.getSelectedItem();
            if (selectedHarbor != null) {
                ship.setHarborId(selectedHarbor.getHarborId());
            } else {
                ship.setHarborId(0);
            }
            
            ship.setStatus((String) statusCombo.getSelectedItem());
            
            if (!maxWeightField.getText().trim().isEmpty()) {
                ship.setMaxWeight(new BigDecimal(maxWeightField.getText().trim()));
            } else {
                ship.setMaxWeight(null);
            }
            
            // Save to database
            boolean success = shipDAO.updateShip(ship);
            if (success) {
                JOptionPane.showMessageDialog(dialog,
                        "Ship updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to update ship. Please try again.",
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
     * Delete the selected ship
     */
    private void deleteShip() {
        // Get selected row
        int selectedRow = shipTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a ship to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get ship ID from selected row
        int shipId = (int) shipTable.getValueAt(shipTable.convertRowIndexToModel(selectedRow), 0);
        
        // Confirm deletion
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this ship?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            // Delete from database
            boolean success = shipDAO.deleteShip(shipId);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Ship deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete ship. It may be referenced by other records.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}