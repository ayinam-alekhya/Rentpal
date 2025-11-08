package com.rentpal.rentpal_backend.dto;

import java.math.BigDecimal;

public class OwnerStatsDTO {

    private long tenantCount;
    private long paymentCount;
    private long complaintCount;
    private BigDecimal totalCollected;
    private BigDecimal totalPending;

    public long getTenantCount() {
        return tenantCount;
    }

    public void setTenantCount(long tenantCount) {
        this.tenantCount = tenantCount;
    }

    public long getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(long paymentCount) {
        this.paymentCount = paymentCount;
    }

    public long getComplaintCount() {
        return complaintCount;
    }

    public void setComplaintCount(long complaintCount) {
        this.complaintCount = complaintCount;
    }

    public BigDecimal getTotalCollected() {
        return totalCollected;
    }

    public void setTotalCollected(BigDecimal totalCollected) {
        this.totalCollected = totalCollected;
    }

    public BigDecimal getTotalPending() {
        return totalPending;
    }

    public void setTotalPending(BigDecimal totalPending) {
        this.totalPending = totalPending;
    }
}
