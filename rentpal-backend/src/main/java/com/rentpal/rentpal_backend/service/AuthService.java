package com.rentpal.rentpal_backend.service;

import com.rentpal.rentpal_backend.dto.UserRegistrationDTO;
import com.rentpal.rentpal_backend.exception.ResourceNotFoundException;
import com.rentpal.rentpal_backend.model.Owner;
import com.rentpal.rentpal_backend.model.Tenant;
import com.rentpal.rentpal_backend.repository.OwnerRepository;
import com.rentpal.rentpal_backend.repository.TenantRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private TenantRepository tenantRepository;

    public Owner registerOwner(UserRegistrationDTO registrationDTO) {
        Owner owner = new Owner();
        owner.setName(registrationDTO.getName());
        owner.setEmail(registrationDTO.getEmail());
        owner.setPhone(registrationDTO.getPhone() != null ? registrationDTO.getPhone() : "");
        owner.setAddress(registrationDTO.getAddress() != null ? registrationDTO.getAddress() : "");
        return ownerRepository.save(owner);
    }

    /**
     * Upsert-by-email + attach to owner.
     * If a tenant with this email already exists (e.g., created from Owner Dashboard),
     * we UPDATE that record instead of creating a duplicate.
     */
    @Transactional
    public Tenant registerTenant(UserRegistrationDTO registrationDTO) {
        // 1) Owner is required for tenant signup
        if (registrationDTO.getOwnerId() == null) {
            throw new IllegalArgumentException("ownerId is required for tenant registration");
        }
        Owner owner = ownerRepository.findById(registrationDTO.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + registrationDTO.getOwnerId()));

        // 2) Try to find existing tenant by email
        Tenant tenant = null;
        String email = registrationDTO.getEmail();

        if (email != null && !email.isBlank()) {
            List<Tenant> matches = tenantRepository.findAllByEmailIgnoreCase(email);

            if (matches.isEmpty()) {
                tenant = new Tenant();
                tenant.setEmail(email);
            } else if (matches.size() == 1) {
                tenant = matches.get(0); // claim existing pre-created tenant
            } else {
                // If duplicates still exist (until you clean the DB), prefer the one under this owner
                tenant = matches.stream()
                        .filter(t -> t.getOwner() != null && t.getOwner().getOwnerId().equals(owner.getOwnerId()))
                        .findFirst()
                        .orElse(matches.get(0)); // fallback: pick the first, or throw to force cleanup
            }
        } else {
            tenant = new Tenant(); // email missing (not recommended) – still allow creation
        }

        // 3) Fill/overwrite fields
        if (registrationDTO.getName() != null) tenant.setName(registrationDTO.getName());
        tenant.setPhone(registrationDTO.getPhone() != null ? registrationDTO.getPhone() : (tenant.getPhone() == null ? "" : tenant.getPhone()));
        tenant.setRoomNumber(registrationDTO.getRoomNumber() != null ? registrationDTO.getRoomNumber() : (tenant.getRoomNumber() == null ? "" : tenant.getRoomNumber()));
        if (registrationDTO.getRentAmount() != null) {
            tenant.setRentAmount(registrationDTO.getRentAmount());
            tenant.setRemainingRent(registrationDTO.getRentAmount());
        }
        tenant.setPaymentStatus(tenant.getPaymentStatus() == null ? "Unpaid" : tenant.getPaymentStatus());
        tenant.setStatus("Active");

        // 4) Attach owner
        tenant.setOwner(owner);

        // 5) Save
        return tenantRepository.save(tenant);
    }

    public Owner authenticateOwner(String email, String password) {
        System.out.println("Authenticating owner with email: " + email);
        Owner owner = ownerRepository.findByEmail(email);
        if (owner != null) {
            System.out.println("Owner authenticated: " + owner.getName() + " (" + owner.getEmail() + ")");
            return owner;
        }
        return null;
    }

    /**
     * Safe tenant auth: no single-result queries.
     * Returns the single match; if duplicates exist, you can:
     *  - prefer one by some rule, or
     *  - return null to force user to clarify.
     */
    public Tenant authenticateTenant(String email, String password) {
        System.out.println("Authenticating tenant with email: " + email);

        List<Tenant> matches = tenantRepository.findAllByEmailIgnoreCase(email);
        if (matches.isEmpty()) {
            // Optional: keep your name fallback, but it's better to remove it for security/clarity
            System.out.println("No tenant found by email.");
            return null;
        }
        if (matches.size() > 1) {
            // Avoid throwing; log and return null (your controller will send 401 cleanly)
            System.out.println("Multiple tenants found for email: " + email + " -> " + matches.size());
            return null;
        }

        Tenant tenant = matches.get(0);
        System.out.println("Tenant authenticated: " + tenant.getName() + " (" + tenant.getEmail() + ")");
        return tenant;
    }
}
