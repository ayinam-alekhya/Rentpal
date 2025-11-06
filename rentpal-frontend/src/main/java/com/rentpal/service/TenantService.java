package com.rentpal.service;

import com.rentpal.dto.CreateTenantRequest;
import com.rentpal.dto.TenantDTO;
import com.rentpal.utils.ApiUtil;
import com.rentpal.dto.TenantSummaryDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class TenantService {

    // Reuse one mapper and ignore unknown fields from backend responses
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public List<TenantDTO> getAllTenants() throws Exception {
        String json = ApiUtil.get("/tenants");
        return MAPPER.readValue(json, new TypeReference<List<TenantDTO>>() {});
    }

    public TenantDTO getTenantById(Long tenantId) throws Exception {
        String json = ApiUtil.get("/tenants/" + tenantId);
        return MAPPER.readValue(json, TenantDTO.class);
    }

    public TenantDTO createTenant(TenantDTO tenant) throws Exception {
        String jsonInput = MAPPER.writeValueAsString(tenant);
        String jsonOutput = ApiUtil.post("/tenants", jsonInput);
        return MAPPER.readValue(jsonOutput, TenantDTO.class);
    }

    public TenantDTO updateTenant(Long tenantId, TenantDTO tenant) throws Exception {
        String jsonInput = MAPPER.writeValueAsString(tenant);
        String jsonOutput = ApiUtil.put("/tenants/" + tenantId, jsonInput);
        return MAPPER.readValue(jsonOutput, TenantDTO.class);
    }

    public boolean deleteTenant(Long tenantId) throws Exception {
        return ApiUtil.delete("/tenants/" + tenantId);
    }
    public List<TenantSummaryDTO> getTenantsByOwner(Long ownerId) throws Exception {
        String json = ApiUtil.get("/tenants/owner/" + ownerId);
        return MAPPER.readValue(json, new TypeReference<List<TenantSummaryDTO>>() {});
    }
    public TenantDTO createTenant(CreateTenantRequest req) throws Exception {
        String jsonInput = MAPPER.writeValueAsString(req);
        String jsonOutput = ApiUtil.post("/tenants", jsonInput);
        return MAPPER.readValue(jsonOutput, TenantDTO.class);
    }

}
