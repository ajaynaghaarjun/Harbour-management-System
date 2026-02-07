package com.harbormgmt.model;

/**
 * Model class for Employee entity
 */
public class Employee {
    private int employeeId;
    private Integer harborId;
    private String name;
    private String staffId;
    private String contactInfo;
    private Integer roleId;
    
    public Employee() {
        // Default constructor
    }
    
    public Employee(int employeeId, Integer harborId, String name, String staffId, String contactInfo, Integer roleId) {
        this.employeeId = employeeId;
        this.harborId = harborId;
        this.name = name;
        this.staffId = staffId;
        this.contactInfo = contactInfo;
        this.roleId = roleId;
    }
    
    // Getters and setters
    public int getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }
    
    public Integer getHarborId() {
        return harborId;
    }
    
    public void setHarborId(Integer harborId) {
        this.harborId = harborId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getStaffId() {
        return staffId;
    }
    
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
    
    public String getContactInfo() {
        return contactInfo;
    }
    
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    public Integer getRoleId() {
        return roleId;
    }
    
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}