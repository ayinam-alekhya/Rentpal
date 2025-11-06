package com.rentpal.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentpal.dto.ComplaintDTO;
import com.rentpal.utils.ApiUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ComplaintService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // --- GET BY OWNER with optional status filter ---
    public List<ComplaintDTO> getComplaintsByOwner(Long ownerId, String status) throws Exception {
        // default to ALL when null/empty
        String s = (status == null || status.isBlank()) ? "ALL" : status;
        String json = ApiUtil.get("/complaints/owner/" + ownerId + "?status=" + s);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<ComplaintDTO>>() {});
    }


    // Convenience overload (defaults to ALL)
    public List<ComplaintDTO> getComplaintsByOwner(long ownerId) throws Exception {
        return getComplaintsByOwner(ownerId, "ALL");
    }

    // --- GET BY TENANT with optional status filter ---
    public List<ComplaintDTO> getComplaintsByTenant(Long tenantId, String status) throws Exception {
        String s = (status == null || status.isBlank()) ? "ALL" : status;
        String url = "/complaints/tenant/" + tenantId;

        // only append ?status= when not ALL (match your backend contract)
        if (!"ALL".equalsIgnoreCase(s)) {
            url += "?status=" + s;
        }

        String json = ApiUtil.get(url);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<ComplaintDTO>>() {});
    }

    // Convenience overload (defaults to ALL)
    public List<ComplaintDTO> getComplaintsByTenant(long tenantId) throws Exception {
        return getComplaintsByTenant(tenantId, "ALL");
    }

    // --- CREATE complaint (make sure complaint.setOwnerId(...) was set by the caller) ---
    public ComplaintDTO addComplaint(long tenantId, ComplaintDTO complaint) throws Exception {
        String body = MAPPER.writeValueAsString(complaint);
        String json = ApiUtil.post("/complaints/" + tenantId, body);
        return MAPPER.readValue(json, ComplaintDTO.class);
    }

    // --- UPDATE status (matches backend: PUT /api/complaints/{id}/status?status=RESOLVED) ---
    public ComplaintDTO updateComplaintStatus(long complaintId, String status) throws Exception {
        String url = "/complaints/" + complaintId + "/status?status=" +
                     URLEncoder.encode(status, StandardCharsets.UTF_8);
        String json = ApiUtil.put(url, ""); // empty body is fine
        return MAPPER.readValue(json, ComplaintDTO.class);
    }
}
