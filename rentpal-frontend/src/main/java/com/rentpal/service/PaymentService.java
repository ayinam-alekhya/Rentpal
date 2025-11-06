package com.rentpal.service;

import com.rentpal.dto.PaymentDTO;
import com.rentpal.utils.ApiUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

public class PaymentService {

    public List<PaymentDTO> getAllPayments() throws Exception {
        String json = ApiUtil.get("/payments");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<PaymentDTO>>() {});
    }

    public List<PaymentDTO> getPaymentsByTenant(Long tenantId) throws Exception {
        String json = ApiUtil.get("/payments/tenant/" + tenantId);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<PaymentDTO>>() {});
    }

    /** NEW: filter by status and optional date range (YYYY-MM-DD) */
    public List<PaymentDTO> getPaymentsByTenant(Long tenantId, String status,
                                                LocalDate from, LocalDate to) throws Exception {
        StringBuilder url = new StringBuilder("/payments/tenant/").append(tenantId);

        // normalize status
        String normalized = (status == null || status.isBlank()) ? "ALL" : status.toUpperCase();
        url.append("?status=").append(URLEncoder.encode(normalized, StandardCharsets.UTF_8));

        if (from != null) {
            url.append("&from=").append(URLEncoder.encode(from.toString(), StandardCharsets.UTF_8));
        }
        if (to != null) {
            url.append("&to=").append(URLEncoder.encode(to.toString(), StandardCharsets.UTF_8));
        }

        String json = ApiUtil.get(url.toString());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<PaymentDTO>>() {});
    }

    public PaymentDTO createPayment(Long tenantId, PaymentDTO payment) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(payment);
        System.out.println("Sending payment data: " + jsonInput);
        String jsonOutput = ApiUtil.post("/payments/" + tenantId, jsonInput);
        return mapper.readValue(jsonOutput, PaymentDTO.class);
    }
}
