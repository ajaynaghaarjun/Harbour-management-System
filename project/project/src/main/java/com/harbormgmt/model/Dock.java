package com.harbormgmt.model;

/**
 * Model class for Dock entity
 */
public class Dock {
    private int dockId;
    private int length;
    private String dockNumber;
    private String status;
    private Integer locationId;
    
    public Dock() {
        // Default constructor
    }
    
    public Dock(int dockId, int length, String dockNumber, String status, Integer locationId) {
        this.dockId = dockId;
        this.length = length;
        this.dockNumber = dockNumber;
        this.status = status;
        this.locationId = locationId;
    }
    
    // Getters and setters
    public int getDockId() {
        return dockId;
    }
    
    public void setDockId(int dockId) {
        this.dockId = dockId;
    }
    
    public int getLength() {
        return length;
    }
    
    public void setLength(int length) {
        this.length = length;
    }
    
    public String getDockNumber() {
        return dockNumber;
    }
    
    public void setDockNumber(String dockNumber) {
        this.dockNumber = dockNumber;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getLocationId() {
        return locationId;
    }
    
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
    
    @Override
    public String toString() {
        return "Dock #" + dockNumber;
    }
}