package com.rentpal.controllers.tenants;

import com.rentpal.controllers.dashboard.TenantProfileController;
import com.rentpal.dto.TenantDTO;
import com.rentpal.dto.TenantSummaryDTO;
import com.rentpal.service.TenantService;
import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class TenantsController {

    @FXML private TableView<TenantSummaryDTO> tenantsTable;
    @FXML private TableColumn<TenantSummaryDTO, String> nameColumn;
    @FXML private TableColumn<TenantSummaryDTO, String> unitColumn;
    @FXML private TableColumn<TenantSummaryDTO, String> contactColumn;   
    @FXML private TableColumn<TenantSummaryDTO, String> statusColumn;    // maps to paymentStatus

    private final TenantService tenantService = new TenantService();
    private final ObservableList<TenantSummaryDTO> tenantList = FXCollections.observableArrayList();

    private StackPane contentArea;
    private Long ownerId;

    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public void setContentArea(StackPane contentArea) { this.contentArea = contentArea; }

    @FXML
    public void initialize() {
        // Match DTO properties exactly
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        // No contact in summary DTO → show blank (or remainingRent if you want)
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact")); 
        // Show paymentStatus in the "Status" column
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        tenantsTable.setItems(tenantList);
        tenantsTable.setOnMouseClicked(this::handleRowDoubleClick);
    }

    /** Called by OwnerDashboardController after setOwnerId(...) */
    public void loadTenantsForOwner() {
        try {
            if (ownerId == null) {
                System.err.println("[TenantsController] ownerId is null; not loading.");
                return;
            }
            List<TenantSummaryDTO> rows = tenantService.getTenantsByOwner(ownerId);
            tenantList.setAll(rows);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load tenants: " + e.getMessage());
        }
    }

    private void handleRowDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2 && tenantsTable.getSelectionModel().getSelectedItem() != null) {
            TenantSummaryDTO summary = tenantsTable.getSelectionModel().getSelectedItem();
            try {
                // Fetch full tenant for profile view
                TenantDTO fullTenant = tenantService.getTenantById(summary.getTenantId());

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rentpal/fxml/tenant_profile.fxml"));
                Parent view = loader.load();
                TenantProfileController controller = loader.getController();
                controller.loadTenant(fullTenant, /*editable*/ false, /*ownerView*/ true);

                if (contentArea != null) {
                    contentArea.getChildren().setAll(view);
                    playFadeIn(view);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to open tenant profile: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddTenant() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rentpal/fxml/add_tenant.fxml"));
            Parent root = loader.load();

            AddTenantController controller = loader.getController();
            // make sure your AddTenantController has setOwnerId(...) and uses it when posting
            controller.setOwnerId(ownerId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Tenant");
            stage.showAndWait();

            // Refresh owner-scoped list after dialog closes
            loadTenantsForOwner();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playFadeIn(Parent node) {
        FadeTransition fade = new FadeTransition(Duration.millis(300), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
