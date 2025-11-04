package com.rentpal.service;

import com.rentpal.dto.PaymentDTO;
import com.rentpal.utils.ApiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public class PaymentService {
    
    public List<PaymentDTO> getAllPayments() throws Exception {
        String json = ApiUtil.get("/payments");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<PaymentDTO>>(){});
    }
    
    public List<PaymentDTO> getPaymentsByTenant(Long tenantId) throws Exception {
        String json = ApiUtil.get("/payments/tenant/" + tenantId);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<PaymentDTO>>(){});
    }
    
    public PaymentDTO createPayment(Long tenantId, PaymentDTO payment) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        // Log the JSON being sent for debugging
        String jsonInput = mapper.writeValueAsString(payment);
        System.out.println("Sending payment data: " + jsonInput);
        String jsonOutput = ApiUtil.post("/payments/" + tenantId, jsonInput);
        return mapper.readValue(jsonOutput, PaymentDTO.class);
    }
}



