package com.rentpal.rentpal_backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "owners")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ownerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 15)
    private String phone;

    // Optional normalization improvement: prepare for expansion
    private String address;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tenant> tenants = new ArrayList<>();

    // One owner can have many properties
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Property> properties = new ArrayList<>();

    // ---------------- Constructors ----------------
    public Owner() {}

    public Owner(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    // ---------------- Getters & Setters ----------------
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<Tenant> getTenants() { return tenants; }
    public void setTenants(List<Tenant> tenants) { this.tenants = tenants; }

    public List<Property> getProperties() { return properties; }
    public void setProperties(List<Property> properties) { this.properties = properties; }

    // ---------------- Helper Methods ----------------
    public void addTenant(Tenant tenant) {
        tenants.add(tenant);
        tenant.setOwner(this);
    }

    public void removeTenant(Tenant tenant) {
        tenants.remove(tenant);
        tenant.setOwner(null);
    }

    public void addProperty(Property property) {
        properties.add(property);
        property.setOwner(this);
    }

    public void removeProperty(Property property) {
        properties.remove(property);
        property.setOwner(null);
    }

    @Override
    public String toString() {
        return "Owner{" +
                "ownerId=" + ownerId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", tenants=" + tenants.size() +
                '}';
    }
}