package com.rentpal.rentpal_backend.controller;

import com.rentpal.rentpal_backend.dto.CreateTenantRequest;
import com.rentpal.rentpal_backend.dto.TenantSummaryDTO;
import com.rentpal.rentpal_backend.dto.UpdateTenantRequest;
import com.rentpal.rentpal_backend.model.Owner;
import com.rentpal.rentpal_backend.model.Tenant;
import com.rentpal.rentpal_backend.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@CrossOrigin(origins = "*")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @GetMapping
    public List<TenantSummaryDTO> getAllTenants() {
        return tenantService.getAllTenants();
    }


    @GetMapping("/{id}")
    public com.rentpal.rentpal_backend.dto.TenantDTO getTenantById(@PathVariable Long id) {
        Tenant t = tenantService.getTenantById(id); // entity

        com.rentpal.rentpal_backend.dto.TenantDTO dto = new com.rentpal.rentpal_backend.dto.TenantDTO();
        dto.setTenantId(t.getTenantId());
        dto.setName(t.getName());
        dto.setEmail(t.getEmail());
        dto.setPhone(t.getPhone());
        dto.setRoomNumber(t.getRoomNumber());
        dto.setRentAmount(t.getRentAmount());
        dto.setStatus(t.getStatus());
        dto.setRemainingRent(t.getRemainingRent());
        dto.setPaymentStatus(t.getPaymentStatus());

        if (t.getOwner() != null) {
            dto.setOwnerId(t.getOwner().getOwnerId());

            // Optional: include a tiny owner summary (id, name, email) for display
            com.rentpal.rentpal_backend.dto.OwnerDTO od = new com.rentpal.rentpal_backend.dto.OwnerDTO();
            od.setOwnerId(t.getOwner().getOwnerId());
            od.setName(t.getOwner().getName());
            od.setEmail(t.getOwner().getEmail());
            dto.setOwner(od);
        }
        return dto;
    }

    
    @GetMapping("/owner/{ownerId}")
    public List<TenantSummaryDTO> getTenantsForOwner(@PathVariable Long ownerId) {
        return tenantService.getTenantSummariesByOwner(ownerId);
    }

    // ✅ Create tenant
    @PostMapping
    public Tenant createTenant(@RequestBody CreateTenantRequest tenantRequest) {
        // Convert DTO to entity
        Tenant tenant = new Tenant();
        tenant.setName(tenantRequest.getName());
        tenant.setEmail(tenantRequest.getEmail());
        tenant.setPhone(tenantRequest.getPhone());
        tenant.setRoomNumber(tenantRequest.getRoomNumber());
        tenant.setRentAmount(tenantRequest.getRentAmount());
        
        // Set owner if provided
        if (tenantRequest.getOwnerId() != null) {
            Owner owner = new Owner();
            owner.setOwnerId(tenantRequest.getOwnerId());
            tenant.setOwner(owner);
        }
        
        return tenantService.createTenant(tenant);
    }

    @PutMapping("/{id}")
    public Tenant updateTenant(@PathVariable Long id, @RequestBody UpdateTenantRequest tenantRequest) {
        // Convert DTO to entity
        Tenant tenant = new Tenant();
        tenant.setName(tenantRequest.getName());
        tenant.setEmail(tenantRequest.getEmail());
        tenant.setPhone(tenantRequest.getPhone());
        tenant.setRoomNumber(tenantRequest.getRoomNumber());
        tenant.setRentAmount(tenantRequest.getRentAmount());
        tenant.setStatus(tenantRequest.getStatus());
        
        // Set owner if provided
        if (tenantRequest.getOwnerId() != null) {
            Owner owner = new Owner();
            owner.setOwnerId(tenantRequest.getOwnerId());
            tenant.setOwner(owner);
        }
        
        return tenantService.updateTenant(id, tenant);
    }

    @DeleteMapping("/{id}")
    public String deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return "Tenant deleted successfully!";
    }
}