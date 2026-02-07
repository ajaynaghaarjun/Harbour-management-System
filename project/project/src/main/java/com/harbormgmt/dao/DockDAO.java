package com.harbormgmt.dao;

import com.harbormgmt.model.Dock;
import com.harbormgmt.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Dock entities
 */
public class DockDAO {
    
    /**
     * Get all docks from the database
     * @return List of all Dock objects
     */
    public List<Dock> getAllDocks() {
        List<Dock> docks = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM dock";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Dock dock = extractDockFromResultSet(rs);
                docks.add(dock);
            }
        } catch (SQLException e) {
            System.err.println("Error getting docks: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return docks;
    }
    
    /**
     * Get a dock by its ID
     * @param dockId the ID of the dock to get
     * @return the Dock object, or null if not found
     */
    public Dock getDockById(int dockId) {
        Dock dock = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM dock WHERE DockID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, dockId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                dock = extractDockFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting dock by ID: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return dock;
    }
    
    /**
     * Get all available docks
     * @return List of available Dock objects
     */
    public List<Dock> getAvailableDocks() {
        List<Dock> docks = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM dock WHERE Status = 'Available'";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Dock dock = extractDockFromResultSet(rs);
                docks.add(dock);
            }
        } catch (SQLException e) {
            System.err.println("Error getting available docks: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return docks;
    }
    
    /**
     * Add a new dock to the database
     * @param dock the Dock object to add
     * @return the ID of the new dock, or -1 if an error occurred
     */
    public int addDock(Dock dock) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int newDockId = -1;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "INSERT INTO dock (Length, DockNumber, Status, LocationID) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, dock.getLength());
            stmt.setString(2, dock.getDockNumber());
            stmt.setString(3, dock.getStatus());
            
            if (dock.getLocationId() != null) {
                stmt.setInt(4, dock.getLocationId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    newDockId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding dock: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return newDockId;
    }
    
    /**
     * Update an existing dock in the database
     * @param dock the Dock object to update
     * @return true if successful, false otherwise
     */
    public boolean updateDock(Dock dock) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "UPDATE dock SET Length = ?, DockNumber = ?, Status = ?, LocationID = ? WHERE DockID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, dock.getLength());
            stmt.setString(2, dock.getDockNumber());
            stmt.setString(3, dock.getStatus());
            
            if (dock.getLocationId() != null) {
                stmt.setInt(4, dock.getLocationId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(5, dock.getDockId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating dock: " + e.getMessage());
            return false;
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Delete a dock from the database
     * @param dockId the ID of the dock to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteDock(int dockId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "DELETE FROM dock WHERE DockID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, dockId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting dock: " + e.getMessage());
            return false;
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Extract a Dock object from a ResultSet
     * @param rs the ResultSet containing dock data
     * @return the extracted Dock object
     * @throws SQLException if there is an error accessing the ResultSet
     */
    private Dock extractDockFromResultSet(ResultSet rs) throws SQLException {
        Dock dock = new Dock();
        dock.setDockId(rs.getInt("DockID"));
        dock.setLength(rs.getInt("Length"));
        dock.setDockNumber(rs.getString("DockNumber"));
        dock.setStatus(rs.getString("Status"));
        
        int locationId = rs.getInt("LocationID");
        if (!rs.wasNull()) {
            dock.setLocationId(locationId);
        }
        
        return dock;
    }
}