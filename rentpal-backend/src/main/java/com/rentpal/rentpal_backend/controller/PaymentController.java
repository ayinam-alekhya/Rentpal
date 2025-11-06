package com.rentpal.rentpal_backend.controller;

import com.rentpal.rentpal_backend.dto.CreatePaymentRequest;
import com.rentpal.rentpal_backend.model.Payment;
import com.rentpal.rentpal_backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/{tenantId}")
    public Payment createPayment(@PathVariable Long tenantId, @RequestBody CreatePaymentRequest paymentRequest) {
        return paymentService.createPayment(tenantId, paymentRequest);
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    // GET payments for a tenant with optional filters
    @GetMapping("/tenant/{tenantId}")
    public List<Payment> getPaymentsByTenant(
            @PathVariable Long tenantId,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return paymentService.getPaymentsByTenant(tenantId, status, from, to);
    }

}