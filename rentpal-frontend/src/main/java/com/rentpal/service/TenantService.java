package com.rentpal.service;

import com.rentpal.dto.TenantDTO;
import com.rentpal.utils.ApiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public class TenantService {
    
    public List<TenantDTO> getAllTenants() throws Exception {
        String json = ApiUtil.get("/tenants");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<TenantDTO>>(){});
    }
    
    public TenantDTO getTenantById(Long tenantId) throws Exception {
        String json = ApiUtil.get("/tenants/" + tenantId);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, TenantDTO.class);
    }
    
    public TenantDTO createTenant(TenantDTO tenant) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(tenant);
        String jsonOutput = ApiUtil.post("/tenants", jsonInput);
        return mapper.readValue(jsonOutput, TenantDTO.class);
    }
    
    public TenantDTO updateTenant(Long tenantId, TenantDTO tenant) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(tenant);
        String jsonOutput = ApiUtil.put("/tenants/" + tenantId, jsonInput);
        return mapper.readValue(jsonOutput, TenantDTO.class);
    }
    
    public boolean deleteTenant(Long tenantId) throws Exception {
        return ApiUtil.delete("/tenants/" + tenantId);
    }
}