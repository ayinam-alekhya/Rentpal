package com.rentpal.controllers.payments;

import com.rentpal.dto.PaymentDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.PaymentService;
import com.rentpal.service.TenantService;
import com.rentpal.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import java.util.List;


public class AddPaymentController {

    @FXML private TextField tenantField;
    @FXML private ComboBox<TenantDTO> tenantComboBox; // New tenant selection dropdown
    @FXML private Label tenantSelectionLabel; // Label for tenant selection
    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> methodComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private Payment newPayment;
    private PaymentService paymentService = new PaymentService();
    private TenantService tenantService = new TenantService(); // New tenant service
    private Long selectedTenantId; // To store the tenant ID for the payment

    @FXML
    public void initialize() {
        // Populate dropdowns
        methodComboBox.getItems().addAll("Cash", "Online", "Bank Transfer");
        statusComboBox.getItems().addAll("Paid", "Pending", "Overdue");
        
        // Set default date to today
        datePicker.setValue(java.time.LocalDate.now());
        
        // Check if user is logged in as tenant
        if (SessionManager.getInstance().isTenant() && SessionManager.getInstance().getCurrentTenant() != null) {
            // If logged in as tenant, set tenant info and hide selection controls
            TenantDTO currentTenant = SessionManager.getInstance().getCurrentTenant();
            tenantField.setText(currentTenant.getName());
            tenantField.setEditable(false);
            selectedTenantId = currentTenant.getTenantId();
            
            // Hide tenant selection controls for tenant users
            if (tenantComboBox != null) tenantComboBox.setVisible(false);
            if (tenantSelectionLabel != null) tenantSelectionLabel.setVisible(false);
        } else {
            // If logged in as owner, show tenant selection dropdown
            loadTenantsIntoComboBox();
            
            // Hide the tenant text field for owner users
            if (tenantField != null) tenantField.setVisible(false);
        }
    }

