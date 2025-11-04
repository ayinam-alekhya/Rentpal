package com.rentpal.service;

import com.rentpal.dto.PropertySummaryDTO;
import com.rentpal.utils.ApiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public class PropertyService {
    
    public List<PropertySummaryDTO> getAllProperties() throws Exception {
        String json = ApiUtil.get("/properties/summary");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<PropertySummaryDTO>>(){});
    }
    
    public PropertySummaryDTO getPropertyById(Long propertyId) throws Exception {
        String json = ApiUtil.get("/properties/" + propertyId);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, PropertySummaryDTO.class);
    }
    
    public List<PropertySummaryDTO> getPropertiesByOwnerId(Long ownerId) throws Exception {
        String json = ApiUtil.get("/properties/owner/" + ownerId);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<PropertySummaryDTO>>(){});
    }
    
    public List<PropertySummaryDTO> searchProperties(String location, Double minRent, Double maxRent) throws Exception {
        StringBuilder endpoint = new StringBuilder("/properties/search?");
        
        if (location != null && !location.isEmpty()) {
            endpoint.append("location=").append(location).append("&");
        }
        
        if (minRent != null) {
            endpoint.append("minRent=").append(minRent).append("&");
        }
        
        if (maxRent != null) {
            endpoint.append("maxRent=").append(maxRent).append("&");
        }
        
        // Remove trailing '&' or '?' if present
        String endpointStr = endpoint.toString();
        if (endpointStr.endsWith("&") || endpointStr.endsWith("?")) {
            endpointStr = endpointStr.substring(0, endpointStr.length() - 1);
        }
        
        String json = ApiUtil.get(endpointStr);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<PropertySummaryDTO>>(){});
    }
}