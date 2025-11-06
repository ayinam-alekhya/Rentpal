package com.rentpal.rentpal_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaintId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    private String status; // "Pending", "Resolved", "In Progress"

    @Column(nullable = false)
    private LocalDateTime dateSubmitted;

    private String priority;

    // 🔗 Each complaint belongs to one tenant
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonBackReference
    private Tenant tenant;

    // 🔗 Each complaint is visible to one owner (optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = true)
    @JsonBackReference
    private Owner owner;

    // ---------------- Constructors ----------------
    public Complaint() {
        this.status = "Pending";
        this.dateSubmitted = LocalDateTime.now();
    }

    public Complaint(String title, String description, Tenant tenant, Owner owner) {
        this();
        this.title = title;
        this.description = description;
        this.tenant = tenant;
        this.owner = owner;
    }

    // ---------------- Getters & Setters ----------------
    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDateSubmitted() { return dateSubmitted; }
    public void setDateSubmitted(LocalDateTime dateSubmitted) { this.dateSubmitted = dateSubmitted; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Owner getOwner() { return owner; }
    public void setOwner(Owner owner) { this.owner = owner; }

    // ---------------- Utility ----------------
    @Override
    public String toString() {
        return "Complaint{" +
                "complaintId=" + complaintId +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", dateSubmitted=" + dateSubmitted +
                '}';
    }
}