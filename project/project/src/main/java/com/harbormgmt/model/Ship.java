package com.harbormgmt.model;

import java.math.BigDecimal;

/**
 * Model class for Ship entity
 */
public class Ship {
    private int shipId;
    private String name;
    private String contactInfo;
    private int capacity;
    private int harborId;
    private String status;
    private BigDecimal maxWeight;
    
    public Ship() {
        // Default constructor
    }
    
    public Ship(int shipId, String name, String contactInfo, int capacity, int harborId, String status, BigDecimal maxWeight) {
        this.shipId = shipId;
        this.name = name;
        this.contactInfo = contactInfo;
        this.capacity = capacity;
        this.harborId = harborId;
        this.status = status;
        this.maxWeight = maxWeight;
    }
    
    // Getters and setters
    public int getShipId() {
        return shipId;
    }
    
    public void setShipId(int shipId) {
        this.shipId = shipId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getContactInfo() {
        return contactInfo;
    }
    
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public int getHarborId() {
        return harborId;
    }
    
    public void setHarborId(int harborId) {
        this.harborId = harborId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public BigDecimal getMaxWeight() {
        return maxWeight;
    }
    
    public void setMaxWeight(BigDecimal maxWeight) {
        this.maxWeight = maxWeight;
    }
    
    @Override
    public String toString() {
        return name;
    }
}