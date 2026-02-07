package com.harbormgmt.ui.panels;

import com.harbormgmt.dao.CargoDAO;
import com.harbormgmt.dao.ShipDAO;
import com.harbormgmt.model.Cargo;
import com.harbormgmt.model.Ship;
import com.harbormgmt.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Panel for viewing and managing cargo data
 */
public class CargoPanel extends JPanel {
    
    private final CargoDAO cargoDAO;
    private final ShipDAO shipDAO;
    private JTable cargoTable;
    private DefaultTableModel tableModel;
    private JComboBox<Ship> shipFilterCombo;
    
    /**
     * Constructor to initialize the cargo panel
     */
    public CargoPanel() {
        this.cargoDAO = new CargoDAO();
        this.shipDAO = new ShipDAO();
        
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
        String[] columnNames = {"ID", "Ship", "Weight", "Cargo Type ID"};
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
        cargoTable = new JTable(tableModel);
        cargoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cargoTable.setRowHeight(25);
        cargoTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        cargoTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        cargoTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        cargoTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        cargoTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        // Add sorting capability
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        cargoTable.setRowSorter(sorter);
        
        // Create scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(cargoTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Create filter panel
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Create ship filter combo
        JPanel shipFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        shipFilterCombo = new JComboBox<>();
        shipFilterCombo.addItem(null); // Add "All Ships" option
        
        // Add ships to combo
        for (Ship ship : shipDAO.getAllShips()) {
            shipFilterCombo.addItem(ship);
        }
        
        shipFilterCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("All Ships");
                }
                return this;
            }
        });
        
        shipFilterCombo.addActionListener(e -> filterCargo());
        
        shipFilterPanel.add(new JLabel("Filter by Ship:"));
        shipFilterPanel.add(shipFilterCombo);
        
        filterPanel.add(shipFilterPanel, BorderLayout.WEST);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton addButton = new JButton("Add Cargo");
        JButton editButton = new JButton("Edit Cargo");
        JButton deleteButton = new JButton("Delete Cargo");
        JButton refreshButton = new JButton("Refresh");
        
        // Add action listeners
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteCargo());
        refreshButton.addActionListener(e -> refreshData());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        filterPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Add components to panel
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Refresh the cargo data in the table
     */
    public void refreshData() {
        // Reset ship filter
        shipFilterCombo.setSelectedItem(null);
        
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get all cargo and add to table
        List<Cargo> cargoList = cargoDAO.getAllCargo();
        for (Cargo cargo : cargoList) {
            addCargoToTable(cargo);
        }
    }
    
    /**
     * Add a cargo item to the table
     * @param cargo the cargo to add
     */
    private void addCargoToTable(Cargo cargo) {
        // Get ship name
        String shipName = "Unknown";
        Ship ship = shipDAO.getShipById(cargo.getShipId());
        if (ship != null) {
            shipName = ship.getName();
        }
        
        Object[] rowData = {
                cargo.getCargoId(),
                shipName,
                cargo.getWeight(),
                cargo.getCargoTypeId()
        };
        tableModel.addRow(rowData);
    }
    
    /**
     * Filter cargo based on selected ship
     */
    private void filterCargo() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get selected ship
        Ship selectedShip = (Ship) shipFilterCombo.getSelectedItem();
        
        if (selectedShip == null) {
            // No filter, show all cargo
            List<Cargo> cargoList = cargoDAO.getAllCargo();
            for (Cargo cargo : cargoList) {
                addCargoToTable(cargo);
            }
        } else {
            // Filter by ship
            List<Cargo> cargoList = cargoDAO.getCargoByShip(selectedShip.getShipId());
            for (Cargo cargo : cargoList) {
                addCargoToTable(cargo);
            }
        }
    }
    
    /**
     * Show dialog for adding new cargo
     */
    public void showAddDialog() {
        // Create dialog components
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Cargo", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel shipLabel = new JLabel("Ship:");
        JComboBox<Ship> shipCombo = new JComboBox<>();
        
        // Add ships to combo
        for (Ship ship : shipDAO.getAllShips()) {
            shipCombo.addItem(ship);
        }
        
        JLabel weightLabel = new JLabel("Weight:");
        JTextField weightField = new JTextField(20);
        
        JLabel typeLabel = new JLabel("Cargo Type ID:");
        JTextField typeField = new JTextField(20);
        
        formPanel.add(shipLabel);
        formPanel.add(shipCombo);
        formPanel.add(weightLabel);
        formPanel.add(weightField);
        formPanel.add(typeLabel);
        formPanel.add(typeField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (shipCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(dialog,
                        "Please select a ship.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!ValidationUtil.validateRequired(weightField, "Weight") ||
                    !ValidationUtil.validateDecimal(weightField, "Weight")) {
                return;
            }
            
            if (!typeField.getText().trim().isEmpty() && 
                    !ValidationUtil.validateInteger(typeField, "Cargo Type ID")) {
                return;
            }
            
            // Create new cargo object
            Cargo cargo = new Cargo();
            cargo.setShipId(((Ship) shipCombo.getSelectedItem()).getShipId());
            cargo.setWeight(new BigDecimal(weightField.getText().trim()));
            
            if (!typeField.getText().trim().isEmpty()) {
                cargo.setCargoTypeId(Integer.parseInt(typeField.getText().trim()));
            }
            
            // Check if weight exceeds ship's max weight
            Ship selectedShip = (Ship) shipCombo.getSelectedItem();
            BigDecimal totalWeight = calculateTotalCargoWeight(selectedShip.getShipId());
            totalWeight = totalWeight.add(new BigDecimal(weightField.getText().trim()));
            
            if (selectedShip.getMaxWeight() != null && totalWeight.compareTo(selectedShip.getMaxWeight()) > 0) {
                int response = JOptionPane.showConfirmDialog(dialog,
                        "Warning: This cargo will exceed the ship's maximum weight capacity.\nDo you want to continue?",
                        "Weight Capacity Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // Save to database
            int newId = cargoDAO.addCargo(cargo);
            if (newId > 0) {
                JOptionPane.showMessageDialog(dialog,
                        "Cargo added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to add cargo. Please try again.",
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
     * Show dialog for editing existing cargo
     */
    private void showEditDialog() {
        // Get selected row
        int selectedRow = cargoTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a cargo item to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get cargo ID from selected row
        int cargoId = (int) cargoTable.getValueAt(cargoTable.convertRowIndexToModel(selectedRow), 0);
        
        // Get cargo data
        Cargo cargo = cargoDAO.getCargoById(cargoId);
        if (cargo == null) {
            JOptionPane.showMessageDialog(this,
                    "Failed to retrieve cargo data.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create dialog components
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Cargo", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel shipLabel = new JLabel("Ship:");
        JComboBox<Ship> shipCombo = new JComboBox<>();
        
        // Add ships to combo and select current ship
        List<Ship> ships = shipDAO.getAllShips();
        for (Ship ship : ships) {
            shipCombo.addItem(ship);
            if (ship.getShipId() == cargo.getShipId()) {
                shipCombo.setSelectedItem(ship);
            }
        }
        
        JLabel weightLabel = new JLabel("Weight:");
        JTextField weightField = new JTextField(cargo.getWeight().toString(), 20);
        
        JLabel typeLabel = new JLabel("Cargo Type ID:");
        JTextField typeField = new JTextField(cargo.getCargoTypeId() != null ? cargo.getCargoTypeId().toString() : "", 20);
        
        formPanel.add(shipLabel);
        formPanel.add(shipCombo);
        formPanel.add(weightLabel);
        formPanel.add(weightField);
        formPanel.add(typeLabel);
        formPanel.add(typeField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (shipCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(dialog,
                        "Please select a ship.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!ValidationUtil.validateRequired(weightField, "Weight") ||
                    !ValidationUtil.validateDecimal(weightField, "Weight")) {
                return;
            }
            
            if (!typeField.getText().trim().isEmpty() && 
                    !ValidationUtil.validateInteger(typeField, "Cargo Type ID")) {
                return;
            }
            
            // Update cargo object
            cargo.setShipId(((Ship) shipCombo.getSelectedItem()).getShipId());
            cargo.setWeight(new BigDecimal(weightField.getText().trim()));
            
            if (!typeField.getText().trim().isEmpty()) {
                cargo.setCargoTypeId(Integer.parseInt(typeField.getText().trim()));
            } else {
                cargo.setCargoTypeId(null);
            }
            
            // Check weight only if ship changed or weight increased
            Ship selectedShip = (Ship) shipCombo.getSelectedItem();
            boolean checkWeight = false;
            
            if (selectedShip.getShipId() != cargo.getShipId()) {
                // Ship changed
                checkWeight = true;
            } else if (new BigDecimal(weightField.getText().trim()).compareTo(cargo.getWeight()) > 0) {
                // Weight increased
                checkWeight = true;
            }
            
            if (checkWeight && selectedShip.getMaxWeight() != null) {
                BigDecimal totalWeight = calculateTotalCargoWeight(selectedShip.getShipId());
                totalWeight = totalWeight.subtract(cargo.getWeight()); // Remove current cargo weight
                totalWeight = totalWeight.add(new BigDecimal(weightField.getText().trim())); // Add new weight
                
                if (totalWeight.compareTo(selectedShip.getMaxWeight()) > 0) {
                    int response = JOptionPane.showConfirmDialog(dialog,
                            "Warning: This cargo will exceed the ship's maximum weight capacity.\nDo you want to continue?",
                            "Weight Capacity Warning",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    
                    if (response != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
            }
            
            // Save to database
            boolean success = cargoDAO.updateCargo(cargo);
            if (success) {
                JOptionPane.showMessageDialog(dialog,
                        "Cargo updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to update cargo. Please try again.",
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
     * Delete the selected cargo
     */
    private void deleteCargo() {
        // Get selected row
        int selectedRow = cargoTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a cargo item to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get cargo ID from selected row
        int cargoId = (int) cargoTable.getValueAt(cargoTable.convertRowIndexToModel(selectedRow), 0);
        
        // Confirm deletion
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this cargo item?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            // Delete from database
            boolean success = cargoDAO.deleteCargo(cargoId);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Cargo deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete cargo. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Calculate the total weight of all cargo on a ship
     * @param shipId the ID of the ship
     * @return the total weight
     */
    private BigDecimal calculateTotalCargoWeight(int shipId) {
        BigDecimal totalWeight = BigDecimal.ZERO;
        List<Cargo> cargoList = cargoDAO.getCargoByShip(shipId);
        
        for (Cargo cargo : cargoList) {
            totalWeight = totalWeight.add(cargo.getWeight());
        }
        
        return totalWeight;
    }
}