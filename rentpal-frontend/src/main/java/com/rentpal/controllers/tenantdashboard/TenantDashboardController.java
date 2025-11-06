package com.rentpal.controllers.tenantdashboard;

import com.rentpal.controllers.dashboard.TenantProfileController;
import com.rentpal.dto.TenantDTO;
import com.rentpal.utils.SceneSwitcher;
import com.rentpal.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class TenantDashboardController {

    @FXML
    private VBox sidebar;

    @FXML
    private StackPane contentArea;

    @FXML
    private Button logout;

    private boolean collapsed = false;

    @FXML
    public void initialize() {
        System.out.println("TenantDashboardController.initialize() called");
        
        try {
            // Check if tenant is in session
            if (SessionManager.getInstance().getCurrentTenant() != null) {
                System.out.println("Tenant found in session: " + SessionManager.getInstance().getCurrentTenant().getName());
            } else {
                System.out.println("No tenant found in session");
            }
            
            showHome();
        } catch (Exception e) {
            System.err.println("Error in TenantDashboardController.initialize(): " + e.getMessage());
            e.printStackTrace();
            // Show error page
            loadFallbackPage("Error initializing dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void toggleSidebar(ActionEvent event) {
        if (!collapsed) {
            sidebar.setPrefWidth(60);
            collapsed = true;
        } else {
            sidebar.setPrefWidth(220);
            collapsed = false;
        }
    }

    @FXML
    private void showHome() {
        System.out.println("showHome() called");
        try {
            loadPage("/com/rentpal/fxml/tenant_home.fxml");
        } catch (Exception e) {
            System.err.println("Error in showHome(): " + e.getMessage());
            e.printStackTrace();
            loadFallbackPage("Error loading home page: " + e.getMessage());
        }
    }

    @FXML
    private void showPayments() {
        System.out.println("showPayments() called");
        try {
            loadPage("/com/rentpal/fxml/tenant_payments.fxml");
        } catch (Exception e) {
            System.err.println("Error in showPayments(): " + e.getMessage());
            e.printStackTrace();
            loadFallbackPage("Error loading payments page: " + e.getMessage());
        }
    }

    @FXML
    private void showComplaints() {
        System.out.println("showComplaints() called");
        try {
            loadPage("/com/rentpal/fxml/tenant_complaints.fxml");
        } catch (Exception e) {
            System.err.println("Error in showComplaints(): " + e.getMessage());
            e.printStackTrace();
            loadFallbackPage("Error loading complaints page: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddComplaint() {
        try {
            System.out.println("handleAddComplaint() called");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rentpal/fxml/add_complaint_form.fxml"));
            Parent root = loader.load();

            root.getStylesheets().add(
                    getClass().getResource("/css/tenant_dashboard.css").toExternalForm()
            );

            Stage popup = new Stage();
            popup.setTitle("Add Complaint");
            popup.setScene(new Scene(root));
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setResizable(false);
            popup.showAndWait();

        } catch (Exception e) {
            System.err.println("Error in handleAddComplaint(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showProfile(ActionEvent event) throws IOException {
        System.out.println("showProfile() called");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rentpal/fxml/tenant_profile.fxml"));
            Parent view = loader.load();

            TenantProfileController ctrl = loader.getController();

            // Use the logged-in tenant DTO from session
            TenantDTO current = SessionManager.getInstance().getCurrentTenant();
            if (current != null) {
                ctrl.loadTenant(current, true, false);   // ✅ no ambiguity
            } else {
                // Fallback if session is empty
                ctrl.loadTenant(new TenantDTO(), true, false);
            }

            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            System.err.println("Error in showProfile(): " + e.getMessage());
            e.printStackTrace();
            loadFallbackPage("Error loading profile: " + e.getMessage());
        }
    }

    // ✅ FIXED logout method
    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        System.out.println("handleLogout() called");
        try {
            SceneSwitcher.switchScene(event, "/com/rentpal/fxml/login.fxml");
        } catch (Exception e) {
            System.err.println("Error in handleLogout(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ NEW home method
    @FXML
    private void handleHome(ActionEvent event) throws IOException {
        System.out.println("handleHome() called");
        try {
            SceneSwitcher.switchToWelcome(event);
        } catch (Exception e) {
            System.err.println("Error in handleHome(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ Helper to load FXML pages into the main content area
    private void loadPage(String fxmlPath) {
        try {
            System.out.println("=== loadPage START ===");
            System.out.println("Loading page: " + fxmlPath);
            
            // Check if FXML file exists
            java.net.URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("FXML resource not found: " + fxmlPath);
                System.out.println("=== loadPage END (resource not found) ===");
                
                // Load a fallback page
                loadFallbackPage("Page not found: " + fxmlPath);
                return;
            }
            System.out.println("FXML resource found: " + resource);
            
            FXMLLoader loader = new FXMLLoader(resource);
            System.out.println("FXMLLoader created");
            
            Parent page = loader.load();
            System.out.println("Page loaded successfully: " + fxmlPath);
            
            // Get controller and check if it's the right type
            Object controller = loader.getController();
            System.out.println("Controller loaded: " + controller);
            if (controller != null) {
                System.out.println("Controller class: " + controller.getClass().getName());
            }
            
            // If it's the complaints page, call loadComplaintData
            if (fxmlPath.equals("/com/rentpal/fxml/tenant_complaints.fxml")) {
                try {
                    TenantComplaintsController complaintsController = loader.getController();
                    if (complaintsController != null) {
                        System.out.println("Calling loadComplaintData for complaints page");
                        complaintsController.reloadFromServer();
                    } else {
                        System.out.println("Complaints controller is null");
                    }
                } catch (Exception e) {
                    System.err.println("Error calling loadComplaintData: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("Clearing content area");
            contentArea.getChildren().clear();
            System.out.println("Adding page to content area");
            contentArea.getChildren().add(page);
            System.out.println("Page added to content area: " + fxmlPath);
            System.out.println("=== loadPage END ===");
        } catch (Exception e) {
            System.err.println("Error loading page " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== loadPage END (error) ===");
            
            // Load a fallback page
            loadFallbackPage("Error loading page: " + fxmlPath + " - " + e.getMessage());
        }
    }
    
    private void loadFallbackPage(String message) {
        try {
            System.out.println("Loading fallback page with message: " + message);
            
            // Create a simple fallback page
            javafx.scene.layout.VBox fallbackPage = new javafx.scene.layout.VBox();
            fallbackPage.setStyle("-fx-padding: 20; -fx-spacing: 10;");
            
            javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Page Loading Error");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            
            javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(message);
            messageLabel.setWrapText(true);
            
            fallbackPage.getChildren().addAll(titleLabel, messageLabel);
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fallbackPage);
            System.out.println("Fallback page loaded");
        } catch (Exception e) {
            System.err.println("Error loading fallback page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}