package com.harbormgmt.model;

import java.math.BigDecimal;

/**
 * Model class for Cargo entity
 */
public class Cargo {
    private int cargoId;
    private int shipId;
    private BigDecimal weight;
    private Integer cargoTypeId;
    
    public Cargo() {
        // Default constructor
    }
    
    public Cargo(int cargoId, int shipId, BigDecimal weight, Integer cargoTypeId) {
        this.cargoId = cargoId;
        this.shipId = shipId;
        this.weight = weight;
        this.cargoTypeId = cargoTypeId;
    }
    
    // Getters and setters
    public int getCargoId() {
        return cargoId;
    }
    
    public void setCargoId(int cargoId) {
        this.cargoId = cargoId;
    }
    
    public int getShipId() {
        return shipId;
    }
    
    public void setShipId(int shipId) {
        this.shipId = shipId;
    }
    
    public BigDecimal getWeight() {
        return weight;
    }
    
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    
    public Integer getCargoTypeId() {
        return cargoTypeId;
    }
    
    public void setCargoTypeId(Integer cargoTypeId) {
        this.cargoTypeId = cargoTypeId;
    }
}