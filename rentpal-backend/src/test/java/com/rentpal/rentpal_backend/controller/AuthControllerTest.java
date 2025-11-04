package com.rentpal.rentpal_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentpal.rentpal_backend.model.Owner;
import com.rentpal.rentpal_backend.model.Tenant;
import com.rentpal.rentpal_backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private Owner testOwner;
    private Tenant testTenant;

    @BeforeEach
    public void setUp() {
        // Create test owner
        testOwner = new Owner();
        testOwner.setOwnerId(1L);
        testOwner.setName("Test Owner");
        testOwner.setEmail("owner@test.com");
        testOwner.setPhone("1234567890");
        testOwner.setAddress("Test Address");

        // Create test tenant
        testTenant = new Tenant();
        testTenant.setTenantId(1L);
        testTenant.setName("Test Tenant");
        testTenant.setEmail("tenant@test.com");
        testTenant.setPhone("0987654321");
        testTenant.setRoomNumber("A101");
        testTenant.setRentAmount(1000.0);
        testTenant.setStatus("Active");
    }

    @Test
    public void testLoginSuccessAsOwner() throws Exception {
        // Mock the authService to return an owner
        when(authService.authenticateOwner(anyString(), anyString())).thenReturn(testOwner);
        when(authService.authenticateTenant(anyString(), anyString())).thenReturn(null);

        // Prepare login credentials
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "owner@test.com");
        credentials.put("password", "password123");

        // Perform the request and verify the response
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userType").value("owner"))
                .andExpect(jsonPath("$.user.email").value("owner@test.com"));
    }

    @Test
    public void testLoginSuccessAsTenant() throws Exception {
        // Mock the authService to return a tenant
        when(authService.authenticateOwner(anyString(), anyString())).thenReturn(null);
        when(authService.authenticateTenant(anyString(), anyString())).thenReturn(testTenant);

        // Prepare login credentials
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "tenant@test.com");
        credentials.put("password", "password123");

        // Perform the request and verify the response
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userType").value("tenant"))
                .andExpect(jsonPath("$.user.email").value("tenant@test.com"));
    }

    @Test
    public void testLoginFailure() throws Exception {
        // Mock the authService to return null for both owner and tenant
        when(authService.authenticateOwner(anyString(), anyString())).thenReturn(null);
        when(authService.authenticateTenant(anyString(), anyString())).thenReturn(null);

        // Prepare login credentials
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "nonexistent@test.com");
        credentials.put("password", "wrongpassword");

        // Perform the request and verify the response
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}