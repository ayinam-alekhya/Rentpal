package com.rentpal.rentpal_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentpal.rentpal_backend.dto.OwnerSummaryDTO;
import com.rentpal.rentpal_backend.model.Owner;
import com.rentpal.rentpal_backend.service.OwnerService;
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

@WebMvcTest(OwnerController.class)
public class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Owner testOwner;
    private OwnerSummaryDTO testOwnerSummary;

    @BeforeEach
    public void setUp() {
        testOwner = new Owner();
        testOwner.setOwnerId(1L);
        testOwner.setName("Test Owner");
        testOwner.setEmail("owner@test.com");
        testOwner.setPhone("1234567890");
        testOwner.setAddress("Test Address");
        
        testOwnerSummary = new OwnerSummaryDTO(1L, "Test Owner", "owner@test.com", "1234567890");
    }

    @Test
    public void testGetAllOwners() throws Exception {
        // Create a list of owner summaries
        List<OwnerSummaryDTO> ownerSummaries = Arrays.asList(testOwnerSummary);
        
        // Mock the ownerService
        when(ownerService.getAllOwners()).thenReturn(ownerSummaries);

        // Perform the request and verify the response
        mockMvc.perform(get("/api/owners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("owner@test.com"));
    }

    @Test
    public void testCreateOwner() throws Exception {
        // Create a simple owner object for JSON serialization
        Owner simpleOwner = new Owner();
        simpleOwner.setName("Test Owner");
        simpleOwner.setEmail("owner@test.com");
        simpleOwner.setPhone("1234567890");
        simpleOwner.setAddress("Test Address");
        
        // Mock the ownerService
        when(ownerService.createOwner(any(Owner.class))).thenReturn(testOwner);

        // Perform the request and verify the response
        mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(simpleOwner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("owner@test.com"));
    }

    @Test
    public void testGetOwnerById() throws Exception {
        // Mock the ownerService
        when(ownerService.getOwnerById(1L)).thenReturn(testOwner);

        // Perform the request and verify the response
        mockMvc.perform(get("/api/owners/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("owner@test.com"));
    }
}