package com.rentpal.rentpal_backend.dto;

import java.time.LocalDateTime;

public class ComplaintDTO {
    private Long complaintId;
    private String title;
    private String description;
    private String status;
    private LocalDateTime dateSubmitted;
    private String tenantName;
    private String ownerName;
    private String priority;  
    private Long ownerId;

    public ComplaintDTO(Long complaintId, String title, String description,
                        String status, LocalDateTime dateSubmitted,
                        String tenantName, String ownerName) {
        this.complaintId = complaintId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dateSubmitted = dateSubmitted;
        this.tenantName = tenantName;
        this.ownerName = ownerName;
    }

    // Getters
    public Long getComplaintId() { return complaintId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDateTime getDateSubmitted() { return dateSubmitted; }
    public String getTenantName() { return tenantName; }
    public String getOwnerName() { return ownerName; }
        public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
