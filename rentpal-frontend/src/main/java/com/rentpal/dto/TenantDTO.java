package com.rentpal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TenantDTO {
    private Long tenantId;
    private String name;
    private String email;
    private String phone;
    private String roomNumber;
    private double rentAmount;
    private String status;
    private double remainingRent;
    private String paymentStatus;
    private OwnerDTO owner; // Add owner field
    private Long ownerId;

    // Default constructor
    public TenantDTO() {}

    // Getters and setters
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public double getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(double rentAmount) {
        this.rentAmount = rentAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getRemainingRent() {
        return remainingRent;
    }

    public void setRemainingRent(double remainingRent) {
        this.remainingRent = remainingRent;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public OwnerDTO getOwner() {
        return owner;
    }

    public void setOwner(OwnerDTO owner) {
        this.owner = owner;
    }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}