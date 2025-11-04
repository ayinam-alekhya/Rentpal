package com.rentpal.rentpal_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonBackReference
    private Tenant tenant;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(nullable = false, length = 20)
    private String status; // "Full", "Partial", "Pending"

    @Column(nullable = false, length = 20)
    private String modeOfPayment; // "Cash", "UPI", "Bank Transfer"

    // Optional normalization field (future proofing)
    private String transactionId;

    // ---------------- Constructors ----------------
    public Payment() {
        this.paymentDate = LocalDateTime.now();
        this.status = "Pending";
    }

    public Payment(Tenant tenant, double amount, String modeOfPayment, String status) {
        this();
        this.tenant = tenant;
        this.amount = amount;
        this.modeOfPayment = modeOfPayment;
        this.status = status;
    }

    // ---------------- Getters & Setters ----------------
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getModeOfPayment() { return modeOfPayment; }
    public void setModeOfPayment(String modeOfPayment) { this.modeOfPayment = modeOfPayment; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", status='" + status + '\'' +
                ", modeOfPayment='" + modeOfPayment + '\'' +
                '}';
    }
}