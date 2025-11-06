package com.rentpal.controllers.auth;

import com.rentpal.dto.OwnerDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.AuthService;
import com.rentpal.utils.SceneSwitcher;
import com.rentpal.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private AuthService authService = new AuthService();

    // ✅ Handles login button click
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        System.out.println("Login attempt for: " + email);

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Missing email or password");
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please enter both email and password.");
            return;
        }

        try {
            // Try to authenticate as owner first
            System.out.println("Attempting owner authentication...");
            AuthService.AuthResult result = authenticateAsOwner(email, password);
            
            if (result.isSuccess()) {
                System.out.println("Owner authentication successful");
                handleSuccessfulOwnerLogin(result.getOwner(), event);
                return;
            }

            // If owner authentication fails, try as tenant
            System.out.println("Owner authentication failed, trying tenant authentication...");
            result = authenticateAsTenant(email, password);
            
            if (result.isSuccess()) {
                System.out.println("Tenant authentication successful");
                handleSuccessfulTenantLogin(result.getTenant(), event);
                return;
            }

            // If both fail
            System.out.println("Both owner and tenant authentication failed");
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password.");
        } catch (Exception e) {
            System.err.println("Error during login process: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred while logging in: " + e.getMessage());
        }
    }

    private AuthService.AuthResult authenticateAsOwner(String email, String password) {
        try {
            return authService.authenticate(email, password, "owner");
        } catch (Exception e) {
            System.err.println("Owner authentication error: " + e.getMessage());
            return new AuthService.AuthResult(false, null, null, null);
        }
    }

    private AuthService.AuthResult authenticateAsTenant(String email, String password) {
        try {
            return authService.authenticate(email, password, "tenant");
        } catch (Exception e) {
            System.err.println("Tenant authentication error: " + e.getMessage());
            return new AuthService.AuthResult(false, null, null, null);
        }
    }

    private void handleSuccessfulOwnerLogin(OwnerDTO owner, ActionEvent event) {
        try {
            System.out.println("Handling successful owner login for: " + owner.getName());
            SessionManager.getInstance().setCurrentOwner(owner);
            SceneSwitcher.switchScene(event, "/com/rentpal/fxml/owner_dashboard.fxml");
        } catch (Exception e) {
            System.err.println("Error handling owner login: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load owner dashboard: " + e.getMessage());
        }
    }

    private void handleSuccessfulTenantLogin(TenantDTO tenant, ActionEvent event) {
        try {
            // fetch full tenant so we have owner/ownerId populated
            com.rentpal.service.TenantService tenantService = new com.rentpal.service.TenantService();
            TenantDTO fullTenant = tenantService.getTenantById(tenant.getTenantId());

            // (optional) also cache owner separately if you like
            if (fullTenant.getOwner() != null) {
                SessionManager.getInstance().setCurrentOwner(fullTenant.getOwner());
            }

            SessionManager.getInstance().setCurrentTenant(fullTenant);

            com.rentpal.utils.SceneSwitcher.switchScene(event, "/com/rentpal/fxml/tenant_dashboard.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load tenant dashboard: " + e.getMessage());
        }
    }


    // ✅ Handles "Forgot Password" link or button click
    @FXML
    private void handleForgotPassword(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/com/rentpal/fxml/forgot_password.fxml");
        } catch (Exception e) {
            System.err.println("Error navigating to forgot password: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ Handles "Sign Up" link or button click
    @FXML
    private void handleSignUp(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/com/rentpal/fxml/signup.fxml");
        } catch (Exception e) {
            System.err.println("Error navigating to signup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ Handles "Back to Home" button click
    @FXML
    private void handleBackToHome(ActionEvent event) {
        try {
            SceneSwitcher.switchToWelcome(event);
        } catch (Exception e) {
            System.err.println("Error navigating to home: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ Utility method for showing alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}