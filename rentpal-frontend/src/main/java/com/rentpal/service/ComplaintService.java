package com.rentpal.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentpal.dto.ComplaintDTO;
import com.rentpal.utils.ApiUtil;

import java.util.List;

public class ComplaintService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // --- Lists ---
    public List<ComplaintDTO> getComplaintsByOwner(Long ownerId) throws Exception {
        String json = ApiUtil.get("/complaints/owner/" + ownerId);
        return MAPPER.readValue(json, new TypeReference<List<ComplaintDTO>>() {});
    }

    public List<ComplaintDTO> getComplaintsByTenant(Long tenantId) throws Exception {
        String json = ApiUtil.get("/complaints/tenant/" + tenantId);
        return MAPPER.readValue(json, new TypeReference<List<ComplaintDTO>>() {});
    }

    // --- Create complaint (backend expects categoryId + priority) ---
    public ComplaintDTO addComplaint(Long tenantId,
                                     String title,
                                     String description,
                                     Long categoryId,
                                     String priority) throws Exception {
        CreateComplaintRequest req = new CreateComplaintRequest();
        req.setTitle(title);
        req.setDescription(description);
        req.setCategoryId(categoryId);     // required by backend
        req.setPriority(priority);         // "LOW" | "MEDIUM" | "HIGH"

        // Do NOT set ownerId or dateSubmitted here; server should infer/set them
        String jsonInput  = MAPPER.writeValueAsString(req);
        String jsonOutput = ApiUtil.post("/complaints/" + tenantId, jsonInput);
        return MAPPER.readValue(jsonOutput, ComplaintDTO.class);
    }

    // --- Update status (owner changes) ---
    public ComplaintDTO updateComplaintStatus(Long complaintId, String status) throws Exception {
        String safe = ApiUtil.encode(status == null ? "" : status);
        String jsonOutput = ApiUtil.put("/complaints/" + complaintId + "?status=" + safe);
        return MAPPER.readValue(jsonOutput, ComplaintDTO.class);
    }

    // Request that matches backend create schema (categoryId+priority)
    public static class CreateComplaintRequest {
        private String title;
        private String description;
        private Long categoryId;
        private String priority;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
    }
}
