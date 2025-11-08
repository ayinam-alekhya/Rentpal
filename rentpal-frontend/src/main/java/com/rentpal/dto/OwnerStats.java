package com.rentpal.dto;

import java.math.BigDecimal;

public class OwnerStats {
    private long tenantCount;
    private long paymentCount;     // total payments for this owner
    private long complaintCount;   // optional if your backend fills it
    private BigDecimal totalCollected;
    private BigDecimal totalPending;

    public long getTenantCount() { return tenantCount; }
    public void setTenantCount(long tenantCount) { this.tenantCount = tenantCount; }
    public long getPaymentCount() { return paymentCount; }
    public void setPaymentCount(long paymentCount) { this.paymentCount = paymentCount; }
    public long getComplaintCount() { return complaintCount; }
    public void setComplaintCount(long complaintCount) { this.complaintCount = complaintCount; }
    public BigDecimal getTotalCollected() { return totalCollected; }
    public void setTotalCollected(BigDecimal totalCollected) { this.totalCollected = totalCollected; }
    public BigDecimal getTotalPending() { return totalPending; }
    public void setTotalPending(BigDecimal totalPending) { this.totalPending = totalPending; }
}
