package com.rentpal.controllers.tenants;

import com.rentpal.controllers.dashboard.TenantProfileController;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.TenantService;
import javafx.animation.FadeTransition;
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

    @FXML
    private TableView<Tenant> tenantsTable;

    @FXML
    private TableColumn<Tenant, String> nameColumn;
    @FXML
    private TableColumn<Tenant, String> unitColumn;
    @FXML
    private TableColumn<Tenant, String> contactColumn;
    @FXML
    private TableColumn<Tenant, String> statusColumn;

    private StackPane contentArea;
    private TenantService tenantService = new TenantService();
    private ObservableList<Tenant> tenantList = FXCollections.observableArrayList();

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load tenants from backend
        loadTenantsFromBackend();

        tenantsTable.setItems(tenantList);
        tenantsTable.setOnMouseClicked(this::handleRowDoubleClick);
    }

    private void loadTenantsFromBackend() {
        try {
            List<TenantDTO> tenantDTOs = tenantService.getAllTenants();
            tenantList.clear();
            
            for (TenantDTO tenantDTO : tenantDTOs) {
                Tenant tenant = new Tenant(
                    tenantDTO.getTenantId(), // Include tenant ID
                    tenantDTO.getName(),
                    tenantDTO.getRoomNumber(),
                    tenantDTO.getPhone(),
                    tenantDTO.getRentAmount(),
                    tenantDTO.getStatus()
                );
                tenantList.add(tenant);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load tenants: " + e.getMessage());
        }
    }

    private void handleRowDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2 && tenantsTable.getSelectionModel().getSelectedItem() != null) {
            Tenant selectedTenant = tenantsTable.getSelectionModel().getSelectedItem();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rentpal/fxml/tenant_profile.fxml"));
                Parent view = loader.load();

                TenantProfileController controller = loader.getController();
                controller.loadTenant(selectedTenant, false, true);

                if (contentArea != null) {
                    contentArea.getChildren().setAll(view);
                    playFadeIn(view);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void playFadeIn(Parent node) {
        FadeTransition fade = new FadeTransition(Duration.millis(300), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    @FXML
    private void handleAddTenant() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rentpal/fxml/add_tenant.fxml"));
            Parent root = loader.load();

            AddTenantController controller = loader.getController();
            controller.setTenantsController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Tenant");
            stage.showAndWait();
            
            // Refresh the tenant list after adding a new tenant
            loadTenantsFromBackend();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method called by AddTenantController to add a new tenant to the table
    public void addTenantToTable(Tenant tenant) {
        if (tenantsTable != null && tenant != null) {
            tenantList.add(tenant);
        } else {
            System.err.println("❌ Cannot add tenant: Table or tenant is null");
        }
    }
    
    // Method to refresh tenant data from backend
    public void refreshTenants() {
        loadTenantsFromBackend();
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