package com.rentpal.service;

import com.rentpal.dto.ComplaintDTO;
import com.rentpal.dto.PaymentDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.utils.ApiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public class DashboardService {
    
    private TenantService tenantService = new TenantService();
    private PaymentService paymentService = new PaymentService();
    private ComplaintService complaintService = new ComplaintService();
    
    /**
     * Get total number of tenants for the current owner
     */
    public int getTotalTenants(Long ownerId) throws Exception {
        // For now, we'll get all tenants since the backend doesn't have owner-specific tenant count
        // In a real implementation, this would be filtered by owner
        List<TenantDTO> tenants = tenantService.getAllTenants();
        return tenants.size();
    }
    
    /**
     * Get number of pending payments
     */
    public int getPendingPayments() throws Exception {
        List<PaymentDTO> payments = paymentService.getAllPayments();
        // Count payments with status not equal to "Completed" or "Paid"
        return (int) payments.stream()
                .filter(p -> !"Completed".equalsIgnoreCase(p.getStatus()) && 
                           !"Paid".equalsIgnoreCase(p.getStatus()))
                .count();
    }
    
    /**
     * Get number of active complaints
     */
    public int getActiveComplaints(Long ownerId) throws Exception {
        List<ComplaintDTO> complaints = complaintService.getComplaintsByOwner(ownerId);
        // Count complaints with status not equal to "Resolved"
        return (int) complaints.stream()
                .filter(c -> !"Resolved".equalsIgnoreCase(c.getStatus()))
                .count();
    }
    
    /**
     * Get number of vacant units
     */
    public int getVacantUnits() throws Exception {
        List<TenantDTO> tenants = tenantService.getAllTenants();
        // Count tenants with status "Inactive" or without room numbers
        return (int) tenants.stream()
                .filter(t -> "Inactive".equalsIgnoreCase(t.getStatus()) || 
                           t.getRoomNumber() == null || 
                           t.getRoomNumber().isEmpty())
                .count();
    }
    
    /**
     * Get payment data for chart
     */
    public List<PaymentDTO> getPaymentData() throws Exception {
        return paymentService.getAllPayments();
    }
}