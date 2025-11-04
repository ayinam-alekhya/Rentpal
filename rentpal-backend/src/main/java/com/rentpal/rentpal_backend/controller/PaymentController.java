package com.rentpal.rentpal_backend.controller;

import com.rentpal.rentpal_backend.dto.CreatePaymentRequest;
import com.rentpal.rentpal_backend.model.Payment;
import com.rentpal.rentpal_backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/tenant/{tenantId}")
    public List<Payment> getPaymentsByTenant(@PathVariable Long tenantId) {
        return paymentService.getPaymentsByTenant(tenantId);
    }
}