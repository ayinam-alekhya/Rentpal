package com.rentpal.rentpal_backend.dto;

public class CreateComplaintRequest {
    private String title;
    private String description;
    private String dateSubmitted; // String representation to match frontend
    private String status;
    private String priority;
    private Long ownerId; // Added ownerId field to match frontend

    // Default constructor
    public CreateComplaintRequest() {}

    // Getters and setters
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

    public String getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(String dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    // Getter and setter for ownerId
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}