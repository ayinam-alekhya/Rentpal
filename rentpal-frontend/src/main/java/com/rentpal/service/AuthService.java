package com.rentpal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentpal.dto.OwnerDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.dto.UserRegistrationDTO;
import com.rentpal.utils.ApiUtil;
import com.rentpal.utils.SessionManager; 

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    
    public AuthResult authenticate(String email, String password, String role) throws Exception {
        try {
            System.out.println("AuthService.authenticate() called with email: " + email + ", role: " + role);
            
            // Prepare login data
            Map<String, String> loginData = new HashMap<>();
            loginData.put("email", email);
            loginData.put("password", password);
            loginData.put("role", role);
            
            // Convert to JSON
            ObjectMapper mapper = new ObjectMapper();
            String jsonInput = mapper.writeValueAsString(loginData);
            System.out.println("Sending JSON: " + jsonInput);
            
            // Make API call
            String jsonResponse = ApiUtil.post("/auth/login", jsonInput);
            System.out.println("Received response: " + jsonResponse);
            
            // Check if response is valid
            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                System.err.println("Empty response from server");
                return new AuthResult(false, null, null, null);
            }
            
            // Parse response
            JsonNode rootNode = mapper.readTree(jsonResponse);
            boolean success = rootNode.get("success").asBoolean();
            System.out.println("Authentication success: " + success);
            
            if (success) {
                String userType = rootNode.get("userType").asText();
                JsonNode userNode = rootNode.get("user");
                System.out.println("User type: " + userType);
                
                if ("owner".equals(userType)) {
                    OwnerDTO owner = mapper.treeToValue(userNode, OwnerDTO.class);
                    System.out.println("Owner authenticated: " + owner.getName());
                    return new AuthResult(true, "owner", owner, null);
                } else if ("tenant".equals(userType)) {
                    TenantDTO tenant = mapper.treeToValue(userNode, TenantDTO.class);
                    SessionManager.getInstance().setCurrentTenant(tenant);
                    System.out.println("Tenant authenticated: " + tenant.getName());
                    return new AuthResult(true, "tenant", null, tenant);
                }
            } else {
                // Get error message
                String message = rootNode.has("message") ? rootNode.get("message").asText() : "Authentication failed";
                System.out.println("Authentication failed: " + message);
            }
            
            return new AuthResult(false, null, null, null);
        } catch (Exception e) {
            System.err.println("Error in AuthService.authenticate(): " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Authentication failed: " + e.getMessage(), e);
        }
    }
    
    public RegistrationResult register(UserRegistrationDTO registrationDTO) throws Exception {
        try {
            System.out.println("AuthService.register() called with: " + registrationDTO.getName() + " (" + registrationDTO.getEmail() + ")");
            
            // Convert to JSON
            ObjectMapper mapper = new ObjectMapper();
            String jsonInput = mapper.writeValueAsString(registrationDTO);
            System.out.println("Sending registration JSON: " + jsonInput);
            
            // Make API call
            String jsonResponse = ApiUtil.post("/auth/register", jsonInput);
            System.out.println("Received registration response: " + jsonResponse);
            
            // Parse response
            JsonNode rootNode = mapper.readTree(jsonResponse);
            boolean success = rootNode.get("success").asBoolean();
            String message = rootNode.get("message").asText();
            System.out.println("Registration success: " + success + ", message: " + message);
            
            if (success) {
                JsonNode userNode = rootNode.get("user");
                
                if ("owner".equals(registrationDTO.getRole())) {
                    OwnerDTO owner = mapper.treeToValue(userNode, OwnerDTO.class);
                    System.out.println("Owner registered: " + owner.getName());
                    return new RegistrationResult(true, message, "owner", owner, null);
                } else if ("tenant".equals(registrationDTO.getRole())) {
                    TenantDTO tenant = mapper.treeToValue(userNode, TenantDTO.class);
                    System.out.println("Tenant registered: " + tenant.getName());
                    return new RegistrationResult(true, message, "tenant", null, tenant);
                }
            }
            
            return new RegistrationResult(false, message, null, null, null);
        } catch (Exception e) {
            System.err.println("Error in AuthService.register(): " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Registration failed: " + e.getMessage(), e);
        }
    }
    
    public static class AuthResult {
        private boolean success;
        private String userType;
        private OwnerDTO owner;
        private TenantDTO tenant;
        
        public AuthResult(boolean success, String userType, OwnerDTO owner, TenantDTO tenant) {
            this.success = success;
            this.userType = userType;
            this.owner = owner;
            this.tenant = tenant;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getUserType() { return userType; }
        public OwnerDTO getOwner() { return owner; }
        public TenantDTO getTenant() { return tenant; }
    }
    
    public static class RegistrationResult {
        private boolean success;
        private String message;
        private String userType;
        private OwnerDTO owner;
        private TenantDTO tenant;
        
        public RegistrationResult(boolean success, String message, String userType, OwnerDTO owner, TenantDTO tenant) {
            this.success = success;
            this.message = message;
            this.userType = userType;
            this.owner = owner;
            this.tenant = tenant;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getUserType() { return userType; }
        public OwnerDTO getOwner() { return owner; }
        public TenantDTO getTenant() { return tenant; }
    }
}