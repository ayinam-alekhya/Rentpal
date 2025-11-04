package com.rentpal.controllers.tenantdashboard;

import com.rentpal.dto.ComplaintDTO;
import com.rentpal.dto.OwnerDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.ComplaintService;
import com.rentpal.service.OwnerService;
import com.rentpal.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddComplaintController {

    @FXML private Label ownerSelectionLabel;
    @FXML private Label ownerNameLabel;

    @FXML private ComboBox<String> categoryField;
    @FXML private ComboBox<String> priorityField;
    @FXML private TextArea descriptionField;
    @FXML private Button submitButton;

    private final ComplaintService complaintService = new ComplaintService();
    @SuppressWarnings("unused")
    private final OwnerService ownerService = new OwnerService();

    private Long ownerId; // resolved from tenant.getOwner()

    @FXML
    public void initialize() {
        // Resolve logged-in tenant and its mapped owner
        TenantDTO currentTenant = SessionManager.getInstance().getCurrentTenant();

        if (currentTenant != null && currentTenant.getOwner() != null) {
            OwnerDTO mappedOwner = currentTenant.getOwner();
            ownerId = mappedOwner.getOwnerId();
            String display =
                    (mappedOwner.getName() != null && !mappedOwner.getName().isBlank())
                            ? mappedOwner.getName()
                            : "Owner #" + ownerId;
            ownerNameLabel.setText(display);
        } else {
            // No owner info on the DTO; keep label but prevent submit
            ownerId = null;
            ownerNameLabel.setText("Unknown Owner");
        }

        categoryField.getItems().setAll("Maintenance", "Billing Issue", "Noise Complaint", "Security", "Other");
        priorityField.getItems().setAll("Low", "Medium", "High");
    }

    @FXML
    private void handleSubmit() {
        String category = categoryField.getValue();
        String priority  = priorityField.getValue();
        String description = descriptionField.getText() == null ? "" : descriptionField.getText().trim();

        if (ownerId == null) {
            showAlert(Alert.AlertType.ERROR, "Owner Missing",
                    "Your account isn’t mapped to an owner. Please re-login or contact support.");
            return;
        }
        if (category == null || priority == null || description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information",
                    "Please select category, priority and enter a description.");
            return;
        }

        try {
            TenantDTO currentTenant = SessionManager.getInstance().getCurrentTenant();
            if (currentTenant == null || currentTenant.getTenantId() == null) {
                showAlert(Alert.AlertType.ERROR, "Authentication Error", "Please log in again.");
                return;
            }

            ComplaintDTO complaint = new ComplaintDTO();
            complaint.setTitle(category);
            complaint.setDescription(description);
            complaint.setStatus("Pending");
            complaint.setOwnerId(ownerId); // use mapped owner

            complaintService.addComplaint(currentTenant.getTenantId(), complaint);

            showAlert(Alert.AlertType.INFORMATION, "Complaint Submitted",
                    "Your complaint has been recorded successfully.");
            closeForm();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to submit complaint",
                    "An error occurred while submitting your complaint:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
