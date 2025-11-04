package com.rentpal.rentpal_backend.controller;

import com.rentpal.rentpal_backend.dto.ComplaintDTO;
import com.rentpal.rentpal_backend.dto.CreateComplaintRequest;
import com.rentpal.rentpal_backend.model.Complaint;
import com.rentpal.rentpal_backend.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    // Add new complaint
    @PostMapping("/{tenantId}")
    public Complaint addComplaint(@PathVariable Long tenantId, @RequestBody CreateComplaintRequest complaintRequest) {
        return complaintService.addComplaint(tenantId, complaintRequest);
    }

    // Get complaints by owner
    @GetMapping("/owner/{ownerId}")
    public List<ComplaintDTO> getComplaintsByOwner(@PathVariable Long ownerId) {
        return complaintService.getComplaintsByOwner(ownerId);
    }

    // Get complaints by tenant
    @GetMapping("/tenant/{tenantId}")
    public List<ComplaintDTO> getComplaintsByTenant(@PathVariable Long tenantId) {
        return complaintService.getComplaintsByTenant(tenantId);
    }

    // Update complaint status (ex: mark resolved)
    @PutMapping("/{complaintId}")
    public Complaint updateComplaintStatus(@PathVariable Long complaintId, @RequestParam String status) {
        return complaintService.updateComplaintStatus(complaintId, status);
    }
}