package com.rentpal.rentpal_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentpal.rentpal_backend.dto.TenantSummaryDTO;
import com.rentpal.rentpal_backend.model.Tenant;
import com.rentpal.rentpal_backend.service.TenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TenantController.class)
public class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TenantService tenantService;

    @Autowired
    private ObjectMapper objectMapper;

    private Tenant testTenant;
    private TenantSummaryDTO testTenantSummary;

    @BeforeEach
    public void setUp() {
        testTenant = new Tenant();
        testTenant.setTenantId(1L);
        testTenant.setName("Test Tenant");
        testTenant.setEmail("tenant@test.com");
        testTenant.setPhone("0987654321");
        testTenant.setRoomNumber("A101");
        testTenant.setRentAmount(1000.0);
        testTenant.setStatus("Active");
        
        testTenantSummary = new TenantSummaryDTO(1L, "Test Tenant", "A101", 0.0, "Unpaid");
    }

    @Test
    public void testGetAllTenants() throws Exception {
        // Create a list of tenant summaries
        List<TenantSummaryDTO> tenantSummaries = Arrays.asList(testTenantSummary);
        
        // Mock the tenantService
        when(tenantService.getAllTenants()).thenReturn(tenantSummaries);

        // Perform the request and verify the response
        mockMvc.perform(get("/api/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Tenant"));
    }

    @Test
    public void testCreateTenant() throws Exception {
        // Create a simple tenant object for JSON serialization
        Tenant simpleTenant = new Tenant();
        simpleTenant.setName("Test Tenant");
        simpleTenant.setEmail("tenant@test.com");
        simpleTenant.setPhone("0987654321");
        simpleTenant.setRoomNumber("A101");
        simpleTenant.setRentAmount(1000.0);
        simpleTenant.setStatus("Active");
        simpleTenant.setRemainingRent(0.0);
        simpleTenant.setPaymentStatus("Unpaid");
        
        // Mock the tenantService
        when(tenantService.createTenant(any(Tenant.class))).thenReturn(testTenant);

        // Perform the request and verify the response
        mockMvc.perform(post("/api/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(simpleTenant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("tenant@test.com"));
    }

    @Test
    public void testGetTenantById() throws Exception {
        // Mock the tenantService
        when(tenantService.getTenantById(1L)).thenReturn(testTenant);

        // Perform the request and verify the response
        mockMvc.perform(get("/api/tenants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("tenant@test.com"));
    }
}