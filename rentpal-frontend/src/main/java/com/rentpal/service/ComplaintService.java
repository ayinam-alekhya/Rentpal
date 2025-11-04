package com.rentpal.service;

import com.rentpal.dto.ComplaintDTO;
import com.rentpal.utils.ApiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public class ComplaintService {
    
    public List<ComplaintDTO> getComplaintsByOwner(Long ownerId) throws Exception {
        String json = ApiUtil.get("/complaints/owner/" + ownerId);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<ComplaintDTO>>(){});
    }
    
    public List<ComplaintDTO> getComplaintsByTenant(Long tenantId) throws Exception {
        String json = ApiUtil.get("/complaints/tenant/" + tenantId);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<ComplaintDTO>>(){});
    }
    
    public ComplaintDTO addComplaint(Long tenantId, ComplaintDTO complaint) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(complaint);
        String jsonOutput = ApiUtil.post("/complaints/" + tenantId, jsonInput);
        return mapper.readValue(jsonOutput, ComplaintDTO.class);
    }
    
    public ComplaintDTO updateComplaintStatus(Long complaintId, String status) throws Exception {
        String jsonOutput = ApiUtil.put("/complaints/" + complaintId + "?status=" + status, "{}");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonOutput, ComplaintDTO.class);
    }
}