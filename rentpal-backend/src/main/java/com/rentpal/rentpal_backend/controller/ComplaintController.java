package com.rentpal.rentpal_backend.controller;

import com.rentpal.rentpal_backend.dto.ComplaintDTO;
import com.rentpal.rentpal_backend.dto.CreateComplaintRequest;
import com.rentpal.rentpal_backend.dto.UpdateComplaintStatusRequest;
import com.rentpal.rentpal_backend.model.Complaint;
import com.rentpal.rentpal_backend.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class ComplaintController {

    private static final Set<String> ALLOWED_STATUSES =
            Set.of("Open", "Pending", "In Progress", "Resolved");

    @Autowired
    private ComplaintService complaintService;

    // Add new complaint
    @PostMapping("/{tenantId}")
    public Complaint addComplaint(@PathVariable Long tenantId,
                                  @RequestBody CreateComplaintRequest complaintRequest) {
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

    // ---- Option A: Backward-compatible (query param) ----
    @PutMapping("/{complaintId}")
    public Complaint updateComplaintStatusParam(@PathVariable Long complaintId,
                                                @RequestParam String status) {
        validateStatus(status);
        Complaint updated = complaintService.updateComplaintStatus(complaintId, status);
        if (updated == null) throw new IllegalArgumentException("Complaint not found: " + complaintId);
        return updated;
    }

    // ---- Option B: Preferred (JSON body) ----
    @PutMapping("/{complaintId}/status")
    public Complaint updateComplaintStatusBody(@PathVariable Long complaintId,
                                               @RequestBody UpdateComplaintStatusRequest body) {
        if (body == null || body.getStatus() == null || body.getStatus().isBlank()) {
            throw new IllegalArgumentException("status is required");
        }
        validateStatus(body.getStatus());
        Complaint updated = complaintService.updateComplaintStatus(complaintId, body.getStatus());
        if (updated == null) throw new IllegalArgumentException("Complaint not found: " + complaintId);
        return updated;
    }

    private void validateStatus(String status) {
        if (!ALLOWED_STATUSES.contains(status)) {
            throw new IllegalArgumentException(
                "Invalid status. Allowed: " + String.join(", ", ALLOWED_STATUSES));
        }
    }
}
