package com.rentpal.rentpal_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true, unique = false)
    private String email;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(nullable = false)
    private String roomNumber;

    @Column(nullable = false)
    private double rentAmount;

    @Column(nullable = false)
    private String status; // e.g. "Active", "Inactive"

    // ✅ Many tenants belong to one owner
    // For signup, owner can be null initially and assigned later
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = true)
    private Owner owner;

    // ✅ One tenant can have many payments
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    // ✅ One tenant can have many complaints
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Complaint> complaints = new ArrayList<>();

    // One tenant can have many bookings
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    @Column(nullable = false)
    private double remainingRent;

    @Column(nullable = false)
    private String paymentStatus; // "Unpaid", "Partial", "Paid"

    // ---------------- Constructors ----------------
    public Tenant() {
        this.remainingRent = 0.0;
        this.paymentStatus = "Unpaid";
        this.status = "Active";
    }

    public Tenant(String name, String email, String phone, String roomNumber,
                  double rentAmount, Owner owner) {
        this();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.roomNumber = roomNumber;
        this.rentAmount = rentAmount;
        this.owner = owner;
    }

    // ---------------- Getters & Setters ----------------
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

    public double getRentAmount() { return rentAmount; }
    public void setRentAmount(double rentAmount) { this.rentAmount = rentAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Owner getOwner() { return owner; }
    public void setOwner(Owner owner) { this.owner = owner; }

    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }

    public List<Complaint> getComplaints() { return complaints; }
    public void setComplaints(List<Complaint> complaints) { this.complaints = complaints; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public double getRemainingRent() { return remainingRent; }
    public void setRemainingRent(double remainingRent) { this.remainingRent = remainingRent; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    // ---------------- Helper Methods ----------------
    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setTenant(this);
    }

    public void addComplaint(Complaint complaint) {
        complaints.add(complaint);
        complaint.setTenant(this);
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setTenant(this);
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "tenantId=" + tenantId +
                ", name='" + name + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", remainingRent=" + remainingRent +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}