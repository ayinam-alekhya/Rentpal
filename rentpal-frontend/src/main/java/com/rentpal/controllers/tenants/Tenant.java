package com.rentpal.controllers.tenants;

import javafx.beans.property.*;

public class Tenant {

    private final LongProperty tenantId;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty contact;
    private final DoubleProperty rent;
    private final StringProperty status;
    private final StringProperty unit;

    // ✅ Default constructor (needed for FXML & object creation)
    public Tenant() {
        this.tenantId = new SimpleLongProperty(0);
        this.name = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
        this.contact = new SimpleStringProperty("");
        this.rent = new SimpleDoubleProperty(0.0);
        this.status = new SimpleStringProperty("Inactive");
        this.unit = new SimpleStringProperty("");
    }

    // ✅ Full constructor for TableView dummy data / backend mapping
    public Tenant(String name, String unit, String contact, double rent, String status) {
        this.tenantId = new SimpleLongProperty(0);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(""); // optional placeholder
        this.contact = new SimpleStringProperty(contact);
        this.rent = new SimpleDoubleProperty(rent);
        this.status = new SimpleStringProperty(status);
        this.unit = new SimpleStringProperty(unit);
    }

    // ✅ Constructor with tenant ID
    public Tenant(Long tenantId, String name, String unit, String contact, double rent, String status) {
        this.tenantId = new SimpleLongProperty(tenantId);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(""); // optional placeholder
        this.contact = new SimpleStringProperty(contact);
        this.rent = new SimpleDoubleProperty(rent);
        this.status = new SimpleStringProperty(status);
        this.unit = new SimpleStringProperty(unit);
    }

    // ✅ Getters
    public Long getTenantId() { return tenantId.get(); }
    public String getName() { return name.get(); }
    public String getEmail() { return email.get(); }
    public String getContact() { return contact.get(); }
    public double getRent() { return rent.get(); }
    public String getStatus() { return status.get(); }
    public String getUnit() { return unit.get(); }

    // ✅ Setters
    public void setTenantId(Long tenantId) { this.tenantId.set(tenantId); }
    public void setName(String name) { this.name.set(name); }
    public void setEmail(String email) { this.email.set(email); }
    public void setContact(String contact) { this.contact.set(contact); }
    public void setRent(double rent) { this.rent.set(rent); }
    public void setStatus(String status) { this.status.set(status); }
    public void setUnit(String unit) { this.unit.set(unit); }

    // ✅ Property methods (required for TableView bindings)
    public LongProperty tenantIdProperty() { return tenantId; }
    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public StringProperty contactProperty() { return contact; }
    public DoubleProperty rentProperty() { return rent; }
    public StringProperty statusProperty() { return status; }
    public StringProperty unitProperty() { return unit; }
}