package com.rentpal.rentpal_backend.service;

import com.rentpal.rentpal_backend.dto.ComplaintDTO;
import com.rentpal.rentpal_backend.dto.CreateComplaintRequest;
import com.rentpal.rentpal_backend.model.Complaint;
import com.rentpal.rentpal_backend.model.Owner;
import com.rentpal.rentpal_backend.model.Tenant;
import com.rentpal.rentpal_backend.repository.ComplaintRepository;
import com.rentpal.rentpal_backend.repository.OwnerRepository;
import com.rentpal.rentpal_backend.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    // ✅ 1. Add new complaint (original method)
    public Complaint addComplaint(Long tenantId, Complaint complaint) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tenant ID: " + tenantId));

        // Optional: auto-fetch owner from tenant if needed
        Owner owner = tenant.getOwner();

        complaint.setTenant(tenant);
        complaint.setOwner(owner);
        complaint.setDateSubmitted(LocalDateTime.now());
        complaint.setStatus("Pending");

        return complaintRepository.save(complaint);
    }

    // ✅ 1b. Add new complaint (new method for DTO)
    public Complaint addComplaint(Long tenantId, CreateComplaintRequest complaintRequest) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tenant ID: " + tenantId));

        // Determine owner - either from request or from tenant relationship
        Owner owner = null;
        if (complaintRequest.getOwnerId() != null) {
            // Use specified owner ID from request
            owner = ownerRepository.findById(complaintRequest.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid owner ID: " + complaintRequest.getOwnerId()));
        } else {
            // Fallback to tenant's owner
            owner = tenant.getOwner();
        }

        Complaint complaint = new Complaint();
        complaint.setTitle(complaintRequest.getTitle());
        complaint.setDescription(complaintRequest.getDescription());
        complaint.setTenant(tenant);
        complaint.setOwner(owner);
        complaint.setStatus(complaintRequest.getStatus() != null ? complaintRequest.getStatus() : "Pending");
        
        // Convert string date to LocalDateTime if provided, otherwise use current time
        if (complaintRequest.getDateSubmitted() != null && !complaintRequest.getDateSubmitted().isEmpty()) {
            try {
                // Try to parse as LocalDate first (from frontend)
                LocalDate localDate = LocalDate.parse(complaintRequest.getDateSubmitted());
                complaint.setDateSubmitted(localDate.atStartOfDay());
            } catch (DateTimeParseException e) {
                try {
                    // Try to parse as LocalDateTime (ISO format)
                    complaint.setDateSubmitted(LocalDateTime.parse(complaintRequest.getDateSubmitted()));
                } catch (DateTimeParseException ex) {
                    // Default to current time if parsing fails
                    complaint.setDateSubmitted(LocalDateTime.now());
                }
            }
        } else {
            complaint.setDateSubmitted(LocalDateTime.now());
        }

        return complaintRepository.save(complaint);
    }

    // ✅ 2. Get all complaints (Admin/General)
    public List<ComplaintDTO> getAllComplaints() {
        return complaintRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ 3. Get complaints by Owner ID
    public List<ComplaintDTO> getComplaintsByOwner(Long ownerId) {
        return complaintRepository.findByOwner_OwnerId(ownerId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ 4. Get complaints by Tenant ID
    public List<ComplaintDTO> getComplaintsByTenant(Long tenantId) {
        return complaintRepository.findByTenant_TenantId(tenantId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ 5. Update complaint status
    public Complaint updateComplaintStatus(Long complaintId, String newStatus) {
        Complaint complaint = complaintRepository.findById(complaintId).orElse(null);
        if (complaint != null) {
            complaint.setStatus(newStatus);
            return complaintRepository.save(complaint);
        }
        return null;
    }

    // ✅ 6. Convert to DTO helper
    private ComplaintDTO toDTO(Complaint complaint) {
        return new ComplaintDTO(
                complaint.getComplaintId(),
                complaint.getTitle(),
                complaint.getDescription(),
                complaint.getStatus(),
                complaint.getDateSubmitted(),
                complaint.getTenant() != null ? complaint.getTenant().getName() : null,
                complaint.getOwner() != null ? complaint.getOwner().getName() : null
        );
    }

    // ✅ 7. Delete complaint
    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }
}