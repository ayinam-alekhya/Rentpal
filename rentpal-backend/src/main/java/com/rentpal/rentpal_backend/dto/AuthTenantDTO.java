package com.rentpal.rentpal_backend.dto;

public class AuthTenantDTO {
    public Long tenantId;
    public String name;
    public String email;
    public String roomNumber;
    public Long ownerId;
    public AuthTenantDTO(Long id, String n, String e, String room, Long ownerId) {
        this.tenantId = id; this.name = n; this.email = e; this.roomNumber = room; this.ownerId = ownerId;
    }
}