package com.rentpal.rentpal_backend.dto;

public class TenantDTO {
    private Long tenantId;
    private String name;
    private String email;
    private String phone;
    private String roomNumber;
    private Double rentAmount;
    private String status;
    private Double remainingRent;
    private String paymentStatus;

    // 👇 crucial for the complaint/owner selection
    private Long ownerId;
    private OwnerDTO owner; // tiny embedded owner (id/name/email)

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Double getRentAmount() { return rentAmount; }
    public void setRentAmount(Double rentAmount) { this.rentAmount = rentAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getRemainingRent() { return remainingRent; }
    public void setRemainingRent(Double remainingRent) { this.remainingRent = remainingRent; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public OwnerDTO getOwner() { return owner; }
    public void setOwner(OwnerDTO owner) { this.owner = owner; }
}
