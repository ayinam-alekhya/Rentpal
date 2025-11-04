package com.rentpal.service;

import com.rentpal.dto.OwnerDTO;
import com.rentpal.dto.CreateOwnerDTO;
import com.rentpal.utils.ApiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public class OwnerService {
    
    public List<OwnerDTO> getAllOwners() throws Exception {
        String json = ApiUtil.get("/owners");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<OwnerDTO>>(){});
    }
    
    public OwnerDTO getOwnerById(Long ownerId) throws Exception {
        String json = ApiUtil.get("/owners/" + ownerId);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, OwnerDTO.class);
    }
    
    public OwnerDTO createOwner(OwnerDTO owner) throws Exception {
        // Use CreateOwnerDTO to avoid sending tenants field
        CreateOwnerDTO createOwnerDTO = new CreateOwnerDTO(owner);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(createOwnerDTO);
        String jsonOutput = ApiUtil.post("/owners", jsonInput);
        return mapper.readValue(jsonOutput, OwnerDTO.class);
    }
    
    public OwnerDTO updateOwner(Long ownerId, OwnerDTO owner) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(owner);
        String jsonOutput = ApiUtil.put("/owners/" + ownerId, jsonInput);
        return mapper.readValue(jsonOutput, OwnerDTO.class);
    }
    
    public boolean deleteOwner(Long ownerId) throws Exception {
        return ApiUtil.delete("/owners/" + ownerId);
    }
}