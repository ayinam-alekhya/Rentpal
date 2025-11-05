package com.rentpal.controllers.auth;

import com.rentpal.dto.OwnerDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.AuthService;
import com.rentpal.utils.SceneSwitcher;
import com.rentpal.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    // NEW: role controls (radio buttons)
    @FXML private RadioButton ownerRadio;   // selected = owner login
    @FXML private RadioButton tenantRadio;  // selected = tenant login

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please enter both email and password.");
            return;
        }

        // Determine role exactly once
        String role = resolveRole();
        System.out.println("Login attempt for: " + email + " as role: " + role);

        try {
            AuthService.AuthResult result = authService.authenticate(email, password, role);

            if (!result.isSuccess()) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password.");
                return;
            }

            // Route by role
            if ("owner".equalsIgnoreCase(role)) {
                OwnerDTO owner = result.getOwner();
                if (owner == null) {
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Owner details missing in response.");
                    return;
                }
                SessionManager.getInstance().setCurrentOwner(owner);
                SceneSwitcher.switchScene(event, "/com/rentpal/fxml/owner_dashboard.fxml");
            } else {
                TenantDTO tenant = result.getTenant();
                if (tenant == null) {
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Tenant details missing in response.");
                    return;
                }
                SessionManager.getInstance().setCurrentTenant(tenant);
                SceneSwitcher.switchScene(event, "/com/rentpal/fxml/tenant_dashboard.fxml");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Network Error", e.getMessage());
        }
    }

    private String resolveRole() {
        // If you wired radio buttons:
        if (ownerRadio != null && ownerRadio.isSelected()) return "owner";
        if (tenantRadio != null && tenantRadio.isSelected()) return "tenant";

        // If you used a ComboBox<String> roleCombo instead, return roleCombo.getValue()
        // Fallback default:
        return "owner";
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        try { SceneSwitcher.switchScene(event, "/com/rentpal/fxml/forgot_password.fxml"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        try { SceneSwitcher.switchScene(event, "/com/rentpal/fxml/signup.fxml"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleBackToHome(ActionEvent event) {
        try { SceneSwitcher.switchToWelcome(event); }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
