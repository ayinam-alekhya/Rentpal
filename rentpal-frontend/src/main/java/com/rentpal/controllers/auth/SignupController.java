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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.rentpal.utils.ApiUtil;
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
    private ComboBox<OwnerDTO> ownerCombo;

    private OwnerService ownerService = new OwnerService();
    private TenantService tenantService = new TenantService();

    @FXML
    public void initialize() {
        // Initialize role selection dropdown
        roleComboBox.setItems(FXCollections.observableArrayList("Owner", "Tenant"));
        roleComboBox.getSelectionModel().selectFirst();
        loadOwners();
        // Hide the owner dropdown initially
        ownerCombo.setVisible(false);
        ownerCombo.setDisable(true);

        // Whenever the selected role changes
        roleComboBox.valueProperty().addListener((obs, oldV, newV) -> {
            boolean tenant = "Tenant".equalsIgnoreCase(newV);
            ownerCombo.setVisible(tenant);
            ownerCombo.setDisable(!tenant);
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

        // Basic validation
        if (selectedRole == null || name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill out all fields.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();

            if ("Owner".equalsIgnoreCase(selectedRole)) {
                // --- Owner signup ---
                ObjectNode body = mapper.createObjectNode();
                body.put("name", name);
                body.put("email", email);
                body.put("phone", "");
                body.put("address", "");

                String resp = ApiUtil.post("/owners", mapper.writeValueAsString(body));
                System.out.println("Owner created: " + resp);

                showAlert(Alert.AlertType.INFORMATION, "Signup Successful",
                        "Owner account created successfully. You can now log in.");

            } else if ("Tenant".equalsIgnoreCase(selectedRole)) {
                // Require an owner selection
                if (ownerCombo == null || ownerCombo.getValue() == null) {
                    showAlert(Alert.AlertType.WARNING, "Owner Required",
                            "Please select an Owner for this tenant.");
                    return;
                }

                Long ownerId = ownerCombo.getValue().getOwnerId();
                if (ownerId == null) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Owner",
                            "Selected owner has no valid ID.");
                    return;
                }

                // Build CreateTenantRequest payload
                ObjectNode body = mapper.createObjectNode();
                body.put("name", name);
                body.put("email", email);
                body.put("phone", "");
                body.put("roomNumber", "");
                body.put("rentAmount", 0.0);
                body.put("ownerId", ownerId);

                // POST /api/tenants
                String resp = ApiUtil.post("/tenants", mapper.writeValueAsString(body));
                System.out.println("Tenant created: " + resp);

                showAlert(Alert.AlertType.INFORMATION, "Signup Successful",
                        "Tenant account created and mapped to Owner ID " + ownerId + ". You can now log in.");
            }

            // Return to login after successful signup
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

    // ✅ Handles Owners Id for tenants
    private void loadOwners() {
        try {
            String resp = ApiUtil.get("/owners");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode arr = mapper.readTree(resp);

            var items = FXCollections.<OwnerDTO>observableArrayList();

            if (arr.isArray()) {
                for (JsonNode o : arr) {
                    OwnerDTO dto = new OwnerDTO();
                    dto.setOwnerId(o.path("ownerId").asLong());
                    dto.setName(o.path("name").asText(""));
                    dto.setEmail(o.path("email").asText(""));
                    dto.setPhone(o.path("phone").asText(""));
                    dto.setAddress(o.path("address").asText(""));
                    items.add(dto);
                }
            }

            ownerCombo.setItems(items);
            ownerCombo.setCellFactory(cb -> new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(OwnerDTO v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? "" : v.getOwnerId() + " — " + v.getName());
                }
            });
            ownerCombo.setButtonCell(new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(OwnerDTO v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? "" : v.getOwnerId() + " — " + v.getName());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Owners Failed",
                    "Could not load owners. Please try again.");
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