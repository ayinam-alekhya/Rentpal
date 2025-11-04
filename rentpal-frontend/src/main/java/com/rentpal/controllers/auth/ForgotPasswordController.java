package com.rentpal.controllers.auth;

import com.rentpal.dto.OwnerDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.OwnerService;
import com.rentpal.service.TenantService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import com.rentpal.utils.SceneSwitcher;

import java.util.List;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    private OwnerService ownerService = new OwnerService();
    private TenantService tenantService = new TenantService();

    // ✅ Called when "Send Reset Link" button is clicked
    @FXML
    private void handleReset(ActionEvent event) {
        String email = emailField.getText();

        if (email == null || email.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Email");
            alert.setHeaderText(null);
            alert.setContentText("Please enter your registered email address.");
            alert.showAndWait();
            return;
        }

        try {
            // Check if email exists in the system
            boolean userExists = checkUserExists(email);
            
            if (userExists) {
                // TODO: In a real application, send actual password reset email
                System.out.println("Reset link sent to: " + email);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Reset Link Sent");
                alert.setHeaderText(null);
                alert.setContentText("A password reset link has been sent to " + email);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Email Not Found");
                alert.setHeaderText(null);
                alert.setContentText("No account found with this email address.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while processing your request. Please try again.");
            alert.showAndWait();
        }
    }

    private boolean checkUserExists(String email) throws Exception {
        // Check if email exists as owner
        List<OwnerDTO> owners = ownerService.getAllOwners();
        boolean ownerExists = owners.stream().anyMatch(owner -> owner.getEmail() != null && owner.getEmail().equals(email));
        
        if (ownerExists) {
            return true;
        }
        
        // Check if email exists as tenant
        List<TenantDTO> tenants = tenantService.getAllTenants();
        return tenants.stream().anyMatch(tenant -> tenant.getEmail() != null && tenant.getEmail().equals(email));
    }

    // ✅ Called when "Back to Login" button is clicked
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Uses your updated SceneSwitcher (now accepts ActionEvent)
            SceneSwitcher.switchScene(event, "/com/rentpal/fxml/login.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Called when "Back to Home" button is clicked
    @FXML
    private void handleBackToHome(ActionEvent event) {
        try {
            SceneSwitcher.switchToWelcome(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}