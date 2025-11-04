package com.rentpal.controllers.payments;

import javafx.beans.property.*;

public class Payment {

    private final StringProperty date;
    private final StringProperty tenant;
    private final LongProperty tenantId;
    private final DoubleProperty amount;
    private final StringProperty status;
    private final StringProperty method;

    public Payment(String date, String tenant, Long tenantId, double amount, String status, String method) {
        this.date = new SimpleStringProperty(date);
        this.tenant = new SimpleStringProperty(tenant);
        this.tenantId = new SimpleLongProperty(tenantId);
        this.amount = new SimpleDoubleProperty(amount);
        this.status = new SimpleStringProperty(status);
        this.method = new SimpleStringProperty(method);
    }

    public StringProperty dateProperty() { return date; }
    public StringProperty tenantProperty() { return tenant; }
    public LongProperty tenantIdProperty() { return tenantId; }
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty statusProperty() { return status; }
    public StringProperty methodProperty() { return method; }

    public String getDate() { return date.get(); }
    public String getTenant() { return tenant.get(); }
    public Long getTenantId() { return tenantId.get(); }
    public double getAmount() { return amount.get(); }
    public String getStatus() { return status.get(); }
    public String getMethod() { return method.get(); }

    public void setDate(String date) { this.date.set(date); }
    public void setTenant(String tenant) { this.tenant.set(tenant); }
    public void setTenantId(Long tenantId) { this.tenantId.set(tenantId); }
    public void setAmount(double amount) { this.amount.set(amount); }
    public void setStatus(String status) { this.status.set(status); }
    public void setMethod(String method) { this.method.set(method); }
}