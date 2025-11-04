package com.rentpal.dto;

import java.time.LocalDateTime;

public class BookingRequestDTO {
    private Long tenantId;
    private Long propertyId;
    private String message;
    private LocalDateTime requestDate;

    // Constructors
    public BookingRequestDTO() {
        this.requestDate = LocalDateTime.now();
    }

    public BookingRequestDTO(Long tenantId, Long propertyId, String message) {
        this();
        this.tenantId = tenantId;
        this.propertyId = propertyId;
        this.message = message;
    }

    // Getters and Setters
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }
}