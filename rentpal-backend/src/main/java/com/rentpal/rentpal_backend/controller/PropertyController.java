package com.rentpal.rentpal_backend.controller;

import com.rentpal.rentpal_backend.dto.CreatePropertyRequest;
import com.rentpal.rentpal_backend.dto.PropertySummaryDTO;
import com.rentpal.rentpal_backend.model.Property;
import com.rentpal.rentpal_backend.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    // CREATE - Add a new property
    @PostMapping
    public ResponseEntity<Map<String, Object>> addProperty(@RequestBody CreatePropertyRequest request) {
        try {
            Property property = propertyService.addProperty(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Property added successfully");
            response.put("property", property);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to add property: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // READ - Get all properties
    @GetMapping
    public List<Property> getAllProperties() {
        return propertyService.getAllProperties();
    }

    // GET all properties as summary DTOs
    @GetMapping("/summary")
    public List<PropertySummaryDTO> getAllPropertiesSummary() {
        return propertyService.getAllPropertiesSummary();
    }

    // READ - Get property by ID
    @GetMapping("/{propertyId}")
    public ResponseEntity<Map<String, Object>> getPropertyById(@PathVariable Long propertyId) {
        try {
            Property property = propertyService.getPropertyById(propertyId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("property", property);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Property not found: " + e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    // READ - Get properties by owner ID
    @GetMapping("/owner/{ownerId}")
    public List<Property> getPropertiesByOwnerId(@PathVariable Long ownerId) {
        return propertyService.getPropertiesByOwnerId(ownerId);
    }

    // SEARCH - Search properties by location or rent range
    @GetMapping("/search")
    public List<Property> searchProperties(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minRent,
            @RequestParam(required = false) Double maxRent) {
        return propertyService.searchProperties(location, minRent, maxRent);
    }

    // UPDATE - Update property details
    @PutMapping("/{propertyId}")
    public ResponseEntity<Map<String, Object>> updateProperty(
            @PathVariable Long propertyId,
            @RequestBody CreatePropertyRequest request) {
        try {
            Property property = propertyService.updateProperty(propertyId, request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Property updated successfully");
            response.put("property", property);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update property: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // DELETE - Delete a property
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Map<String, Object>> deleteProperty(@PathVariable Long propertyId) {
        try {
            propertyService.deleteProperty(propertyId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Property deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete property: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}