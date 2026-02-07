package com.harbormgmt.ui.panels;

import com.harbormgmt.dao.EmployeeDAO;
import com.harbormgmt.dao.HarborDAO;
import com.harbormgmt.dao.RoleDAO;
import com.harbormgmt.model.Employee;
import com.harbormgmt.model.Harbor;
import com.harbormgmt.model.Role;
import com.harbormgmt.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Panel for viewing and managing employee data
 */
public class EmployeePanel extends JPanel {
    
    private final EmployeeDAO employeeDAO;
    private final HarborDAO harborDAO;
    private final RoleDAO roleDAO;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    /**
     * Constructor to initialize the employee panel
     */
    public EmployeePanel() {
        this.employeeDAO = new EmployeeDAO();
        this.harborDAO = new HarborDAO();
        this.roleDAO = new RoleDAO();
        
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
        String[] columnNames = {"ID", "Name", "Staff ID", "Contact Info", "Harbor", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Integer.class; // Set column class for sorting
                }
                return String.class;
            }
        };
        
        // Create the table
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(25);
        employeeTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        employeeTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        // Add sorting capability
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        employeeTable.setRowSorter(sorter);
        
        // Create scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Create search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Search employees...");
        
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
        
        JButton addButton = new JButton("Add Employee");
        JButton editButton = new JButton("Edit Employee");
        JButton deleteButton = new JButton("Delete Employee");
        JButton refreshButton = new JButton("Refresh");
        
        // Add action listeners
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteEmployee());
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
     * Refresh the employee data in the table
     */
    public void refreshData() {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get all employees and add to table
        List<Employee> employees = employeeDAO.getAllEmployees();
        for (Employee employee : employees) {
            addEmployeeToTable(employee);
        }
    }
    
    /**
     * Add an employee to the table
     * @param employee the employee to add
     */
    private void addEmployeeToTable(Employee employee) {
        // Get harbor name if harbor ID is set
        String harborName = "";
        if (employee.getHarborId() != null) {
            Harbor harbor = harborDAO.getHarborById(employee.getHarborId());
            if (harbor != null) {
                harborName = harbor.getLocation();
            }
        }
        
        // Get role name if role ID is set
        String roleName = "";
        if (employee.getRoleId() != null) {
            Role role = roleDAO.getRoleById(employee.getRoleId());
            if (role != null) {
                roleName = role.getRoleName();
            }
        }
        
        Object[] rowData = {
                employee.getEmployeeId(),
                employee.getName(),
                employee.getStaffId(),
                employee.getContactInfo(),
                harborName,
                roleName
        };
        tableModel.addRow(rowData);
    }
    
    /**
     * Perform search on employee data
     */
    private void performSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        
        if (searchTerm.isEmpty()) {
            refreshData();
            return;
        }
        
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get all employees and filter
        List<Employee> employees = employeeDAO.getAllEmployees();
        for (Employee employee : employees) {
            if (employee.getName().toLowerCase().contains(searchTerm) ||
                    employee.getStaffId().toLowerCase().contains(searchTerm) ||
                    (employee.getContactInfo() != null && employee.getContactInfo().toLowerCase().contains(searchTerm))) {
                
                addEmployeeToTable(employee);
            }
        }
    }
    
    /**
     * Show dialog for adding a new employee
     */
    public void showAddDialog() {
        // Create dialog components
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Employee", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        
        JLabel staffIdLabel = new JLabel("Staff ID:");
        JTextField staffIdField = new JTextField(20);
        
        JLabel contactLabel = new JLabel("Contact Info:");
        JTextField contactField = new JTextField(20);
        
        JLabel harborLabel = new JLabel("Harbor:");
        JComboBox<Harbor> harborCombo = new JComboBox<>();
        harborCombo.addItem(null); // Add empty option
        for (Harbor harbor : harborDAO.getAllHarbors()) {
            harborCombo.addItem(harbor);
        }
        
        JLabel roleLabel = new JLabel("Role:");
        JComboBox<Role> roleCombo = new JComboBox<>();
        roleCombo.addItem(null); // Add empty option
        for (Role role : roleDAO.getAllRoles()) {
            roleCombo.addItem(role);
        }
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(staffIdLabel);
        formPanel.add(staffIdField);
        formPanel.add(contactLabel);
        formPanel.add(contactField);
        formPanel.add(harborLabel);
        formPanel.add(harborCombo);
        formPanel.add(roleLabel);
        formPanel.add(roleCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (!ValidationUtil.validateRequired(nameField, "Name") ||
                    !ValidationUtil.validateRequired(staffIdField, "Staff ID")) {
                return;
            }
            
            if (!contactField.getText().trim().isEmpty()) {
                ValidationUtil.validateEmail(contactField, "Contact Info");
            }
            
            // Create new employee object
            Employee employee = new Employee();
            employee.setName(nameField.getText().trim());
            employee.setStaffId(staffIdField.getText().trim());
            employee.setContactInfo(contactField.getText().trim());
            
            Harbor selectedHarbor = (Harbor) harborCombo.getSelectedItem();
            if (selectedHarbor != null) {
                employee.setHarborId(selectedHarbor.getHarborId());
            }
            
            Role selectedRole = (Role) roleCombo.getSelectedItem();
            if (selectedRole != null) {
                employee.setRoleId(selectedRole.getRoleId());
            }
            
            // Save to database
            int newId = employeeDAO.addEmployee(employee);
            if (newId > 0) {
                JOptionPane.showMessageDialog(dialog,
                        "Employee added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to add employee. Please try again.",
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
     * Show dialog for editing an existing employee
     */
    private void showEditDialog() {
        // Get selected row
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get employee ID from selected row
        int employeeId = (int) employeeTable.getValueAt(employeeTable.convertRowIndexToModel(selectedRow), 0);
        
        // Get employee data
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        if (employee == null) {
            JOptionPane.showMessageDialog(this,
                    "Failed to retrieve employee data.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create dialog components
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Employee", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(employee.getName(), 20);
        
        JLabel staffIdLabel = new JLabel("Staff ID:");
        JTextField staffIdField = new JTextField(employee.getStaffId(), 20);
        
        JLabel contactLabel = new JLabel("Contact Info:");
        JTextField contactField = new JTextField(employee.getContactInfo() != null ? employee.getContactInfo() : "", 20);
        
        JLabel harborLabel = new JLabel("Harbor:");
        JComboBox<Harbor> harborCombo = new JComboBox<>();
        harborCombo.addItem(null); // Add empty option
        
        List<Harbor> harbors = harborDAO.getAllHarbors();
        for (Harbor harbor : harbors) {
            harborCombo.addItem(harbor);
            if (employee.getHarborId() != null && harbor.getHarborId() == employee.getHarborId()) {
                harborCombo.setSelectedItem(harbor);
            }
        }
        
        JLabel roleLabel = new JLabel("Role:");
        JComboBox<Role> roleCombo = new JComboBox<>();
        roleCombo.addItem(null); // Add empty option
        
        List<Role> roles = roleDAO.getAllRoles();
        for (Role role : roles) {
            roleCombo.addItem(role);
            if (employee.getRoleId() != null && role.getRoleId() == employee.getRoleId()) {
                roleCombo.setSelectedItem(role);
            }
        }
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(staffIdLabel);
        formPanel.add(staffIdField);
        formPanel.add(contactLabel);
        formPanel.add(contactField);
        formPanel.add(harborLabel);
        formPanel.add(harborCombo);
        formPanel.add(roleLabel);
        formPanel.add(roleCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (!ValidationUtil.validateRequired(nameField, "Name") ||
                    !ValidationUtil.validateRequired(staffIdField, "Staff ID")) {
                return;
            }
            
            if (!contactField.getText().trim().isEmpty()) {
                ValidationUtil.validateEmail(contactField, "Contact Info");
            }
            
            // Update employee object
            employee.setName(nameField.getText().trim());
            employee.setStaffId(staffIdField.getText().trim());
            employee.setContactInfo(contactField.getText().trim());
            
            Harbor selectedHarbor = (Harbor) harborCombo.getSelectedItem();
            if (selectedHarbor != null) {
                employee.setHarborId(selectedHarbor.getHarborId());
            } else {
                employee.setHarborId(null);
            }
            
            Role selectedRole = (Role) roleCombo.getSelectedItem();
            if (selectedRole != null) {
                employee.setRoleId(selectedRole.getRoleId());
            } else {
                employee.setRoleId(null);
            }
            
            // Save to database
            boolean success = employeeDAO.updateEmployee(employee);
            if (success) {
                JOptionPane.showMessageDialog(dialog,
                        "Employee updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to update employee. Please try again.",
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
     * Delete the selected employee
     */
    private void deleteEmployee() {
        // Get selected row
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get employee ID from selected row
        int employeeId = (int) employeeTable.getValueAt(employeeTable.convertRowIndexToModel(selectedRow), 0);
        
        // Confirm deletion
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this employee?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            // Delete from database
            boolean success = employeeDAO.deleteEmployee(employeeId);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Employee deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete employee. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}