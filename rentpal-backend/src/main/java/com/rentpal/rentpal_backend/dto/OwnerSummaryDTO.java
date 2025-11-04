package com.rentpal.rentpal_backend.dto;

public class OwnerSummaryDTO {
    private Long ownerId;
    private String name;
    private String email;
    private String phone;

    public OwnerSummaryDTO(Long ownerId, String name, String email, String phone) {
        this.ownerId = ownerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getters
    public Long getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}
