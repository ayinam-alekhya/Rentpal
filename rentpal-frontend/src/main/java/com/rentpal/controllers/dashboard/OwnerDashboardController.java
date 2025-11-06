package com.rentpal.controllers.dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import java.io.IOException;
import com.rentpal.utils.SessionManager;
import com.rentpal.dto.OwnerDTO;

import com.rentpal.controllers.tenants.TenantsController;
import com.rentpal.utils.SceneSwitcher;

public class OwnerDashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox sidebar;

    private boolean sidebarCollapsed = false;
    private Long ownerId;

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    @FXML
    public void initialize() throws IOException {
        OwnerDTO owner = SessionManager.getInstance().getCurrentOwner();
        if (owner != null) {
            this.ownerId = owner.getOwnerId();   // <-- set it here
        }
        showDashboard(null);
    }

    private void loadView(String fxml) throws IOException {
        Parent view = FXMLLoader.load(getClass().getResource(fxml));
        contentArea.getChildren().setAll(view);
    }
    

    @FXML
    private void showDashboard(ActionEvent event) throws IOException {
        loadView("/com/rentpal/fxml/dashboard_home.fxml");
    }

    @FXML
    private void showTenants(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rentpal/fxml/tenants.fxml"));
        Parent view = loader.load();
        TenantsController controller = loader.getController();
        controller.setContentArea(contentArea);
        controller.setOwnerId(ownerId);
        controller.loadTenantsForOwner();
        contentArea.getChildren().setAll(view);
    }

    @FXML
    private void showPayments(ActionEvent event) throws IOException {
        loadView("/com/rentpal/fxml/payments.fxml");
    }

    @FXML
    private void showComplaints(ActionEvent event) throws IOException {
        loadView("/com/rentpal/fxml/complaints.fxml");
    }

    @FXML
    private void showProfile(ActionEvent event) throws IOException {
        loadView("/com/rentpal/fxml/owner_profile.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/rentpal/fxml/login.fxml"));
        javafx.stage.Stage stage = (javafx.stage.Stage)
                ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new javafx.scene.Scene(root));
        stage.show();
    }

    @FXML
    private void handleHome(ActionEvent event) throws IOException {
        SceneSwitcher.switchToWelcome(event);
    }

    // --- Collapsible Sidebar ---
    @FXML
    private void toggleSidebar(ActionEvent event) {
        if (!sidebarCollapsed) {
            sidebar.setPrefWidth(60);
            sidebarCollapsed = true;
        } else {
            sidebar.setPrefWidth(220);
            sidebarCollapsed = false;
        }
    }
}