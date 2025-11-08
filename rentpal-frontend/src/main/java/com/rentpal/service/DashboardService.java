package com.rentpal.service;

import com.rentpal.dto.ComplaintDTO;
import com.rentpal.dto.PaymentDTO;
import com.rentpal.dto.TenantDTO;

import java.util.List;

public class DashboardService {
    private final TenantService tenantService = new TenantService();
    private final PaymentService paymentService = new PaymentService();
    private final ComplaintService complaintService = new ComplaintService();

    public int getTotalTenants(Long ownerId) throws Exception {
        if (ownerId == null) return 0;
        List<TenantDTO> tenants = tenantService.getTenantsForOwner(ownerId);
        return tenants.size();
    }

    public int getPendingPayments(Long ownerId) throws Exception {
        if (ownerId == null) return 0;
        return (int) paymentService.getPaymentsByOwner(ownerId).stream()
                .map(PaymentDTO::getStatus)
                .filter(s -> s != null)
                .map(String::toUpperCase)
                .filter(s -> !s.equals("PAID") && !s.equals("COMPLETED") && !s.equals("FULL"))
                .count();
    }

    public int getActiveComplaints(Long ownerId) throws Exception {
        if (ownerId == null) return 0;
        return (int) complaintService.getComplaintsByOwner(ownerId).stream()
                .map(ComplaintDTO::getStatus)
                .filter(s -> s != null)
                .map(String::toUpperCase)
                .filter(s -> !s.equals("RESOLVED"))
                .count();
    }

    public int getVacantUnits(Long ownerId) throws Exception {
        if (ownerId == null) return 0;
        return (int) tenantService.getTenantsByOwner(ownerId).stream()
                .filter(t -> t.getRoomNumber() == null || t.getRoomNumber().isBlank()
                        || "INACTIVE".equalsIgnoreCase(t.getPaymentStatus()))
                .count();
    }

    /** For the bar chart */
    public List<PaymentDTO> getPaymentsForOwner(Long ownerId) throws Exception {
        if (ownerId == null) return List.of();
        return paymentService.getPaymentsByOwner(ownerId);
    }
}
