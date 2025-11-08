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

    @FXML
    private ComboBox<OwnerDTO> ownerComboBox;


    private OwnerService ownerService = new OwnerService();
    private TenantService tenantService = new TenantService();

    @FXML
    public void initialize() {
        // Initialize role selection dropdown
        roleComboBox.setItems(FXCollections.observableArrayList("Owner", "Tenant"));
        roleComboBox.getSelectionModel().selectFirst();
        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isTenant = "Tenant".equals(newVal);
            ownerComboBox.setVisible(isTenant);
            ownerComboBox.setManaged(isTenant);

            if (isTenant && ownerComboBox.getItems().isEmpty()) {
                try {
                    var owners = ownerService.getAllOwners(); // next section
                    ownerComboBox.setItems(FXCollections.observableArrayList(owners));
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Load Owners Failed", "Could not load owners.");
                }
            }
        });

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
            OwnerDTO ownerDTO = new OwnerDTO();
            ownerDTO.setName(name);
            ownerDTO.setEmail(email);
            ownerDTO.setPhone("");
            ownerDTO.setAddress("");

            OwnerDTO createdOwner = ownerService.createOwner(ownerDTO);
            showAlert(Alert.AlertType.INFORMATION, "Signup Successful",
                    "Owner account created successfully. You can now log in.");
        } else if ("Tenant".equals(selectedRole)) {
            var selectedOwner = ownerComboBox.getSelectionModel().getSelectedItem();
            if (selectedOwner == null) {
                showAlert(Alert.AlertType.WARNING, "Missing Owner", "Please select an Owner for this Tenant.");
                return;
            }

            TenantDTO tenantDTO = new TenantDTO();
            tenantDTO.setName(name);
            tenantDTO.setEmail(email);
            tenantDTO.setPhone("");
            tenantDTO.setRoomNumber("");
            tenantDTO.setRentAmount(0.0);
            tenantDTO.setStatus("Active");
            tenantDTO.setRemainingRent(0.0);
            tenantDTO.setPaymentStatus("Unpaid");
            tenantDTO.setOwnerId(selectedOwner.getOwnerId());

            TenantDTO createdTenant = tenantService.createTenant(tenantDTO);
            showAlert(Alert.AlertType.INFORMATION, "Signup Successful",
                    "Tenant account created successfully. You can now log in.");
        }

        SceneSwitcher.switchScene(event, "/com/rentpal/fxml/login.fxml");


        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

            // ✅ Detect duplicate email messages from backend
            if (msg.contains("already exists") || msg.contains("duplicate") || msg.contains("email taken")) {
                showAlert(Alert.AlertType.WARNING, "User Already Exists",
                        "An account with this email already exists. Please log in instead.");
            } else {
                // Generic unknown error
                showAlert(Alert.AlertType.ERROR, "Signup Failed",
                        "An unexpected error occurred. Please try again later.");
            }

            e.printStackTrace();
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