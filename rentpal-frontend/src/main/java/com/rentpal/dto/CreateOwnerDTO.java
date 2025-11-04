package com.rentpal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOwnerDTO {
    private Long ownerId;
    private String name;
    private String email;
    private String phone;
    private String address;

    // Default constructor
    public CreateOwnerDTO() {}

    // Constructor from OwnerDTO
    public CreateOwnerDTO(OwnerDTO owner) {
        this.ownerId = owner.getOwnerId();
        this.name = owner.getName();
        this.email = owner.getEmail();
        this.phone = owner.getPhone();
        this.address = owner.getAddress();
    }

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
}