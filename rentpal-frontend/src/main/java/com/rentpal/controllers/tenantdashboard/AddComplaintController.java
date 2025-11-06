package com.rentpal.controllers.tenantdashboard;

import com.rentpal.dto.ComplaintDTO;
import com.rentpal.dto.OwnerDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.ComplaintService;
import com.rentpal.service.OwnerService;
import com.rentpal.utils.SessionManager;
import com.rentpal.service.TenantService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

public class AddComplaintController {

    @FXML
    private Label ownerNameLabel;
    private Long selectedOwnerId;

    @FXML
    private ComboBox<String> categoryField;

    @FXML
    private ComboBox<String> priorityField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private Button submitButton;

    private ComplaintService complaintService = new ComplaintService();
    private OwnerService ownerService = new OwnerService(); // New owner service
    private TenantService tenantService = new TenantService();

    @FXML
    public void initialize() {
        // categories & priorities
        categoryField.getItems().setAll("Maintenance","Billing Issue","Noise Complaint","Security","Other");
        priorityField.getItems().setAll("Low","Medium","High");

        // IMPORTANT: populate owner for the logged-in tenant
        loadOwnerForLoggedInTenant();
    }



    private void loadOwnerForLoggedInTenant() {
        try {
            TenantDTO tenant = SessionManager.getInstance().getCurrentTenant();
            if (tenant == null) {
                ownerNameLabel.setText("No tenant logged in");
                return;
            }

            OwnerDTO owner = tenant.getOwner();
            if (owner == null && tenant.getOwnerId() != null) {
                owner = new OwnerService().getOwnerById(tenant.getOwnerId());
            }

            if (owner != null) {
                ownerNameLabel.setText(owner.getName());
                selectedOwnerId = owner.getOwnerId();
            } else {
                ownerNameLabel.setText("No owner linked");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ownerNameLabel.setText("Error loading owner");
        }
    }

    @FXML
    private void handleSubmit() {
        // Owner is already selected/locked; use selectedOwnerId
        if (selectedOwnerId == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No linked owner found for your account.");
            return;
        }

        String category = categoryField.getValue();
        String priority = priorityField.getValue();
        String description = descriptionField.getText().trim();
        if (category == null || priority == null || description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill all required fields.");
            return;
        }

        try {
            TenantDTO currentTenant = SessionManager.getInstance().getCurrentTenant();
            if (currentTenant == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please log in again.");
                return;
            }

            ComplaintDTO complaint = new ComplaintDTO();
            complaint.setTitle(category);
            complaint.setDescription(description);
            complaint.setStatus("Pending");
            complaint.setOwnerId(selectedOwnerId);     // ← the linked owner

            complaintService.addComplaint(currentTenant.getTenantId(), complaint);

            showAlert(Alert.AlertType.INFORMATION, "Complaint Submitted",
                      "Your complaint has been recorded successfully.");
            closeForm();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to submit complaint", e.getMessage());
        }
    }
    private void markInvalid(Control control) {
        control.setStyle("-fx-border-color: red;");
        control.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                control.setStyle("");
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void notifyOwnerDashboard() {
        // In a real application, this would send a notification to the owner dashboard
        // For now, we'll just print a message to the console
        System.out.println("Complaint submitted - owner dashboard should refresh");
        
        // If we had access to the owner dashboard controller, we could call:
        // ownerDashboardController.refreshComplaints();
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}