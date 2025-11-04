package com.rentpal.rentpal_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(nullable = false)
    private String status; // e.g. "Pending", "Approved", "Rejected"

    @Column(nullable = false)
    private LocalDateTime requestDate;

    @Column
    private LocalDateTime responseDate;

    @Column(length = 500)
    private String message;

    // Many bookings belong to one tenant
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    // Many bookings are for one property
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    // ---------------- Constructors ----------------
    public Booking() {
        this.status = "Pending";
        this.requestDate = LocalDateTime.now();
    }

    public Booking(Tenant tenant, Property property, String message) {
        this();
        this.tenant = tenant;
        this.property = property;
        this.message = message;
    }

    // ---------------- Getters & Setters ----------------
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    public LocalDateTime getResponseDate() { return responseDate; }
    public void setResponseDate(LocalDateTime responseDate) { this.responseDate = responseDate; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

    // ---------------- Helper Methods ----------------
    public void approve() {
        this.status = "Approved";
        this.responseDate = LocalDateTime.now();
    }

    public void reject() {
        this.status = "Rejected";
        this.responseDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", status='" + status + '\'' +
                ", requestDate=" + requestDate +
                ", responseDate=" + responseDate +
                ", message='" + message + '\'' +
                '}';
    }
}