package com.rentpal.rentpal_backend.dto;

import java.time.LocalDate;

public class PaymentDTO {
    private Long paymentId;
    private double amount;
    private LocalDate paymentDate;
    private String status;
    private String modeOfPayment;

    public PaymentDTO(Long paymentId, double amount, LocalDate paymentDate,
                      String status, String modeOfPayment) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
        this.modeOfPayment = modeOfPayment;
    }

    // Getters
    public Long getPaymentId() { return paymentId; }
    public double getAmount() { return amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public String getStatus() { return status; }
    public String getModeOfPayment() { return modeOfPayment; }
}
