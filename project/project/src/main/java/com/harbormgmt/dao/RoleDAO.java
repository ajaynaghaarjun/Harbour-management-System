package com.harbormgmt.dao;

import com.harbormgmt.model.Role;
import com.harbormgmt.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Role entities
 */
public class RoleDAO {
    
    /**
     * Get all roles from the database
     * @return List of all Role objects
     */
    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM roles";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Role role = new Role();
                role.setRoleId(rs.getInt("RoleID"));
                role.setRoleName(rs.getString("RoleName"));
                
                roles.add(role);
            }
        } catch (SQLException e) {
            System.err.println("Error getting roles: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return roles;
    }
    
    /**
     * Get a role by its ID
     * @param roleId the ID of the role to get
     * @return the Role object, or null if not found
     */
    public Role getRoleById(int roleId) {
        Role role = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM roles WHERE RoleID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, roleId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                role = new Role();
                role.setRoleId(rs.getInt("RoleID"));
                role.setRoleName(rs.getString("RoleName"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting role by ID: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return role;
    }
    
    /**
     * Add a new role to the database
     * @param role the Role object to add
     * @return the ID of the new role, or -1 if an error occurred
     */
    public int addRole(Role role) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int newRoleId = -1;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "INSERT INTO roles (RoleName) VALUES (?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, role.getRoleName());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    newRoleId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding role: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return newRoleId;
    }
    
    /**
     * Update an existing role in the database
     * @param role the Role object to update
     * @return true if successful, false otherwise
     */
    public boolean updateRole(Role role) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "UPDATE roles SET RoleName = ? WHERE RoleID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, role.getRoleName());
            stmt.setInt(2, role.getRoleId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating role: " + e.getMessage());
            return false;
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Delete a role from the database
     * @param roleId the ID of the role to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteRole(int roleId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "DELETE FROM roles WHERE RoleID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, roleId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting role: " + e.getMessage());
            return false;
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
}