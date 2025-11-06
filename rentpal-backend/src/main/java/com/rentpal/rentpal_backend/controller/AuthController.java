package com.rentpal.rentpal_backend.controller;

import com.rentpal.rentpal_backend.dto.UserLoginDTO;
import com.rentpal.rentpal_backend.dto.UserRegistrationDTO;
import com.rentpal.rentpal_backend.model.Owner;
import com.rentpal.rentpal_backend.model.Tenant;
import com.rentpal.rentpal_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

   @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserRegistrationDTO registrationDTO) {
        try {
            String role = registrationDTO.getRole();
            if ("owner".equalsIgnoreCase(role)) {
                Owner owner = authService.registerOwner(registrationDTO);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Owner registered successfully",
                    "userType", "owner",
                    "user", Map.of("id", owner.getOwnerId(), "name", owner.getName(), "email", owner.getEmail())
                ));
            } else if ("tenant".equalsIgnoreCase(role)) {
                if (registrationDTO.getOwnerId() == null) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "ownerId is required for tenant registration"
                    ));
                }
                Tenant tenant = authService.registerTenant(registrationDTO);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Tenant registered successfully",
                    "userType", "tenant",
                    "user", Map.of("id", tenant.getTenantId(), "name", tenant.getName(), "email", tenant.getEmail())
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid role. Must be 'owner' or 'tenant'"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Registration failed: " + e.getMessage()
            ));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();
        String role = loginDTO.getRole();
        System.out.println("[/auth/login] email=" + loginDTO.getEmail() + ", role=" + loginDTO.getRole());
        
        try {
            if ("owner".equalsIgnoreCase(role)) {
                Owner owner = authService.authenticateOwner(email, password);
                if (owner != null) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("userType", "owner");
                    response.put("user", owner);
                    return ResponseEntity.ok(response);
                }
            } else if ("tenant".equalsIgnoreCase(role)) {
                Tenant tenant = authService.authenticateTenant(email, password);
                if (tenant != null) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("userType", "tenant");
                    response.put("user", tenant);
                    return ResponseEntity.ok(response);
                }
            }
            
            // If authentication failed
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid email, password, or role");
            return ResponseEntity.status(401).body(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Authentication failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}