package com.rentpal.controllers.auth;

import com.rentpal.dto.OwnerDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.OwnerService;
import com.rentpal.service.TenantService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.rentpal.utils.SceneSwitcher;

public class SignupController {

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private OwnerService ownerService = new OwnerService();
    private TenantService tenantService = new TenantService();

    @FXML
    public void initialize() {
        // Initialize role selection dropdown
        roleComboBox.setItems(FXCollections.observableArrayList("Owner", "Tenant"));
        roleComboBox.getSelectionModel().selectFirst();
    }

    // ✅ Handles the Signup button click
    @FXML
    private void handleSignup(ActionEvent event) {
        String selectedRole = roleComboBox.getValue();
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (selectedRole == null || name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill out all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
            return;
        }

        try {
            if ("Owner".equals(selectedRole)) {
                // Create a new owner DTO
                OwnerDTO ownerDTO = new OwnerDTO();
                ownerDTO.setName(name);
                ownerDTO.setEmail(email);
                ownerDTO.setPhone(""); // Phone is not collected in signup form
                ownerDTO.setAddress(""); // Address is not collected in signup form

                // Call backend API to create owner
                OwnerDTO createdOwner = ownerService.createOwner(ownerDTO);

                System.out.println("New owner registered: " + createdOwner.getName() + " (" + createdOwner.getEmail() + ")");

                showAlert(Alert.AlertType.INFORMATION, "Signup Successful", "Owner account created successfully. You can now log in.");
            } else if ("Tenant".equals(selectedRole)) {
                // Create a new tenant DTO
                TenantDTO tenantDTO = new TenantDTO();
                tenantDTO.setName(name);
                tenantDTO.setEmail(email);
                tenantDTO.setPhone(""); // Phone is not collected in signup form
                tenantDTO.setRoomNumber(""); // Room number is not collected in signup form
                tenantDTO.setRentAmount(0.0); // Rent amount is not collected in signup form
                tenantDTO.setStatus("Active"); // Default status
                tenantDTO.setRemainingRent(0.0); // Default remaining rent
                tenantDTO.setPaymentStatus("Unpaid"); // Default payment status

                // Call backend API to create tenant
                TenantDTO createdTenant = tenantService.createTenant(tenantDTO);

                System.out.println("New tenant registered: " + createdTenant.getName() + " (" + createdTenant.getEmail() + ")");

                showAlert(Alert.AlertType.INFORMATION, "Signup Successful", "Tenant account created successfully. You can now log in.");
            }

            // ✅ After successful signup, go back to login screen using the same Stage
            SceneSwitcher.switchScene(event, "/com/rentpal/fxml/login.fxml");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while signing up. Please try again.");
        }
    }

    // ✅ Handles "Back to Login" button click
    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/com/rentpal/fxml/login.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Handles "Back to Home" button click
    @FXML
    private void handleBackToHome(ActionEvent event) {
        try {
            SceneSwitcher.switchToWelcome(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Utility for showing alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}