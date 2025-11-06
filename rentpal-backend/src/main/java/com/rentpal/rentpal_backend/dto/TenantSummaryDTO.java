package com.rentpal.rentpal_backend.dto;

public class TenantSummaryDTO {
    private Long tenantId;
    private String name;
    private String phone;
    private String roomNumber;
    private double remainingRent;
    private String paymentStatus;

    // ✅ Updated constructor includes phone
    public TenantSummaryDTO(Long tenantId, String name, String phone, String roomNumber,
                            double remainingRent, String paymentStatus) {
        this.tenantId = tenantId;
        this.name = name;
        this.phone = phone;
        this.roomNumber = roomNumber;
        this.remainingRent = remainingRent;
        this.paymentStatus = paymentStatus;
    }

    // Getters and setters
    public Long getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getRoomNumber() { return roomNumber; }
    public double getRemainingRent() { return remainingRent; }
    public String getPaymentStatus() { return paymentStatus; }

    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setRemainingRent(double remainingRent) { this.remainingRent = remainingRent; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
