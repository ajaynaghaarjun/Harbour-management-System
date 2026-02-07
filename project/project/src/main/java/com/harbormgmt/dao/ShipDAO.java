package com.harbormgmt.dao;

import com.harbormgmt.model.Ship;
import com.harbormgmt.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Ship entities
 */
public class ShipDAO {
    
    /**
     * Get all ships from the database
     * @return List of all Ship objects
     */
    public List<Ship> getAllShips() {
        List<Ship> ships = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM ships";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Ship ship = extractShipFromResultSet(rs);
                ships.add(ship);
            }
        } catch (SQLException e) {
            System.err.println("Error getting ships: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return ships;
    }
    
    /**
     * Get all ships for a specific harbor
     * @param harborId the ID of the harbor
     * @return List of Ship objects for the harbor
     */
    public List<Ship> getShipsByHarbor(int harborId) {
        List<Ship> ships = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM ships WHERE HarborID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, harborId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Ship ship = extractShipFromResultSet(rs);
                ships.add(ship);
            }
        } catch (SQLException e) {
            System.err.println("Error getting ships by harbor: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return ships;
    }
    
    /**
     * Get a ship by its ID
     * @param shipId the ID of the ship to get
     * @return the Ship object, or null if not found
     */
    public Ship getShipById(int shipId) {
        Ship ship = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM ships WHERE ShipID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, shipId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                ship = extractShipFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting ship by ID: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return ship;
    }
    
    /**
     * Add a new ship to the database
     * @param ship the Ship object to add
     * @return the ID of the new ship, or -1 if an error occurred
     */
    public int addShip(Ship ship) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int newShipId = -1;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "INSERT INTO ships (Name, ContactInfo, Capacity, HarborID, Status, MaxWeight) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, ship.getName());
            stmt.setString(2, ship.getContactInfo());
            stmt.setInt(3, ship.getCapacity());
            
            if (ship.getHarborId() > 0) {
                stmt.setInt(4, ship.getHarborId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            stmt.setString(5, ship.getStatus());
            
            if (ship.getMaxWeight() != null) {
                stmt.setBigDecimal(6, ship.getMaxWeight());
            } else {
                stmt.setNull(6, java.sql.Types.DECIMAL);
            }
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    newShipId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding ship: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return newShipId;
    }
    
    /**
     * Update an existing ship in the database
     * @param ship the Ship object to update
     * @return true if successful, false otherwise
     */
    public boolean updateShip(Ship ship) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "UPDATE ships SET Name = ?, ContactInfo = ?, Capacity = ?, HarborID = ?, Status = ?, MaxWeight = ? WHERE ShipID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, ship.getName());
            stmt.setString(2, ship.getContactInfo());
            stmt.setInt(3, ship.getCapacity());
            
            if (ship.getHarborId() > 0) {
                stmt.setInt(4, ship.getHarborId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            stmt.setString(5, ship.getStatus());
            
            if (ship.getMaxWeight() != null) {
                stmt.setBigDecimal(6, ship.getMaxWeight());
            } else {
                stmt.setNull(6, java.sql.Types.DECIMAL);
            }
            
            stmt.setInt(7, ship.getShipId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating ship: " + e.getMessage());
            return false;
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Delete a ship from the database
     * @param shipId the ID of the ship to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteShip(int shipId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "DELETE FROM ships WHERE ShipID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, shipId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting ship: " + e.getMessage());
            return false;
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Extract a Ship object from a ResultSet
     * @param rs the ResultSet containing ship data
     * @return the extracted Ship object
     * @throws SQLException if there is an error accessing the ResultSet
     */
    private Ship extractShipFromResultSet(ResultSet rs) throws SQLException {
        Ship ship = new Ship();
        ship.setShipId(rs.getInt("ShipID"));
        ship.setName(rs.getString("Name"));
        ship.setContactInfo(rs.getString("ContactInfo"));
        ship.setCapacity(rs.getInt("Capacity"));
        
        // Handle nullable HarborID
        int harborId = rs.getInt("HarborID");
        if (!rs.wasNull()) {
            ship.setHarborId(harborId);
        }
        
        ship.setStatus(rs.getString("Status"));
        ship.setMaxWeight(rs.getBigDecimal("MaxWeight"));
        
        return ship;
    }
}