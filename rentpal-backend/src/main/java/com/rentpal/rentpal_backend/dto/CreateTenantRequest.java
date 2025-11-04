package com.rentpal.rentpal_backend.dto;

public class CreateTenantRequest {
    private String name;
    private String email;
    private String phone;
    private String roomNumber;
    private double rentAmount;
    private Long ownerId;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public double getRentAmount() { return rentAmount; }
    public void setRentAmount(double rentAmount) { this.rentAmount = rentAmount; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}