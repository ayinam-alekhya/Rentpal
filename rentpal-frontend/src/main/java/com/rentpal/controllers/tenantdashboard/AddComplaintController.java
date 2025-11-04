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
import java.util.List;

public class AddComplaintController {

    @FXML
    private ComboBox<OwnerDTO> ownerComboBox; // New owner selection dropdown

    @FXML
    private Label ownerSelectionLabel; // Label for owner selection

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
    private Long selectedOwnerId; // To store the owner ID for the complaint

    @FXML
    public void initialize() {
        // Load owners into the combo box
        loadOwners();

        // Sample categories
        categoryField.getItems().addAll(
                "Maintenance",
                "Billing Issue",
                "Noise Complaint",
                "Security",
                "Other"
        );

        // Priority levels
        priorityField.getItems().addAll("Low", "Medium", "High");
    }

    private void loadOwners() {
        try {
            List<OwnerDTO> owners = ownerService.getAllOwners();
            ownerComboBox.getItems().addAll(owners);
            
            // Set converter to display owner name in combo box
            ownerComboBox.setConverter(new javafx.util.StringConverter<OwnerDTO>() {
                @Override
                public String toString(OwnerDTO owner) {
                    return owner != null ? owner.getName() : "";
                }

                @Override
                public OwnerDTO fromString(String string) {
                    return ownerComboBox.getItems().stream()
                            .filter(owner -> owner.getName().equals(string))
                            .findFirst()
                            .orElse(null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load owners: " + e.getMessage());
        }
    }

    @FXML
    private void handleSubmit() {
        // Validate Owner selection
        OwnerDTO selectedOwner = ownerComboBox.getValue();
        if (selectedOwner == null) {
            markInvalid(ownerComboBox);
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please select an owner");
            return;
        }
        selectedOwnerId = selectedOwner.getOwnerId();

        String category = categoryField.getValue();
        String priority = priorityField.getValue();
        String description = descriptionField.getText().trim();

        if (category == null || priority == null || description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill all required fields");
            return;
        }

        try {
            // Get current tenant from session
            TenantDTO currentTenant = SessionManager.getInstance().getCurrentTenant();
            if (currentTenant != null) {
                // Create complaint DTO
                ComplaintDTO complaint = new ComplaintDTO();
                complaint.setTitle(category);
                complaint.setDescription(description);
                complaint.setStatus("Pending"); // Default status
                complaint.setOwnerId(selectedOwnerId); // Set the selected owner ID
                
                // Save to backend
                ComplaintDTO createdComplaint = complaintService.addComplaint(currentTenant.getTenantId(), complaint);
                
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Complaint Submitted", "Complaint Added Successfully", 
                         "Your complaint has been recorded successfully.");
                
                // Notify owner dashboard to refresh complaints
                notifyOwnerDashboard();
                
                closeForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Authentication Error", "Please log in again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to submit complaint", 
                     "An error occurred while submitting your complaint: " + e.getMessage());
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