package com.rentpal.rentpal_backend.controller;

import com.rentpal.rentpal_backend.dto.CreateTenantRequest;
import com.rentpal.rentpal_backend.dto.TenantSummaryDTO;
import com.rentpal.rentpal_backend.dto.UpdateTenantRequest;
import com.rentpal.rentpal_backend.model.Tenant;
import com.rentpal.rentpal_backend.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@CrossOrigin(origins = "*")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    // LIST (summary DTOs)
    @GetMapping
    public List<TenantSummaryDTO> getAllTenants() {
        return tenantService.getAllTenants();
    }

    // GET by id (entity or you can create a detailed DTO if you prefer)
    @GetMapping("/{id}")
    public Tenant getTenantById(@PathVariable Long id) {
        return tenantService.getTenantById(id);
    }

    // CREATE — pass DTO directly; service enforces ownerId presence and resolves the Owner
    @PostMapping
    public ResponseEntity<Tenant> createTenant(@RequestBody CreateTenantRequest tenantRequest) {
        Tenant saved = tenantService.createTenant(tenantRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // UPDATE — use the DTO variant, service will set owner only if ownerId present
    @PutMapping("/{id}")
    public Tenant updateTenant(@PathVariable Long id, @RequestBody UpdateTenantRequest tenantRequest) {
        return tenantService.updateTenant(id, tenantRequest);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.ok("Tenant deleted successfully!");
    }
}
