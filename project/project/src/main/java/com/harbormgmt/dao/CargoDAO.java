package com.harbormgmt.dao;

import com.harbormgmt.model.Cargo;
import com.harbormgmt.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Cargo entities
 */
public class CargoDAO {
    
    /**
     * Get all cargo items from the database
     * @return List of all Cargo objects
     */
    public List<Cargo> getAllCargo() {
        List<Cargo> cargoList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM cargo";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Cargo cargo = extractCargoFromResultSet(rs);
                cargoList.add(cargo);
            }
        } catch (SQLException e) {
            System.err.println("Error getting cargo: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return cargoList;
    }
    
    /**
     * Get all cargo items for a specific ship
     * @param shipId the ID of the ship
     * @return List of Cargo objects for the ship
     */
    public List<Cargo> getCargoByShip(int shipId) {
        List<Cargo> cargoList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM cargo WHERE ShipID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, shipId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Cargo cargo = extractCargoFromResultSet(rs);
                cargoList.add(cargo);
            }
        } catch (SQLException e) {
            System.err.println("Error getting cargo by ship: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return cargoList;
    }
    
    /**
     * Get a cargo item by its ID
     * @param cargoId the ID of the cargo to get
     * @return the Cargo object, or null if not found
     */
    public Cargo getCargoById(int cargoId) {
        Cargo cargo = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "SELECT * FROM cargo WHERE CargoID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cargoId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                cargo = extractCargoFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting cargo by ID: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return cargo;
    }
    
    /**
     * Add a new cargo item to the database
     * @param cargo the Cargo object to add
     * @return the ID of the new cargo, or -1 if an error occurred
     */
    public int addCargo(Cargo cargo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int newCargoId = -1;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "INSERT INTO cargo (ShipID, Weight, CargoTypeID) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, cargo.getShipId());
            stmt.setBigDecimal(2, cargo.getWeight());
            
            if (cargo.getCargoTypeId() != null) {
                stmt.setInt(3, cargo.getCargoTypeId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    newCargoId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding cargo: " + e.getMessage());
        } finally {
            DatabaseManager.closeResources(conn, stmt, rs);
        }
        
        return newCargoId;
    }
    
    /**
     * Update an existing cargo item in the database
     * @param cargo the Cargo object to update
     * @return true if successful, false otherwise
     */
    public boolean updateCargo(Cargo cargo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "UPDATE cargo SET ShipID = ?, Weight = ?, CargoTypeID = ? WHERE CargoID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cargo.getShipId());
            stmt.setBigDecimal(2, cargo.getWeight());
            
            if (cargo.getCargoTypeId() != null) {
                stmt.setInt(3, cargo.getCargoTypeId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(4, cargo.getCargoId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating cargo: " + e.getMessage());
            return false;
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Delete a cargo item from the database
     * @param cargoId the ID of the cargo to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteCargo(int cargoId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseManager.getConnection();
            String sql = "DELETE FROM cargo WHERE CargoID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cargoId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting cargo: " + e.getMessage());
            return false;
        } finally {
            DatabaseManager.closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Extract a Cargo object from a ResultSet
     * @param rs the ResultSet containing cargo data
     * @return the extracted Cargo object
     * @throws SQLException if there is an error accessing the ResultSet
     */
    private Cargo extractCargoFromResultSet(ResultSet rs) throws SQLException {
        Cargo cargo = new Cargo();
        cargo.setCargoId(rs.getInt("CargoID"));
        cargo.setShipId(rs.getInt("ShipID"));
        cargo.setWeight(rs.getBigDecimal("Weight"));
        
        int cargoTypeId = rs.getInt("CargoTypeID");
        if (!rs.wasNull()) {
            cargo.setCargoTypeId(cargoTypeId);
        }
        
        return cargo;
    }
}