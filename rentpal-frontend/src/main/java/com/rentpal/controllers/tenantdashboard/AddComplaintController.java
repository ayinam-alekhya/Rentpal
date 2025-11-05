package com.rentpal.controllers.tenantdashboard;

import com.rentpal.dto.OwnerDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.ComplaintService;
import com.rentpal.service.OwnerService;
import com.rentpal.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class AddComplaintController {

    @FXML private Label ownerSelectionLabel;
    @FXML private Label ownerNameLabel;

    @FXML private ComboBox<String> categoryField;
    @FXML private ComboBox<String> priorityField;
    @FXML private TextArea descriptionField;
    @FXML private Button submitButton;

    private final ComplaintService complaintService = new ComplaintService();
    private final OwnerService ownerService = new OwnerService();

    private Long ownerId; // resolved from tenant.getOwner()

    // Map UI category labels -> backend category IDs (replace with real IDs if different)
    private final Map<String, Long> categoryIds = new HashMap<>();

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
            ownerId = null;
            ownerNameLabel.setText("Unknown Owner");
        }

        // Populate UI choices
        categoryField.getItems().setAll("Maintenance", "Billing Issue", "Noise Complaint", "Security", "Other");
        priorityField.getItems().setAll("Low", "Medium", "High");

        // Category -> ID mapping (ensure these match your backend)
        categoryIds.put("Maintenance", 1L);
        categoryIds.put("Billing Issue", 2L);
        categoryIds.put("Noise Complaint", 3L);
        categoryIds.put("Security", 4L);
        categoryIds.put("Other", 5L);
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

            Long tenantId = currentTenant.getTenantId();
            Long categoryId = categoryIds.get(category);
            if (categoryId == null) {
                showAlert(Alert.AlertType.ERROR, "Unknown Category",
                        "Selected category isn’t recognized. Please pick a valid category.");
                return;
            }

            // Normalize to enum-style names if backend expects them
            String priorityValue = priority.toUpperCase(); // "LOW"/"MEDIUM"/"HIGH"

            // Use category text as a simple title; change if you add a dedicated title field
            String title = category;

            // Call existing service signature: (Long, String, String, Long, String)
            complaintService.addComplaint(
                tenantId,
                title,
                description,
                categoryId,
                priorityValue
            );

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
        if (stage != null) {
            stage.close();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
