package com.rentpal.rentpal_backend.service;

import com.rentpal.rentpal_backend.dto.CreateTenantRequest;
import com.rentpal.rentpal_backend.dto.TenantSummaryDTO;
import com.rentpal.rentpal_backend.dto.UpdateTenantRequest;
import com.rentpal.rentpal_backend.exception.BadRequestException;
import com.rentpal.rentpal_backend.exception.ResourceNotFoundException;
import com.rentpal.rentpal_backend.model.Owner;
import com.rentpal.rentpal_backend.model.Tenant;
import com.rentpal.rentpal_backend.repository.OwnerRepository;
import com.rentpal.rentpal_backend.repository.TenantRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final OwnerRepository ownerRepository;

    public TenantService(TenantRepository tenantRepository, OwnerRepository ownerRepository) {
        this.tenantRepository = tenantRepository;
        this.ownerRepository = ownerRepository;
    }

    // =========================
    // CREATE (requires ownerId)
    // =========================
    @Transactional
    public Tenant createTenant(CreateTenantRequest req) {
        if (req.getOwnerId() == null) {
            throw new BadRequestException("ownerId is required to create a tenant");
        }

        Owner owner = ownerRepository.findById(req.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + req.getOwnerId()));
        
        double rentAmount = req.getRentAmount();
        if (Double.isNaN(rentAmount) || rentAmount < 0) {
            rentAmount = 0.0; 
        }

        Tenant t = new Tenant();
        t.setName(req.getName());
        t.setEmail(req.getEmail());
        t.setPhone(req.getPhone());
        t.setRoomNumber(req.getRoomNumber());
        t.setRentAmount(rentAmount);
        t.setOwner(owner);

        t.setStatus("Active");

        return tenantRepository.save(t);
    }

    // =========================
    // READ
    // =========================
    public Tenant getTenantById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + id));
    }

    public List<TenantSummaryDTO> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(t -> new TenantSummaryDTO(
                        t.getTenantId(),
                        t.getName(),
                        t.getRoomNumber(),
                        t.getRemainingRent(),
                        t.getPaymentStatus()
                ))
                .toList();
    }

    // =========================
    // UPDATE (entity variant)
    //   - does NOT silently null owner
    //   - only changes owner if provided
    // =========================
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
        // else: keep current owner

        return tenantRepository.save(tenant);
    }

    // =========================
    // UPDATE (DTO variant)
    //   - set owner only if ownerId present
    //   - if ownerId explicitly null, clears mapping
    // =========================
    @Transactional
    public Tenant updateTenant(Long id, UpdateTenantRequest req) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + id));

        tenant.setName(req.getName());
        tenant.setEmail(req.getEmail());
        tenant.setPhone(req.getPhone());
        tenant.setRoomNumber(req.getRoomNumber());
        tenant.setRentAmount(req.getRentAmount());
        tenant.setStatus(req.getStatus());

        // Change owner only if ownerId is provided
        if (req.getOwnerId() != null) {
            Owner owner = ownerRepository.findById(req.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + req.getOwnerId()));
            tenant.setOwner(owner);
        }
        // else: leave current owner mapping as-is

        return tenantRepository.save(tenant);
    }


    // =========================
    // DELETE
    // =========================
    @Transactional
    public void deleteTenant(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + id));
        tenantRepository.delete(tenant);
    }

    // =========================
    // Helpers
    // =========================
    private boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    private boolean hasField(Double d) {
        return d != null;
    }
}
