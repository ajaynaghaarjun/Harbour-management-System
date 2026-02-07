package com.harbormgmt.model;

/**
 * Model class for CargoType entity
 */
public class CargoType {
    private int cargoTypeId;
    private String typeName;
    
    public CargoType() {
        // Default constructor
    }
    
    public CargoType(int cargoTypeId, String typeName) {
        this.cargoTypeId = cargoTypeId;
        this.typeName = typeName;
    }
    
    // Getters and setters
    public int getCargoTypeId() {
        return cargoTypeId;
    }
    
    public void setCargoTypeId(int cargoTypeId) {
        this.cargoTypeId = cargoTypeId;
    }
    
    public String getTypeName() {
        return typeName;
    }
    
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    
    @Override
    public String toString() {
        return typeName;
    }
}