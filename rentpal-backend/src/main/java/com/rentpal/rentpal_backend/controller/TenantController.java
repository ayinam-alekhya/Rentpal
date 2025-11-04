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
    public Tenant getTenantById(@PathVariable Long id) {
        return tenantService.getTenantById(id);
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