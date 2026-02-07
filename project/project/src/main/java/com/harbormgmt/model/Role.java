package com.harbormgmt.model;

/**
 * Model class for Role entity
 */
public class Role {
    private int roleId;
    private String roleName;
    
    public Role() {
        // Default constructor
    }
    
    public Role(int roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }
    
    // Getters and setters
    public int getRoleId() {
        return roleId;
    }
    
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
    
    public String getRoleName() {
        return roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    @Override
    public String toString() {
        return roleName;
    }
}