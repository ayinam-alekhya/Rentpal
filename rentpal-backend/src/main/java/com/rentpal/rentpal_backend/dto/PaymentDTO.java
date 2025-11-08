package com.rentpal.rentpal_backend.dto;

import java.time.LocalDate;

public class PaymentDTO {
    private Long paymentId;
    private Long tenantId;      
    private String tenantName;  
    private double amount;
    private LocalDate paymentDate; // serialize as "yyyy-MM-dd" by default
    private String status;
    private String modeOfPayment;

    public PaymentDTO() {} // <-- Jackson needs no-args

    public PaymentDTO(Long paymentId, Long tenantId, String tenantName,
                      double amount, LocalDate paymentDate,
                      String status, String modeOfPayment) {
        this.paymentId = paymentId;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
        this.modeOfPayment = modeOfPayment;
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getModeOfPayment() { return modeOfPayment; }
    public void setModeOfPayment(String modeOfPayment) { this.modeOfPayment = modeOfPayment; }
}
