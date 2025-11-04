package com.rentpal.dto;

public class PropertySummaryDTO {
    private Long propertyId;
    private String title;
    private double rentAmount;
    private String location;
    private String description;
    private String status;
    private Long ownerId;
    private String ownerName;

    // Constructors
    public PropertySummaryDTO() {}

    public PropertySummaryDTO(Long propertyId, String title, double rentAmount, String location, String description, String status, Long ownerId, String ownerName) {
        this.propertyId = propertyId;
        this.title = title;
        this.rentAmount = rentAmount;
        this.location = location;
        this.description = description;
        this.status = status;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
    }

    // Getters and Setters
    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(double rentAmount) {
        this.rentAmount = rentAmount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}