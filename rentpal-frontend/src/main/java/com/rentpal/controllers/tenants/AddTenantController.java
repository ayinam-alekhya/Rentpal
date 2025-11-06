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
    @FXML private TextField tenantUnitField;
    @FXML private TextField tenantRentField;
    @FXML private ComboBox<String> statusComboBox;

    private TenantsController tenantsController; // Reference to parent controller
    private TenantService tenantService = new TenantService();
    private Long ownerId;

    public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
}

    // 👇 This setter will be called from TenantsController
    public void setTenantsController(TenantsController controller) {
        this.tenantsController = controller;
    }

   @FXML
    private void handleAddTenant() {
        String name = tenantNameField.getText();
        String email = tenantEmailField.getText();
        String contact = tenantContactField.getText();
        String unit = tenantUnitField.getText();
        String rentText = tenantRentField.getText();
        String status = statusComboBox.getValue();

        if (name.isEmpty() || email.isEmpty() || contact.isEmpty() || unit.isEmpty() || rentText.isEmpty() || status == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill all fields!").showAndWait();
            return;
        }

        if (ownerId == null) {
            new Alert(Alert.AlertType.ERROR, "Owner context missing (ownerId is null).").showAndWait();
            return;
        }

        double rent;
        try {
            rent = Double.parseDouble(rentText);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Rent must be a valid number!").showAndWait();
            return;
        }

        try {
            // Build request that backend expects
            com.rentpal.dto.CreateTenantRequest req = new com.rentpal.dto.CreateTenantRequest();
            req.setName(name);
            req.setEmail(email);
            req.setPhone(contact);
            req.setRoomNumber(unit);
            req.setRentAmount(rent);
            req.setOwnerId(ownerId); // <-- critical

            // POST
            TenantDTO created = tenantService.createTenant(req);

            // optional: you can ignore 'status' here if backend sets paymentStatus/remainingRent defaults
            new Alert(Alert.AlertType.INFORMATION, "Tenant added successfully!").showAndWait();

            // Ask parent to refresh its owner-scoped table
            if (tenantsController != null) {
                tenantsController.loadTenantsForOwner();
            }

            // Close dialog
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