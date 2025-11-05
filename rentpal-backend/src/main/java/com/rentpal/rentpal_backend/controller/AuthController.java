package com.rentpal.rentpal_backend.controller;

import com.rentpal.rentpal_backend.dto.AuthOwnerDTO;
import com.rentpal.rentpal_backend.dto.AuthTenantDTO;
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
            if ("owner".equalsIgnoreCase(registrationDTO.getRole())) {
                Owner owner = authService.registerOwner(registrationDTO);
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Owner registered successfully");
                response.put("user", owner);
                return ResponseEntity.ok(response);
            } else if ("tenant".equalsIgnoreCase(registrationDTO.getRole())) {
                Tenant tenant = authService.registerTenant(registrationDTO);
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Tenant registered successfully");
                response.put("user", tenant);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid role. Must be 'owner' or 'tenant'");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

 @PostMapping("/login")
public ResponseEntity<Map<String, Object>> login(@RequestBody UserLoginDTO loginDTO) {
    String email = loginDTO.getEmail();
    String password = loginDTO.getPassword();
    String role = loginDTO.getRole();

    try {
        Map<String, Object> ok = new HashMap<>();
        ok.put("success", true);

        if ("owner".equalsIgnoreCase(role)) {
            Owner o = authService.authenticateOwner(email, password);
            if (o != null) {
                var dto = new AuthOwnerDTO(
                    o.getOwnerId(), o.getName(), o.getEmail(), o.getPhone(), o.getAddress()
                );
                ok.put("userType", "owner");
                ok.put("user", dto);
                return ResponseEntity.ok(ok);
            }
        } else if ("tenant".equalsIgnoreCase(role)) {
            Tenant t = authService.authenticateTenant(email, password);
            if (t != null) {
                var dto = new AuthTenantDTO(
                    t.getTenantId(), t.getName(), t.getEmail(), t.getRoomNumber(),
                    t.getOwner() != null ? t.getOwner().getOwnerId() : null
                );
                ok.put("userType", "tenant");
                ok.put("user", dto);
                return ResponseEntity.ok(ok);
            }
        }

        Map<String, Object> fail = new HashMap<>();
        fail.put("success", false);
        fail.put("message", "Invalid email, password, or role");
        return ResponseEntity.status(401).body(fail);

    } catch (Exception e) {
        Map<String, Object> err = new HashMap<>();
        err.put("success", false);
        err.put("message", "Authentication failed: " + e.getMessage());
        return ResponseEntity.status(500).body(err);
    }
}

}