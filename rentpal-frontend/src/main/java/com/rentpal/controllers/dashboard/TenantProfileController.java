package com.rentpal.controllers.dashboard;

import com.rentpal.controllers.tenants.Tenant;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.TenantService;
import com.rentpal.utils.SessionManager;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

public class TenantProfileController {

    @FXML private AnchorPane rootPane;
    @FXML private Circle profilePic;
    @FXML private Button changePhotoBtn;
    @FXML private TextField nameField;
    @FXML private TextField emailField; // placeholder
    @FXML private TextField phoneField;
    @FXML private TextField apartmentField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Button saveBtn;

    private boolean editable = false;
    private boolean ownerView = false;
    private Tenant tenant;
    private TenantService tenantService = new TenantService();

    @FXML
    public void initialize() {
        statusCombo.setItems(FXCollections.observableArrayList("Active", "Inactive"));
        loadTenantProfileData();
        setEditable(false);
        playFadeIn(rootPane);
    }

    private void loadTenantProfileData() {
        try {
            // Get current tenant from session
            TenantDTO currentTenant = SessionManager.getInstance().getCurrentTenant();
            if (currentTenant != null) {
                // Load tenant data from backend
                TenantDTO tenant = tenantService.getTenantById(currentTenant.getTenantId());
                if (tenant != null) {
                    nameField.setText(tenant.getName() != null ? tenant.getName() : "");
                    emailField.setText(tenant.getEmail() != null ? tenant.getEmail() : "");
                    phoneField.setText(tenant.getPhone() != null ? tenant.getPhone() : "");
                    apartmentField.setText(tenant.getRoomNumber() != null ? tenant.getRoomNumber() : "");
                    statusCombo.getSelectionModel().select(tenant.getStatus() != null ? tenant.getStatus() : "Active");
                }
            } else {
                // Fallback to placeholder data
                nameField.setText("");
                emailField.setText("");
                phoneField.setText("");
                apartmentField.setText("");
                statusCombo.getSelectionModel().select("Active");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to placeholder data in case of error
            nameField.setText("");
            emailField.setText("");
            phoneField.setText("");
            apartmentField.setText("");
            statusCombo.getSelectionModel().select("Active");
        }
    }

    public void loadTenant(Tenant tenant, boolean editable, boolean ownerView) {
        this.tenant = tenant;
        this.editable = editable;
        this.ownerView = ownerView;

        if (tenant != null) {
            nameField.setText(tenant.getName());
            emailField.setText("");
            phoneField.setText(tenant.getContact());
            apartmentField.setText(tenant.getUnit());
            statusCombo.getSelectionModel().select(tenant.getStatus());
        }

        setEditable(editable);
        statusCombo.setDisable(!ownerView);
        changePhotoBtn.setVisible(editable);
        playFadeIn(rootPane);
    }

    private void setEditable(boolean value) {
        nameField.setEditable(value);
        emailField.setEditable(value);
        phoneField.setEditable(value);
        apartmentField.setEditable(value);
    }

    @FXML
    private void handleChangePhoto(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            profilePic.setFill(new ImagePattern(image));
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            // Get current tenant from session
            TenantDTO currentTenant = SessionManager.getInstance().getCurrentTenant();
            if (currentTenant != null) {
                // Update tenant data
                currentTenant.setName(nameField.getText());
                currentTenant.setEmail(emailField.getText());
                currentTenant.setPhone(phoneField.getText());
                currentTenant.setRoomNumber(apartmentField.getText());
                currentTenant.setStatus(statusCombo.getSelectionModel().getSelectedItem());
                
                // Save to backend
                TenantDTO updatedTenant = tenantService.updateTenant(currentTenant.getTenantId(), currentTenant);
                
                // Update session
                SessionManager.getInstance().setCurrentTenant(updatedTenant);
                
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile: " + e.getMessage());
        }

        setEditable(false);
        changePhotoBtn.setVisible(false);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        setEditable(true);
        changePhotoBtn.setVisible(true);
    }

    @FXML
    private void handleViewPayments(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rentpal/fxml/payments.fxml"));
            Parent view = loader.load();

            // Optional: if you want fade-in
            javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(300), view);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            // Replace current content
            javafx.scene.Scene scene = ((Button) event.getSource()).getScene();
            StackPane root = (StackPane) scene.lookup("#contentArea");
            if (root != null) root.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void playFadeIn(Node node) {
        if (node == null) return;
        node.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(300), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
}
