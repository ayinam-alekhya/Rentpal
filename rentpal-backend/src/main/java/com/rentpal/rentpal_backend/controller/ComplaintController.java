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

    @PostMapping("/{tenantId}")
    public Complaint addComplaint(@PathVariable Long tenantId, @RequestBody ComplaintDTO dto) {
        System.out.println("Received complaint for tenantId=" + tenantId + " → ownerId=" + dto.getOwnerId());
        return complaintService.addComplaint(tenantId, dto);
    }

    // Get complaints by owner, with optional ?status=ALL|PENDING|IN_PROGRESS|RESOLVED
    @GetMapping("/owner/{ownerId}")
    public List<ComplaintDTO> getComplaintsByOwner(
            @PathVariable Long ownerId,
            @RequestParam(defaultValue = "ALL") String status) {
        return complaintService.getComplaintsByOwner(ownerId, status);
    }

    // Get complaints by tenant, with optional ?status=ALL|PENDING|IN_PROGRESS|RESOLVED
    @GetMapping("/tenant/{tenantId}")
    public List<ComplaintDTO> getComplaintsByTenant(
            @PathVariable Long tenantId,
            @RequestParam(defaultValue = "ALL") String status) {
        return complaintService.getComplaintsByTenant(tenantId, status);
    }

    // Update complaint status (prefer a dedicated /status path)
    @PutMapping("/{complaintId}/status")
    public Complaint updateComplaintStatus(
            @PathVariable Long complaintId,
            @RequestParam String status) {
        return complaintService.updateComplaintStatus(complaintId, status);
    }

}