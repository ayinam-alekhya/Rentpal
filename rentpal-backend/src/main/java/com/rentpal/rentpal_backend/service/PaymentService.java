package com.rentpal.rentpal_backend.service;

import com.rentpal.rentpal_backend.dto.CreatePaymentRequest;
import com.rentpal.rentpal_backend.dto.PaymentDTO;
import com.rentpal.rentpal_backend.exception.ResourceNotFoundException;
import com.rentpal.rentpal_backend.model.Payment;
import com.rentpal.rentpal_backend.model.Tenant;
import com.rentpal.rentpal_backend.repository.PaymentRepository;
import com.rentpal.rentpal_backend.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TenantRepository tenantRepository;

    public PaymentDTO toDTO(Payment p) {
        return new PaymentDTO(
            p.getPaymentId(),
            (p.getTenant() != null ? p.getTenant().getTenantId() : null),
            (p.getTenant() != null ? p.getTenant().getName() : null),
            p.getAmount(),
            (p.getPaymentDate() != null ? p.getPaymentDate().toLocalDate() : null),
            p.getStatus(),
            p.getModeOfPayment()
        );
    }

    public Payment createPayment(Long tenantId, Payment payment) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + tenantId));

        payment.setTenant(tenant);
        payment.setPaymentDate(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        // Update tenant's remaining rent
        double totalPaid = tenant.getPayments().stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        double remaining = tenant.getRentAmount() - totalPaid;
        tenant.setRemainingRent(Math.max(remaining, 0)); // never go below 0

        // Update payment status
        if (remaining <= 0)
            tenant.setPaymentStatus("Paid");
        else if (remaining < tenant.getRentAmount())
            tenant.setPaymentStatus("Partial");
        else
            tenant.setPaymentStatus("Unpaid");

        tenantRepository.save(tenant);

        return savedPayment;
    }

    public Payment createPayment(Long tenantId, CreatePaymentRequest paymentRequest) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + tenantId));

        Payment payment = new Payment();
        payment.setTenant(tenant);
        payment.setAmount(paymentRequest.getAmount());
        payment.setStatus(paymentRequest.getStatus());
        payment.setModeOfPayment(paymentRequest.getModeOfPayment());
        
        // Convert string date to LocalDateTime if provided, otherwise use current time
        if (paymentRequest.getPaymentDate() != null && !paymentRequest.getPaymentDate().isEmpty()) {
            try {
                // Try to parse as LocalDate first (from frontend)
                LocalDate localDate = LocalDate.parse(paymentRequest.getPaymentDate());
                payment.setPaymentDate(localDate.atStartOfDay());
            } catch (DateTimeParseException e) {
                try {
                    // Try to parse as LocalDateTime (ISO format)
                    payment.setPaymentDate(LocalDateTime.parse(paymentRequest.getPaymentDate()));
                } catch (DateTimeParseException ex) {
                    // Default to current time if parsing fails
                    payment.setPaymentDate(LocalDateTime.now());
                }
            }
        } else {
            payment.setPaymentDate(LocalDateTime.now());
        }

        Payment savedPayment = paymentRepository.save(payment);

        // Update tenant's remaining rent
        double totalPaid = tenant.getPayments().stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        double remaining = tenant.getRentAmount() - totalPaid;
        tenant.setRemainingRent(Math.max(remaining, 0)); // never go below 0

        // Update payment status
        if (remaining <= 0)
            tenant.setPaymentStatus("Paid");
        else if (remaining < tenant.getRentAmount())
            tenant.setPaymentStatus("Partial");
        else
            tenant.setPaymentStatus("Unpaid");

        tenantRepository.save(tenant);

        return savedPayment;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public List<Payment> getPaymentsByOwner(Long ownerId) {
        return paymentRepository.findByTenant_Owner_OwnerId(ownerId);
    }

    public List<Payment> getPaymentsByTenant(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + tenantId));
        return tenant.getPayments();
    }
    public List<Payment> getPaymentsByTenant(Long tenantId, String status,
                                         LocalDate from, LocalDate to) {

        // Normalize range to full-day boundaries if provided
        LocalDateTime fromDt = (from == null) ? null : from.atStartOfDay();
        LocalDateTime toDt   = (to   == null) ? null : to.atTime(23, 59, 59);

        boolean hasRange = (fromDt != null || toDt != null);
        boolean hasStatus = (status != null && !"ALL".equalsIgnoreCase(status));

        if (!hasRange && !hasStatus) {
            return paymentRepository.findByTenant_TenantId(tenantId);
        }

        if (!hasRange) {
            return paymentRepository.findByTenant_TenantIdAndStatus(tenantId, status);
        }

        // when only one bound is present, clip the other to extremes
        if (fromDt == null) fromDt = LocalDateTime.of(1970, 1, 1, 0, 0);
        if (toDt == null)   toDt   = LocalDateTime.of(2999,12,31,23,59,59);

        if (!hasStatus) {
            return paymentRepository.findByTenant_TenantIdAndPaymentDateBetween(tenantId, fromDt, toDt);
        }

        return paymentRepository.findByTenant_TenantIdAndStatusAndPaymentDateBetween(
                tenantId, status, fromDt, toDt);
    }
    public List<PaymentDTO> getAllPaymentsAsDTO() {
        return paymentRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<PaymentDTO> getPaymentsByOwnerAsDTO(Long ownerId) {
        return paymentRepository.findByTenant_Owner_OwnerId(ownerId).stream().map(this::toDTO).toList();
    }

    public List<PaymentDTO> getPaymentsByTenantAsDTO(Long tenantId) {
        return getPaymentsByTenant(tenantId).stream().map(this::toDTO).toList();
    }

    public List<PaymentDTO> getPaymentsByTenantAsDTO(Long tenantId, String status,
                                                    LocalDate from, LocalDate to) {
        return getPaymentsByTenant(tenantId, status, from, to).stream()
                .map(this::toDTO).toList();
    }

    @Transactional
    public PaymentDTO updateStatus(Long paymentId, String status) {
        Payment p = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        p.setStatus(status);
        paymentRepository.save(p);
        return toDTO(p);
    }

}