package com.harbormgmt.dao;

import com.harbormgmt.model.Employee;
import com.harbormgmt.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Employee entities
 */
public class EmployeeDAO {
    
    /**
     * Get all employees from the database
     * @return List of all Employee objects
     */
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM employees";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Employee employee = extractEmployeeFromResultSet(rs);
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error getting employees: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return employees;
    }
    
    /**
     * Get all employees for a specific harbor
     * @param harborId the ID of the harbor
     * @return List of Employee objects for the harbor
     */
    public List<Employee> getEmployeesByHarbor(int harborId) {
        List<Employee> employees = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM employees WHERE HarborID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, harborId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Employee employee = extractEmployeeFromResultSet(rs);
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error getting employees by harbor: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return employees;
    }
    
    /**
     * Get an employee by their ID
     * @param employeeId the ID of the employee to get
     * @return the Employee object, or null if not found
     */
    public Employee getEmployeeById(int employeeId) {
        Employee employee = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM employees WHERE EmployeeID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                employee = extractEmployeeFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting employee by ID: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return employee;
    }
    
    /**
     * Add a new employee to the database
     * @param employee the Employee object to add
     * @return the ID of the new employee, or -1 if an error occurred
     */
    public int addEmployee(Employee employee) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int newEmployeeId = -1;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "INSERT INTO employees (HarborID, Name, StaffID, ContactInfo, RoleID) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            if (employee.getHarborId() != null) {
                stmt.setInt(1, employee.getHarborId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            
            stmt.setString(2, employee.getName());
            stmt.setString(3, employee.getStaffId());
            stmt.setString(4, employee.getContactInfo());
            
            if (employee.getRoleId() != null) {
                stmt.setInt(5, employee.getRoleId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    newEmployeeId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return newEmployeeId;
    }
    
    /**
     * Update an existing employee in the database
     * @param employee the Employee object to update
     * @return true if successful, false otherwise
     */
    public boolean updateEmployee(Employee employee) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "UPDATE employees SET HarborID = ?, Name = ?, StaffID = ?, ContactInfo = ?, RoleID = ? WHERE EmployeeID = ?";
            stmt = conn.prepareStatement(sql);
            
            if (employee.getHarborId() != null) {
                stmt.setInt(1, employee.getHarborId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            
            stmt.setString(2, employee.getName());
            stmt.setString(3, employee.getStaffId());
            stmt.setString(4, employee.getContactInfo());
            
            if (employee.getRoleId() != null) {
                stmt.setInt(5, employee.getRoleId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(6, employee.getEmployeeId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            return false;
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Delete an employee from the database
     * @param employeeId the ID of the employee to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteEmployee(int employeeId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "DELETE FROM employees WHERE EmployeeID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            return false;
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Extract an Employee object from a ResultSet
     * @param rs the ResultSet containing employee data
     * @return the extracted Employee object
     * @throws SQLException if there is an error accessing the ResultSet
     */
    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("EmployeeID"));
        
        int harborId = rs.getInt("HarborID");
        if (!rs.wasNull()) {
            employee.setHarborId(harborId);
        }
        
        employee.setName(rs.getString("Name"));
        employee.setStaffId(rs.getString("StaffID"));
        employee.setContactInfo(rs.getString("ContactInfo"));
        
        int roleId = rs.getInt("RoleID");
        if (!rs.wasNull()) {
            employee.setRoleId(roleId);
        }
        
        return employee;
    }
}