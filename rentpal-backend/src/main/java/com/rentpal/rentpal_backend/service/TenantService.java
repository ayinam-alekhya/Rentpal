package com.rentpal.rentpal_backend.service;

import com.rentpal.rentpal_backend.dto.CreateTenantRequest;
import com.rentpal.rentpal_backend.dto.TenantSummaryDTO;
import com.rentpal.rentpal_backend.dto.UpdateTenantRequest;
import com.rentpal.rentpal_backend.exception.ResourceNotFoundException;
import com.rentpal.rentpal_backend.model.Owner;
import com.rentpal.rentpal_backend.model.Tenant;
import com.rentpal.rentpal_backend.repository.OwnerRepository;
import com.rentpal.rentpal_backend.repository.TenantRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.util.StringUtils;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    // ✅ CREATE
    @Transactional
    public Tenant createTenant(Tenant tenant) {
        // Validate and set owner if provided
        if (tenant.getOwner() != null && tenant.getOwner().getOwnerId() != null) {
            try {
                Owner owner = ownerRepository.findById(tenant.getOwner().getOwnerId())
                        .orElse(null);
                tenant.setOwner(owner);
            } catch (Exception e) {
                // If owner not found, set owner to null
                tenant.setOwner(null);
            }
        } else {
            // Set owner to null explicitly if no owner is provided
            tenant.setOwner(null);
        }
        return tenantRepository.save(tenant);
    }

    @Transactional
    public Tenant createTenant(CreateTenantRequest req) {
        if (req.getOwnerId() == null) {
            throw new RuntimeException("ownerId is required to create a tenant");
        }

        Owner owner = ownerRepository.findById(req.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Owner not found with ID: " + req.getOwnerId()));

        Tenant tenant = new Tenant();
        tenant.setName(req.getName());
        tenant.setEmail(req.getEmail());
        tenant.setPhone(req.getPhone());
        tenant.setRoomNumber(req.getRoomNumber());
        tenant.setRentAmount(req.getRentAmount());
        tenant.setStatus("Active");
        tenant.setOwner(owner); // <-- attach during signup

        // defaults already handled in Tenant() ctor (remainingRent/paymentStatus)
        return tenantRepository.save(tenant);
    }

    public List<TenantSummaryDTO> getAllTenants() {
        List<Tenant> tenants = tenantRepository.findAll();
        return tenants.stream()
                .map(t -> new TenantSummaryDTO(
                        t.getTenantId(),
                        t.getName(),
                        t.getRoomNumber(),
                        t.getPhone(),
                        t.getRemainingRent(),
                        t.getPaymentStatus()
                ))
                .toList();
    }


    // ✅ UPDATE
    @Transactional
    public Tenant updateTenant(Long id, Tenant tenantDetails) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + id));

        tenant.setName(tenantDetails.getName());
        tenant.setEmail(tenantDetails.getEmail());
        tenant.setPhone(tenantDetails.getPhone());
        tenant.setRoomNumber(tenantDetails.getRoomNumber());
        tenant.setRentAmount(tenantDetails.getRentAmount());
        tenant.setStatus(tenantDetails.getStatus());

        if (tenantDetails.getOwner() != null && tenantDetails.getOwner().getOwnerId() != null) {
            Owner owner = ownerRepository.findById(tenantDetails.getOwner().getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Owner not found with ID: " + tenantDetails.getOwner().getOwnerId()));
            tenant.setOwner(owner);
        }

        return tenantRepository.save(tenant);
    }

    // ✅ UPDATE with DTO
    @Transactional
    public Tenant updateTenant(Long id, UpdateTenantRequest tenantRequest) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + id));

        tenant.setName(tenantRequest.getName());
        tenant.setEmail(tenantRequest.getEmail());
        tenant.setPhone(tenantRequest.getPhone());
        tenant.setRoomNumber(tenantRequest.getRoomNumber());
        tenant.setRentAmount(tenantRequest.getRentAmount());
        tenant.setStatus(tenantRequest.getStatus());

        if (tenantRequest.getOwnerId() != null) {
            Owner owner = ownerRepository.findById(tenantRequest.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Owner not found with ID: " + tenantRequest.getOwnerId()));
            tenant.setOwner(owner);
        } else {
            tenant.setOwner(null);
        }

        return tenantRepository.save(tenant);
    }

    // ✅ DELETE
    @Transactional
    public void deleteTenant(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + id));
        tenantRepository.delete(tenant);
    }
    
    public Tenant getTenantById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + id));
    }

    public List<TenantSummaryDTO> getTenantSummariesByOwner(Long ownerId) {
        return tenantRepository.findByOwner_OwnerId(ownerId).stream()
                .map(t -> new TenantSummaryDTO(
                        t.getTenantId(),
                        t.getName(),
                        t.getPhone(),          // ✅ Added phone here
                        t.getRoomNumber(),
                        t.getRemainingRent(),
                        t.getPaymentStatus()
                ))
                .toList();
    }
}