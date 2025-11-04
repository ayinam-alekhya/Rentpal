package com.rentpal.rentpal_backend.dto;

public class TenantSummaryDTO {
    private Long tenantId;
    private String name;
    private String roomNumber;
    private double remainingRent;
    private String paymentStatus;

    public TenantSummaryDTO(Long tenantId, String name, String roomNumber,
                            double remainingRent, String paymentStatus) {
        this.tenantId = tenantId;
        this.name = name;
        this.roomNumber = roomNumber;
        this.remainingRent = remainingRent;
        this.paymentStatus = paymentStatus;
    }

    // Getters
    public Long getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getRoomNumber() { return roomNumber; }
    public double getRemainingRent() { return remainingRent; }
    public String getPaymentStatus() { return paymentStatus; }
}
