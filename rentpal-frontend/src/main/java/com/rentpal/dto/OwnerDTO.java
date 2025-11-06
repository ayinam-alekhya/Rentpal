package com.rentpal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OwnerDTO {
    private Long ownerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private List<?> tenants; // Add tenants field to match backend response

    // Default constructor
    public OwnerDTO() {}

    // Getters and setters
    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<?> getTenants() {
        return tenants;
    }

    public void setTenants(List<?> tenants) {
        this.tenants = tenants;
    }

    @Override
    public String toString() {
        // Display both name and email (you can change formatting as you like)
        return name != null
                ? name 
                : "Unknown Owner";
    }

}