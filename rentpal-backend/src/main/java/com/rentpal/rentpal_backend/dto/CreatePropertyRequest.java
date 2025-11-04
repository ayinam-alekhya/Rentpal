package com.rentpal.rentpal_backend.dto;

public class CreatePropertyRequest {
    private Long ownerId;
    private String title;
    private String description;
    private double rentAmount;
    private String location;

    // Constructors
    public CreatePropertyRequest() {}

    public CreatePropertyRequest(Long ownerId, String title, String description, double rentAmount, String location) {
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.rentAmount = rentAmount;
        this.location = location;
    }

    // Getters and Setters
    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}