package com.rentpal.rentpal_backend.dto;

public class UpdateComplaintStatusRequest {
    private String status; // "Open", "In Progress", "Resolved"
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
