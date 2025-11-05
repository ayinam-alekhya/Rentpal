package com.rentpal.controllers.complaints;

import javafx.beans.property.*;

public class Complaint {
    private final IntegerProperty id;
    private final StringProperty tenant;
    private final StringProperty issue;
    private final StringProperty status;
    private final StringProperty date;

    public Complaint(int id, String tenant, String issue, String status, String date) {
        this.id = new SimpleIntegerProperty(id);
        this.tenant = new SimpleStringProperty(tenant);
        this.issue = new SimpleStringProperty(issue);
        this.status = new SimpleStringProperty(status);
        this.date = new SimpleStringProperty(date);
    }

    // --- properties
    public IntegerProperty idProperty() { return id; }
    public StringProperty tenantProperty() { return tenant; }
    public StringProperty issueProperty() { return issue; }
    public StringProperty statusProperty() { return status; }
    public StringProperty dateProperty() { return date; }

    // --- getters
    public int getId() { return id.get(); }
    public String getTenant() { return tenant.get(); }
    public String getIssue() { return issue.get(); }
    public String getStatus() { return status.get(); }
    public String getDate() { return date.get(); }

    // --- setters (add at least this one so status edits can commit)
    public void setStatus(String value) { this.status.set(value); }

    // (optional) if you later want to edit these columns too:
    public void setIssue(String value) { this.issue.set(value); }
    public void setTenant(String value) { this.tenant.set(value); }
    public void setDate(String value) { this.date.set(value); }
}
