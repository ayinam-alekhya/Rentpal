package com.rentpal.rentpal_backend.service;

import com.rentpal.rentpal_backend.dto.UserRegistrationDTO;
import com.rentpal.rentpal_backend.model.Owner;
import com.rentpal.rentpal_backend.model.Tenant;
import com.rentpal.rentpal_backend.repository.OwnerRepository;
import com.rentpal.rentpal_backend.repository.TenantRepository;
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
        // In a real application, you would hash the password before saving
        // TODO: Implement proper password hashing
        return ownerRepository.save(owner);
    }

    public Tenant registerTenant(UserRegistrationDTO registrationDTO) {
        Tenant tenant = new Tenant();
        tenant.setName(registrationDTO.getName());
        tenant.setEmail(registrationDTO.getEmail());
        tenant.setPhone(registrationDTO.getPhone() != null ? registrationDTO.getPhone() : "");
        tenant.setRoomNumber(registrationDTO.getRoomNumber() != null ? registrationDTO.getRoomNumber() : "");
        tenant.setRentAmount(registrationDTO.getRentAmount() != null ? registrationDTO.getRentAmount() : 0.0);
        tenant.setRemainingRent(registrationDTO.getRentAmount() != null ? registrationDTO.getRentAmount() : 0.0);
        tenant.setPaymentStatus("Unpaid");
        tenant.setStatus("Active");
        // In a real application, you would hash the password before saving
        // TODO: Implement proper password hashing
        return tenantRepository.save(tenant);
    }

    public Owner authenticateOwner(String email, String password) {
        System.out.println("Authenticating owner with email: " + email);
        Owner owner = ownerRepository.findByEmail(email);
        System.out.println("Owner found: " + (owner != null ? owner.getName() : "null"));
        
        // In a real application, you would hash and compare passwords
        // For now, we'll just check if the email exists and return the owner
        // TODO: Implement proper password hashing and verification
        
        // For demonstration purposes, we'll accept any password for existing owners
        // In a real application, you would verify the hashed password
        if (owner != null) {
            System.out.println("Owner authenticated: " + owner.getName() + " (" + owner.getEmail() + ")");
            return owner;
        }
        
        return null;
    }

    public Tenant authenticateTenant(String email, String password) {
        System.out.println("Authenticating tenant with email: " + email);
        Tenant tenant = tenantRepository.findByEmail(email);
        System.out.println("Tenant found by email: " + (tenant != null ? tenant.getName() : "null"));
        
        // In a real application, you would hash and compare passwords
        // For now, we'll just check if the email exists and return the tenant
        // TODO: Implement proper password hashing and verification
        
        // If tenant is not found by email, try to find by name (fallback for existing tenants without emails)
        if (tenant == null) {
            // This is a temporary workaround for existing tenants without emails
            // In a real application, all tenants should have emails
            try {
                // Try to parse email as a name if it contains no @ symbol
                if (!email.contains("@")) {
                    // Try to find tenant by name
                    System.out.println("Trying to find tenant by name: " + email);
                    List<Tenant> tenants = tenantRepository.findByName(email);
                    if (!tenants.isEmpty()) {
                        tenant = tenants.get(0); // Return first match
                        System.out.println("Tenant found by name: " + tenant.getName());
                    }
                }
            } catch (Exception e) {
                System.out.println("Error finding tenant by name: " + e.getMessage());
                // Ignore and return null
            }
        }
        
        // For demonstration purposes, we'll accept any password for existing tenants
        // In a real application, you would verify the hashed password
        if (tenant != null) {
            System.out.println("Tenant authenticated: " + tenant.getName() + " (" + tenant.getEmail() + ")");
            return tenant;
        }
        
        return null;
    }
}