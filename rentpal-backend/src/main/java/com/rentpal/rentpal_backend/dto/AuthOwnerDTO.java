package com.rentpal.rentpal_backend.dto;

public class AuthOwnerDTO {
    public Long ownerId;
    public String name;
    public String email;
    public String phone;
    public String address;
    public AuthOwnerDTO(Long id, String n, String e, String p, String a) {
        this.ownerId = id; this.name = n; this.email = e; this.phone = p; this.address = a;
    }
}


