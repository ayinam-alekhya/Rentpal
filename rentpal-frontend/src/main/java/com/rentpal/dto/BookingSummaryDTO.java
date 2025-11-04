package com.rentpal.dto;

import java.time.LocalDateTime;

public class BookingSummaryDTO {
    private Long bookingId;
    private String status;
    private LocalDateTime requestDate;
    private LocalDateTime responseDate;
    private String message;
    private Long tenantId;
    private String tenantName;
    private Long propertyId;
    private String propertyTitle;
    private double propertyRentAmount;
    private String propertyLocation;

    // Constructors
    public BookingSummaryDTO() {}

    public BookingSummaryDTO(Long bookingId, String status, LocalDateTime requestDate, LocalDateTime responseDate, String message, Long tenantId, String tenantName, Long propertyId, String propertyTitle, double propertyRentAmount, String propertyLocation) {
        this.bookingId = bookingId;
        this.status = status;
        this.requestDate = requestDate;
        this.responseDate = responseDate;
        this.message = message;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.propertyId = propertyId;
        this.propertyTitle = propertyTitle;
        this.propertyRentAmount = propertyRentAmount;
        this.propertyLocation = propertyLocation;
    }

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDateTime responseDate) {
        this.responseDate = responseDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
    }

    public double getPropertyRentAmount() {
        return propertyRentAmount;
    }

    public void setPropertyRentAmount(double propertyRentAmount) {
        this.propertyRentAmount = propertyRentAmount;
    }

    public String getPropertyLocation() {
        return propertyLocation;
    }

    public void setPropertyLocation(String propertyLocation) {
        this.propertyLocation = propertyLocation;
    }
}