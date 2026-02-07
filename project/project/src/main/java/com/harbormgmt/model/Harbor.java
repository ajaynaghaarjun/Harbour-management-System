package com.harbormgmt.model;

/**
 * Model class for Harbor entity
 */
public class Harbor {
    private int harborId;
    private String managerName;
    private String contactInfo;
    private int capacity;
    private String location;
    
    public Harbor() {
        // Default constructor
    }
    
    public Harbor(int harborId, String managerName, String contactInfo, int capacity, String location) {
        this.harborId = harborId;
        this.managerName = managerName;
        this.contactInfo = contactInfo;
        this.capacity = capacity;
        this.location = location;
    }
    
    // Getters and setters
    public int getHarborId() {
        return harborId;
    }
    
    public void setHarborId(int harborId) {
        this.harborId = harborId;
    }
    
    public String getManagerName() {
        return managerName;
    }
    
    public void setManagerName(String managerName) {
        this.managerName = managerName;
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
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    @Override
    public String toString() {
        return location;
    }
}