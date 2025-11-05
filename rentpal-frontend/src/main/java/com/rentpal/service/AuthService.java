package com.rentpal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentpal.dto.OwnerDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.dto.UserRegistrationDTO;
import com.rentpal.utils.ApiUtil;

import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ---------- LOGIN ----------
    public AuthResult authenticate(String email, String password, String role) throws Exception {
        System.out.println("AuthService.authenticate() called with email=" + email + ", role=" + role);

        // build request body
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", email);
        loginData.put("password", password);
        loginData.put("role", role);

        String requestJson = MAPPER.writeValueAsString(loginData);

        // call backend (BASE must match your ApiUtil)
        String response = ApiUtil.post("/auth/login", requestJson);

        // defensive guard: ensure JSON
        String trimmed = response == null ? "" : response.trim();
        System.out.println("AUTH len=" + trimmed.length() + " head=" +
                (trimmed.length() > 160 ? trimmed.substring(0, 160) : trimmed));
        if (!(trimmed.startsWith("{") || trimmed.startsWith("["))) {
            throw new Exception("Non-JSON response from /auth/login: " +
                    (trimmed.length() > 300 ? trimmed.substring(0, 300) + "…" : trimmed));
        }

        // parse
        JsonNode root = MAPPER.readTree(trimmed);
        boolean success = root.path("success").asBoolean(false);
        if (!success) {
            String msg = root.path("message").asText("Authentication failed");
            System.out.println("Authentication failed: " + msg);
            return new AuthResult(false, null, null, null);
        }

        String userType = root.path("userType").asText("");
        JsonNode userNode = root.path("user");

        if ("owner".equalsIgnoreCase(userType)) {
            OwnerDTO owner = MAPPER.treeToValue(userNode, OwnerDTO.class);
            return new AuthResult(true, "owner", owner, null);
        } else if ("tenant".equalsIgnoreCase(userType)) {
            TenantDTO tenant = MAPPER.treeToValue(userNode, TenantDTO.class);
            return new AuthResult(true, "tenant", null, tenant);
        } else {
            return new AuthResult(false, null, null, null);
        }
    }

    // ---------- REGISTER ----------
    public RegistrationResult register(UserRegistrationDTO registrationDTO) throws Exception {
        System.out.println("AuthService.register() " + registrationDTO.getEmail() +
                " role=" + registrationDTO.getRole());

        String requestJson = MAPPER.writeValueAsString(registrationDTO);
        String response = ApiUtil.post("/auth/register", requestJson);

        String trimmed = response == null ? "" : response.trim();
        if (!(trimmed.startsWith("{") || trimmed.startsWith("["))) {
            throw new Exception("Non-JSON response from /auth/register: " +
                    (trimmed.length() > 300 ? trimmed.substring(0, 300) + "…" : trimmed));
        }

        JsonNode root = MAPPER.readTree(trimmed);
        boolean success = root.path("success").asBoolean(false);
        String message = root.path("message").asText("");

        if (!success) return new RegistrationResult(false, message, null, null, null);

        String role = registrationDTO.getRole();
        JsonNode userNode = root.path("user");

        if ("owner".equalsIgnoreCase(role)) {
            OwnerDTO owner = MAPPER.treeToValue(userNode, OwnerDTO.class);
            return new RegistrationResult(true, message, "owner", owner, null);
        } else if ("tenant".equalsIgnoreCase(role)) {
            TenantDTO tenant = MAPPER.treeToValue(userNode, TenantDTO.class);
            return new RegistrationResult(true, message, "tenant", null, tenant);
        } else {
            return new RegistrationResult(true, message, null, null, null);
        }
    }

    // ---------- POJOs ----------
    public static class AuthResult {
        private final boolean success;
        private final String userType;
        private final OwnerDTO owner;
        private final TenantDTO tenant;

        public AuthResult(boolean success, String userType, OwnerDTO owner, TenantDTO tenant) {
            this.success = success;
            this.userType = userType;
            this.owner = owner;
            this.tenant = tenant;
        }
        public boolean isSuccess() { return success; }
        public String getUserType() { return userType; }
        public OwnerDTO getOwner() { return owner; }
        public TenantDTO getTenant() { return tenant; }
    }

    public static class RegistrationResult {
        private final boolean success;
        private final String message;
        private final String userType;
        private final OwnerDTO owner;
        private final TenantDTO tenant;

        public RegistrationResult(boolean success, String message, String userType, OwnerDTO owner, TenantDTO tenant) {
            this.success = success;
            this.message = message;
            this.userType = userType;
            this.owner = owner;
            this.tenant = tenant;
        }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getUserType() { return userType; }
        public OwnerDTO getOwner() { return owner; }
        public TenantDTO getTenant() { return tenant; }
    }
}