    // Load all tenants into the combo box
    private void loadTenantsIntoComboBox() {
        try {
            if (tenantComboBox != null) {
                List<TenantDTO> tenants = tenantService.getAllTenants();
                tenantComboBox.getItems().addAll(tenants);
                
                // Set display format for tenant names
                tenantComboBox.setCellFactory(param -> new ListCell<TenantDTO>() {
                    @Override
                    protected void updateItem(TenantDTO tenant, boolean empty) {
                        super.updateItem(tenant, empty);
                        if (empty || tenant == null) {
                            setText(null);
                        } else {
                            setText(tenant.getName() + " (ID: " + tenant.getTenantId() + ")");
                        }
                    }
                });
                
                // Set display format for selected tenant
                tenantComboBox.setButtonCell(new ListCell<TenantDTO>() {
                    @Override
                    protected void updateItem(TenantDTO tenant, boolean empty) {
                        super.updateItem(tenant, empty);
                        if (empty || tenant == null) {
                            setText("Select Tenant");
                        } else {
                            setText(tenant.getName());
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load tenants: " + e.getMessage());
        }
    }

    // Method to set tenant information (called from parent controller)
    public void setTenantInfo(String tenantName, Long tenantId) {
        if (tenantName != null && !tenantName.isEmpty()) {
            tenantField.setText(tenantName);
            tenantField.setEditable(false); // Make it read-only since we have the ID
        }
        this.selectedTenantId = tenantId;
        
        // Hide tenant selection controls if tenant info is pre-set
        if (tenantComboBox != null) tenantComboBox.setVisible(false);
        if (tenantSelectionLabel != null) tenantSelectionLabel.setVisible(false);
    }

    @FXML
    private void handleCancel() {
        // Close dialog without saving
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void handleSave() {
        boolean valid = true;

        resetFieldStyles();

        // Validate Tenant (either from field or combo box)
        if (SessionManager.getInstance().isTenant()) {
            // For tenants, tenant info should already be set
            if (selectedTenantId == null || selectedTenantId <= 0) {
                valid = false;
            }
        } else {
            // For owners, check if tenant is selected from combo box
            if (tenantComboBox != null && tenantComboBox.isVisible()) {
                TenantDTO selectedTenant = tenantComboBox.getValue();
                if (selectedTenant == null) {
                    markInvalid(tenantComboBox);
                    valid = false;
                } else {
                    selectedTenantId = selectedTenant.getTenantId();
                    tenantField.setText(selectedTenant.getName());
                }
            } else if (selectedTenantId == null || selectedTenantId <= 0) {
                valid = false;
            }
        }

        // Validate Amount
        double amount = 0;
        try {
            if (amountField.getText().trim().isEmpty() || (amount = Double.parseDouble(amountField.getText())) <= 0) {
                markInvalid(amountField);
                valid = false;
            }
        } catch (NumberFormatException e) {
            markInvalid(amountField);
            valid = false;
        }

        // Validate Date
        if (datePicker.getValue() == null) {
            markInvalid(datePicker);
            valid = false;
        }

        // Validate Dropdowns
        if (methodComboBox.getValue() == null) {
            markInvalid(methodComboBox);
            valid = false;
        }

        if (statusComboBox.getValue() == null) {
            markInvalid(statusComboBox);
            valid = false;
        }

        // Stop here if validation fails
        if (!valid) {
            showInlineError("⚠️ Please fill in all fields correctly.");
            return;
        }

        try {
            // If tenant ID is not provided, try to get it from session
            if (selectedTenantId == null || selectedTenantId <= 0) {
                // For tenant dashboard, we can get it from session
                if (SessionManager.getInstance().isTenant() && SessionManager.getInstance().getCurrentTenant() != null) {
                    selectedTenantId = SessionManager.getInstance().getCurrentTenant().getTenantId();
                } else {
                    // Show error that tenant ID is required
                    showAlert(Alert.AlertType.ERROR, "Error", "Tenant ID is required to create a payment.");
                    return;
                }
            }
            
            // If everything is valid → create Payment object and save to backend
            if (selectedTenantId != null && selectedTenantId > 0) {
                // Create PaymentDTO for backend with correct field mappings
                PaymentDTO paymentDTO = new PaymentDTO();
                paymentDTO.setAmount(amount);
                paymentDTO.setModeOfPayment(methodComboBox.getValue());
                paymentDTO.setStatus(statusComboBox.getValue());
                paymentDTO.setPaymentDate(datePicker.getValue().toString());
                // Note: paymentId will be generated by backend
                // Note: transactionId is optional and not in backend DTO
                
                // Save to backend
                PaymentDTO createdPayment = paymentService.createPayment(selectedTenantId, paymentDTO);
                
                // Create frontend Payment object
                newPayment = new Payment(
                        createdPayment.getPaymentDate(),
                        tenantField.getText().trim(),
                        selectedTenantId, // Include tenant ID
                        createdPayment.getAmount(),
                        createdPayment.getStatus(),
                        createdPayment.getModeOfPayment()
                );
                
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Success", "Payment added successfully!");
            } else {
                // Show error that tenant ID is required
                showAlert(Alert.AlertType.ERROR, "Error", "Unable to determine tenant ID. Payment cannot be created.");
            }

            // Close dialog
            ((Stage) saveButton.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add payment: " + e.getMessage());
        }
    }

    private void resetFieldStyles() {
        if (tenantField != null) tenantField.setStyle(null);
        if (tenantComboBox != null) tenantComboBox.setStyle(null);
        amountField.setStyle(null);
        datePicker.setStyle(null);
        methodComboBox.setStyle(null);
        statusComboBox.setStyle(null);
    }

    private void markInvalid(Control control) {
        control.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2; -fx-background-color: rgba(255,240,240,0.9);");
        shakeField(control);
    }

    private void showInlineError(String message) {
        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px; -fx-padding: 5;");
        VBox parent = (VBox) saveButton.getParent().getParent();
        if (!parent.getChildren().contains(errorLabel)) {
            parent.getChildren().add(1, errorLabel);
        }
    }

    private void shakeField(Control control) {
        javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(80), control);
        tt.setFromX(0);
        tt.setByX(8);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Payment getNewPayment() {
        return newPayment;
    }
}