package com.rentpal.rentpal_backend.service;

import com.rentpal.rentpal_backend.dto.PropertySummaryDTO;
import com.rentpal.rentpal_backend.dto.CreatePropertyRequest;
import com.rentpal.rentpal_backend.model.Owner;
import com.rentpal.rentpal_backend.model.Property;
import com.rentpal.rentpal_backend.repository.OwnerRepository;
import com.rentpal.rentpal_backend.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    // CREATE - Add a new property
    public Property addProperty(CreatePropertyRequest request) {
        Optional<Owner> ownerOpt = ownerRepository.findById(request.getOwnerId());
        if (ownerOpt.isEmpty()) {
            throw new RuntimeException("Owner not found with ID: " + request.getOwnerId());
        }

        Property property = new Property();
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setRentAmount(request.getRentAmount());
        property.setLocation(request.getLocation());
        property.setOwner(ownerOpt.get());
        property.setStatus("Available");

        return propertyRepository.save(property);
    }

    // READ - Get all properties
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // READ - Get all properties as summary DTOs
    public List<PropertySummaryDTO> getAllPropertiesSummary() {
        List<Property> properties = propertyRepository.findAll();
        return properties.stream()
                .map(p -> new PropertySummaryDTO(
                        p.getPropertyId(),
                        p.getTitle(),
                        p.getRentAmount(),
                        p.getLocation(),
                        p.getDescription(),
                        p.getStatus(),
                        p.getOwner() != null ? p.getOwner().getOwnerId() : null,
                        p.getOwner() != null ? p.getOwner().getName() : null
                ))
                .collect(Collectors.toList());
    }

    // READ - Get property by ID
    public Property getPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + propertyId));
    }

    // READ - Get properties by owner ID
    public List<Property> getPropertiesByOwnerId(Long ownerId) {
        return propertyRepository.findByOwnerOwnerId(ownerId);
    }

    // READ - Search properties by location or rent range
    public List<Property> searchProperties(String location, Double minRent, Double maxRent) {
        List<Property> properties = propertyRepository.findAll();
        
        return properties.stream()
                .filter(property -> {
                    boolean locationMatch = location == null || property.getLocation().toLowerCase().contains(location.toLowerCase());
                    boolean rentMatch = true;
                    
                    if (minRent != null && maxRent != null) {
                        rentMatch = property.getRentAmount() >= minRent && property.getRentAmount() <= maxRent;
                    } else if (minRent != null) {
                        rentMatch = property.getRentAmount() >= minRent;
                    } else if (maxRent != null) {
                        rentMatch = property.getRentAmount() <= maxRent;
                    }
                    
                    return locationMatch && rentMatch;
                })
                .collect(Collectors.toList());
    }

    // UPDATE - Update property details
    public Property updateProperty(Long propertyId, CreatePropertyRequest request) {
        Property property = getPropertyById(propertyId);
        
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setRentAmount(request.getRentAmount());
        property.setLocation(request.getLocation());
        
        return propertyRepository.save(property);
    }

    // DELETE - Delete a property
    public void deleteProperty(Long propertyId) {
        Property property = getPropertyById(propertyId);
        propertyRepository.delete(property);
    }
}