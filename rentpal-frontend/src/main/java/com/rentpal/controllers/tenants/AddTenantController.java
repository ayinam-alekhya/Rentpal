package com.rentpal.controllers.tenants;

import com.rentpal.dto.TenantDTO;
import com.rentpal.service.TenantService;
import com.rentpal.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.rentpal.controllers.tenants.Tenant;


import java.io.IOException;

public class AddTenantController {

    @FXML private TextField tenantNameField;
    @FXML private TextField tenantEmailField;
    @FXML private TextField tenantContactField;
    @FXML private TextField tenantUnitField;   // room number
    @FXML private TextField tenantRentField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private PasswordField passwordField;

    private TenantsController tenantsController;
    private TenantService tenantService = new TenantService();

    public void setTenantsController(TenantsController controller) {
        this.tenantsController = controller;
    }

    @FXML
    private void handleAddTenant() {
        String name = tenantNameField.getText().trim();
        String email = tenantEmailField.getText().trim();
        String contact = tenantContactField.getText().trim();
        String unit = tenantUnitField.getText().trim();       // e.g., "A1-101"
        String rentText = tenantRentField.getText().trim();
        String status = statusComboBox.getValue();
        String password = passwordField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || contact.isEmpty() ||
            unit.isEmpty() || rentText.isEmpty() || status == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill all fields!").showAndWait();
            return;
        }
        if (password.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Password cannot be empty!").showAndWait();
            return;
        }

        Double rentAmount;
        try {
            rentAmount = Double.parseDouble(rentText);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Rent must be a valid number!").showAndWait();
            return;
        }

        // ✅ Ensure owner is logged in
        var session = SessionManager.getInstance();
        if (!session.isOwner() || session.getCurrentOwner() == null) {
            new Alert(Alert.AlertType.ERROR, "Owner context missing. Please log in as an owner.").showAndWait();
            return;
        }

        Long ownerId = session.getCurrentOwner().getOwnerId();
        if (ownerId == null) {
            new Alert(Alert.AlertType.ERROR, "Owner ID is missing in session.").showAndWait();
            return;
        }

        try {
            // Build the request DTO
            com.rentpal.dto.CreateTenantRequest req = new com.rentpal.dto.CreateTenantRequest();
            req.setName(name);
            req.setEmail(email);
            req.setPhone(contact);
            req.setRoomNumber(unit);      // String like “A1-101”
            req.setRentAmount(rentAmount);
            req.setPassword(password);
            req.setOwnerId(ownerId);      // ✅ this goes to CreateTenantRequest

            // POST to backend
            TenantDTO created = tenantService.createTenant(req);

            new Alert(Alert.AlertType.INFORMATION, "Tenant added successfully!").showAndWait();

            if (tenantsController != null) {
                tenantsController.loadTenantsForOwner();
            }

            Stage stage = (Stage) tenantNameField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to add tenant: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) tenantNameField.getScene().getWindow();
        stage.close();
    }
}
