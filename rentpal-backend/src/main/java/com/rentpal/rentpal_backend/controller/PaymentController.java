package com.rentpal.rentpal_backend.controller;

import com.rentpal.rentpal_backend.dto.CreatePaymentRequest;
import com.rentpal.rentpal_backend.model.Payment;
import com.rentpal.rentpal_backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import com.rentpal.rentpal_backend.dto.PaymentDTO;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// com.rentpal.rentpal_backend.controller.PaymentController
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // CREATE → return DTO
    @PostMapping("/{tenantId}")
    public PaymentDTO createPayment(@PathVariable Long tenantId,
                                    @RequestBody CreatePaymentRequest req) {
        Payment saved = paymentService.createPayment(tenantId, req);
        return paymentService.toDTO(saved);
    }

    // ALL → DTO list
    @GetMapping
    public List<PaymentDTO> getAllPayments() {
        return paymentService.getAllPayments()
                .stream().map(paymentService::toDTO).toList();
    }

    // BY OWNER → DTO list
    @GetMapping("/owner/{ownerId}")
    public List<PaymentDTO> getPaymentsByOwner(@PathVariable Long ownerId) {
        return paymentService.getPaymentsByOwner(ownerId)
                .stream().map(paymentService::toDTO).toList();
    }

    // BY TENANT (+filters) → DTO list
    @GetMapping("/tenant/{tenantId}")
    public List<PaymentDTO> getPaymentsByTenant(
            @PathVariable Long tenantId,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return paymentService.getPaymentsByTenant(tenantId, status, from, to)
                .stream().map(paymentService::toDTO).toList();
    }
    @PutMapping("/{paymentId}/status")
    public PaymentDTO updateStatus(@PathVariable Long paymentId,
                                @RequestBody Map<String,String> body) {
        String status = body.getOrDefault("status", "PENDING");
        return paymentService.updateStatus(paymentId, status); // returns DTO
    }
}
