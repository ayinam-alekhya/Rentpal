package com.rentpal.rentpal_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
public class Property {

    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long propertyId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private double rentAmount;

    @Column(nullable = false)
    private String location;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String status; // e.g. "Available", "Occupied", "Maintenance"

    // Many properties belong to one owner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    // One property can have many booking requests
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    // ---------------- Constructors ----------------
    public Property() {
        this.status = "Available";
    }

    public Property(String title, double rentAmount, String location, String description, Owner owner) {
        this();
        this.title = title;
        this.rentAmount = rentAmount;
        this.location = location;
        this.description = description;
        this.owner = owner;
    }

    // ---------------- Getters & Setters ----------------
    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getRentAmount() { return rentAmount; }
    public void setRentAmount(double rentAmount) { this.rentAmount = rentAmount; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Owner getOwner() { return owner; }
    public void setOwner(Owner owner) { this.owner = owner; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    // ---------------- Helper Methods ----------------
    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setProperty(this);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        booking.setProperty(null);
    }

    @Override
    public String toString() {
        return "Property{" +
                "propertyId=" + propertyId +
                ", title='" + title + '\'' +
                ", rentAmount=" + rentAmount +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}