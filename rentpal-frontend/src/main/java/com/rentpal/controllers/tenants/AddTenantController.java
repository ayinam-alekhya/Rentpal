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

        double rent;
        try {
            rent = Double.parseDouble(rentText);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Rent must be a valid number!").showAndWait();
            return;
        }

        // Create a new TenantDTO object
        TenantDTO tenantDTO = new TenantDTO();
        tenantDTO.setName(name);
        tenantDTO.setEmail(email);
        tenantDTO.setPhone(contact);
        tenantDTO.setRoomNumber(unit);
        tenantDTO.setRentAmount(rent);
        tenantDTO.setStatus(status);
        tenantDTO.setRemainingRent(0.0);
        tenantDTO.setPaymentStatus("Unpaid");

        // Set the owner information from the session
        if (SessionManager.getInstance().isOwner() && SessionManager.getInstance().getCurrentOwner() != null) {
            tenantDTO.setOwner(SessionManager.getInstance().getCurrentOwner());
        } else {
            new Alert(Alert.AlertType.ERROR, "Owner information not found!").showAndWait();
            return;
        }

        try {
            // Call backend API to create tenant
            TenantDTO createdTenant = tenantService.createTenant(tenantDTO);
            
            // Create a new Tenant object for the frontend
            Tenant newTenant = new Tenant();
            newTenant.setName(createdTenant.getName());
            newTenant.setEmail(createdTenant.getEmail());
            newTenant.setContact(createdTenant.getPhone());
            newTenant.setUnit(createdTenant.getRoomNumber());
            newTenant.setRent(createdTenant.getRentAmount());
            newTenant.setStatus(createdTenant.getStatus());

            // Add to parent table (if controller is connected)
            if (tenantsController != null) {
                tenantsController.addTenantToTable(newTenant);
            }

            // Show success message
            new Alert(Alert.AlertType.INFORMATION, "Tenant added successfully!").showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to add tenant: " + e.getMessage()).showAndWait();
            return;
        }

        // Close the popup
        Stage stage = (Stage) tenantNameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) tenantNameField.getScene().getWindow();
        stage.close();
    }
}