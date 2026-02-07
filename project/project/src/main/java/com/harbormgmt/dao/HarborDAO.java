package com.harbormgmt.dao;

import com.harbormgmt.model.Harbor;
import com.harbormgmt.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Harbor entities
 */
public class HarborDAO {
    
    /**
     * Get all harbors from the database
     * @return List of all Harbor objects
     */
    public List<Harbor> getAllHarbors() {
        List<Harbor> harbors = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM harbor";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Harbor harbor = new Harbor();
                harbor.setHarborId(rs.getInt("HarborID"));
                harbor.setManagerName(rs.getString("ManagerName"));
                harbor.setContactInfo(rs.getString("ContactInfo"));
                harbor.setCapacity(rs.getInt("Capacity"));
                harbor.setLocation(rs.getString("Location"));
                
                harbors.add(harbor);
            }
        } catch (SQLException e) {
            System.err.println("Error getting harbors: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return harbors;
    }
    
    /**
     * Get a harbor by its ID
     * @param harborId the ID of the harbor to get
     * @return the Harbor object, or null if not found
     */
    public Harbor getHarborById(int harborId) {
        Harbor harbor = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM harbor WHERE HarborID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, harborId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                harbor = new Harbor();
                harbor.setHarborId(rs.getInt("HarborID"));
                harbor.setManagerName(rs.getString("ManagerName"));
                harbor.setContactInfo(rs.getString("ContactInfo"));
                harbor.setCapacity(rs.getInt("Capacity"));
                harbor.setLocation(rs.getString("Location"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting harbor by ID: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return harbor;
    }
    
    /**
     * Add a new harbor to the database
     * @param harbor the Harbor object to add
     * @return the ID of the new harbor, or -1 if an error occurred
     */
    public int addHarbor(Harbor harbor) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int newHarborId = -1;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "INSERT INTO harbor (ManagerName, ContactInfo, Capacity, Location) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, harbor.getManagerName());
            stmt.setString(2, harbor.getContactInfo());
            stmt.setInt(3, harbor.getCapacity());
            stmt.setString(4, harbor.getLocation());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    newHarborId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding harbor: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return newHarborId;
    }
    
    /**
     * Update an existing harbor in the database
     * @param harbor the Harbor object to update
     * @return true if successful, false otherwise
     */
    public boolean updateHarbor(Harbor harbor) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "UPDATE harbor SET ManagerName = ?, ContactInfo = ?, Capacity = ?, Location = ? WHERE HarborID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, harbor.getManagerName());
            stmt.setString(2, harbor.getContactInfo());
            stmt.setInt(3, harbor.getCapacity());
            stmt.setString(4, harbor.getLocation());
            stmt.setInt(5, harbor.getHarborId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating harbor: " + e.getMessage());
            return false;
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Delete a harbor from the database
     * @param harborId the ID of the harbor to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteHarbor(int harborId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "DELETE FROM harbor WHERE HarborID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, harborId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting harbor: " + e.getMessage());
            return false;
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
}