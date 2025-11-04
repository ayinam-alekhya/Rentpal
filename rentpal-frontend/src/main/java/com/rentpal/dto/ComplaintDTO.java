package com.rentpal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ComplaintDTO {
    private Long complaintId;
    private String title;
    private String description;
    private String dateSubmitted;
    private String status;
    private Long ownerId; // Added ownerId field

    // Default constructor
    public ComplaintDTO() {}

    // Getters and setters
    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
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
}