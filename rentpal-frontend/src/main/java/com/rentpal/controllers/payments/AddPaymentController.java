package com.rentpal.controllers.payments;

import com.rentpal.dto.PaymentDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.PaymentService;
import com.rentpal.service.TenantService;
import com.rentpal.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class AddPaymentController {

    @FXML private ComboBox<TenantDTO> tenantComboBox;     // Only selector we keep
    @FXML private Label tenantSelectionLabel;

    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> methodComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private Payment newPayment; // your table-row view model
    private final PaymentService paymentService = new PaymentService();
    private final TenantService tenantService = new TenantService();
    private Long selectedTenantId; // who this payment is for

    @FXML
    public void initialize() {
        // dropdowns
        methodComboBox.getItems().setAll("Cash", "Online", "Bank Transfer");
        statusComboBox.getItems().setAll("Paid", "Pending", "Overdue");
        datePicker.setValue(java.time.LocalDate.now());

        if (SessionManager.getInstance().isTenant()
                && SessionManager.getInstance().getCurrentTenant() != null) {
            // Tenant logged in → lock to that tenant, hide selector
            TenantDTO t = SessionManager.getInstance().getCurrentTenant();
            selectedTenantId = t.getTenantId();
            tenantComboBox.setVisible(false);
            tenantSelectionLabel.setVisible(false);
        } else {
            // Owner flow → load all tenants to combo
            loadTenantsIntoComboBox();
        }
    }

    /** Populate owners’ tenant dropdown. */
 // AddPaymentController.java
    private void loadTenantsIntoComboBox() {
        try {
            // 1) Get current owner id from the session
            if (SessionManager.getInstance().getCurrentOwner() == null) {
                showAlert(Alert.AlertType.ERROR, "Session", "Owner session not found. Please log in again.");
                tenantComboBox.setDisable(true);
                return;
            }
            Long ownerId = SessionManager.getInstance().getCurrentOwner().getOwnerId();
            System.out.println("[AddPayment] loading tenants for ownerId=" + ownerId);

            // 2) Fetch ONLY this owner's tenants
            List<TenantDTO> tenants = tenantService.getTenantsForOwner(ownerId);

            // 3) Defensive: remove nulls / missing ids
            tenants.removeIf(t -> t == null || t.getTenantId() == null);

            // 4) Fill combo
            tenantComboBox.getItems().setAll(tenants);

            // 5) Pretty render + selection text
            tenantComboBox.setCellFactory(list -> new ListCell<>() {
                @Override protected void updateItem(TenantDTO t, boolean empty) {
                    super.updateItem(t, empty);
                    setText(empty || t == null ? null : t.getName() + "  (ID: " + t.getTenantId() + ")");
                }
            });
            tenantComboBox.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(TenantDTO t, boolean empty) {
                    super.updateItem(t, empty);
                    setText(empty || t == null ? "Select Tenant" : t.getName());
                }
            });

            // 6) Optional: preselect first tenant to avoid null id
            if (!tenants.isEmpty()) {
                tenantComboBox.getSelectionModel().select(0);
                selectedTenantId = tenantComboBox.getValue().getTenantId();
                System.out.println("[AddPayment] preselected tenantId=" + selectedTenantId);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "No Tenants",
                        "You have no tenants mapped to this owner yet.");
            }

            // 7) Keep selectedTenantId in sync if user changes dropdown
            tenantComboBox.valueProperty().addListener((obs, o, n) -> {
                selectedTenantId = (n == null) ? null : n.getTenantId();
                System.out.println("[AddPayment] changed selection tenantId=" + selectedTenantId);
            });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Tenants Failed", e.getMessage());
            tenantComboBox.setDisable(true);
        }
    }

    /** Optional: parent can preselect a tenant (e.g., when a row was selected). */
    public void preselectTenant(Long tenantId, String tenantName) {
        selectedTenantId = tenantId;
        // Try to select it in combo if visible
        if (tenantComboBox != null && tenantComboBox.isVisible() && tenantId != null) {
            tenantComboBox.getItems().stream()
                .filter(t -> tenantId.equals(t.getTenantId()))
                .findFirst()
                .ifPresent(tenantComboBox::setValue);
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void handleSave() {
        boolean valid = true;
        resetFieldStyles();

        // Resolve tenant id
        if (tenantComboBox != null && tenantComboBox.isVisible()) {
            TenantDTO sel = tenantComboBox.getValue();
            if (sel == null) { markInvalid(tenantComboBox); valid = false; }
            else selectedTenantId = sel.getTenantId();
        } else if (selectedTenantId == null || selectedTenantId <= 0) {
            valid = false; // tenant session missing
        }

        // Validate amount
        double amount = 0;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) { markInvalid(amountField); valid = false; }
        } catch (Exception ex) { markInvalid(amountField); valid = false; }

        if (datePicker.getValue() == null) { markInvalid(datePicker); valid = false; }
        if (methodComboBox.getValue() == null) { markInvalid(methodComboBox); valid = false; }
        if (statusComboBox.getValue() == null) { markInvalid(statusComboBox); valid = false; }

        if (!valid) {
            showInlineError("⚠️ Please fill in all fields correctly.");
            return;
        }

        try {
            // Build DTO for backend
            PaymentDTO dto = new PaymentDTO();
            dto.setAmount(amount);
            dto.setModeOfPayment(methodComboBox.getValue());
            dto.setStatus(statusComboBox.getValue());
            dto.setPaymentDate(datePicker.getValue().toString()); // yyyy-MM-dd

            PaymentDTO created = paymentService.createPayment(selectedTenantId, dto);

            // Build table row view model (your existing Payment class)
            String tenantName = resolveTenantNameForRow();
            newPayment = new Payment(
                created.getPaymentId(),
                created.getPaymentDate(),   // display date (you format in table)
                tenantName,                 // tenant name for the table
                selectedTenantId,           // tenant id
                created.getAmount(),
                created.getStatus(),
                created.getModeOfPayment()
            );

            showAlert(Alert.AlertType.INFORMATION, "Success", "Payment added successfully!");
            ((Stage) saveButton.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add payment: " + e.getMessage());
        }
    }

    private String resolveTenantNameForRow() {
        if (tenantComboBox != null && tenantComboBox.isVisible() && tenantComboBox.getValue() != null) {
            return tenantComboBox.getValue().getName();
        }
        if (SessionManager.getInstance().isTenant() && SessionManager.getInstance().getCurrentTenant() != null) {
            return SessionManager.getInstance().getCurrentTenant().getName();
        }
        // fallback if preselected by id only
        if (tenantComboBox != null) {
            return tenantComboBox.getItems().stream()
                    .filter(t -> t.getTenantId().equals(selectedTenantId))
                    .map(TenantDTO::getName)
                    .findFirst().orElse("Tenant");
        }
        return "Tenant";
        }

    private void resetFieldStyles() {
        if (tenantComboBox != null) tenantComboBox.setStyle(null);
        amountField.setStyle(null);
        datePicker.setStyle(null);
        methodComboBox.setStyle(null);
        statusComboBox.setStyle(null);
    }

    private void markInvalid(Control c) {
        c.setStyle("-fx-border-color:#e74c3c; -fx-border-width:2; -fx-background-color:rgba(255,240,240,0.9);");
        javafx.animation.TranslateTransition tt =
            new javafx.animation.TranslateTransition(javafx.util.Duration.millis(80), c);
        tt.setFromX(0); tt.setByX(8); tt.setCycleCount(6); tt.setAutoReverse(true); tt.play();
    }

    private void showInlineError(String msg) {
        Label error = new Label(msg);
        error.setStyle("-fx-text-fill:#e74c3c; -fx-font-size:12px; -fx-padding:5;");
        VBox parent = (VBox) saveButton.getParent().getParent();
        if (!parent.getChildren().contains(error)) parent.getChildren().add(1, error);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(message); a.showAndWait();
    }

    public Payment getNewPayment() { return newPayment; }
}
