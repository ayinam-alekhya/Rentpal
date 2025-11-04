package com.rentpal.controllers.payments;

import com.rentpal.dto.PaymentDTO;
import com.rentpal.service.PaymentService;
import com.rentpal.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import com.rentpal.controllers.payments.AddPaymentController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaymentsController {

    @FXML private Label totalCollected;
    @FXML private Label pendingPayments;
    @FXML private Label overduePayments;
    @FXML private Label refundedPayments;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private TableView<Payment> paymentsTable;

    @FXML private TableColumn<Payment, String> colDate;
    @FXML private TableColumn<Payment, String> colTenant;
    @FXML private TableColumn<Payment, Number> colAmount;
    @FXML private TableColumn<Payment, String> colStatus;
    @FXML private TableColumn<Payment, String> colMethod;

    @FXML private Button addPaymentButton;

    private ObservableList<Payment> paymentData;
    private PaymentService paymentService = new PaymentService();

    @FXML
    public void initialize() {
        // Dropdown filter
        filterComboBox.setItems(FXCollections.observableArrayList("All", "Paid", "Pending", "Overdue"));

        // Table column mappings
        colDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        colTenant.setCellValueFactory(cellData -> cellData.getValue().tenantProperty());
        colAmount.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colMethod.setCellValueFactory(cellData -> cellData.getValue().methodProperty());

        // Load payments from backend
        loadPaymentsFromBackend();

        // Initialize quick stats
        updateStats();
    }

    private void loadPaymentsFromBackend() {
        try {
            List<PaymentDTO> paymentDTOs = paymentService.getAllPayments();
            paymentData = FXCollections.observableArrayList();
            
            for (PaymentDTO paymentDTO : paymentDTOs) {
                // Format the date for display
                String displayDate = formatPaymentDate(paymentDTO.getPaymentDate());
                
                Payment payment = new Payment(
                    displayDate,
                    "Tenant " + (paymentDTO.getPaymentId() != null ? paymentDTO.getPaymentId() : "N/A"), // Placeholder for tenant name
                    0L, // Placeholder for tenant ID - we'll need to get this from the backend
                    paymentDTO.getAmount(),
                    paymentDTO.getStatus(),
                    paymentDTO.getModeOfPayment()
                );
                paymentData.add(payment);
            }
            
            paymentsTable.setItems(paymentData);
            updateStats();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load payments: " + e.getMessage());
        }
    }

    private String formatPaymentDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.isEmpty()) {
                return "N/A";
            }
            // Assuming the backend sends date in "yyyy-MM-dd" format
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        } catch (Exception e) {
            // If parsing fails, return the original string
            return dateStr != null ? dateStr : "N/A";
        }
    }

    // ✅ Add Payment Popup
    @FXML
    private void handleAddPayment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rentpal/fxml/add_payment_dialog.fxml"));

            DialogPane dialogPane = loader.load();

            // ✅ Load dialog CSS for styling
            dialogPane.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());

            AddPaymentController controller = loader.getController();
            
            // Check if a tenant is selected in the payments table
            Payment selectedPayment = paymentsTable.getSelectionModel().getSelectedItem();
            if (selectedPayment != null && selectedPayment.getTenantId() != null && selectedPayment.getTenantId() > 0) {
                // Pass the tenant information to the AddPaymentController
                controller.setTenantInfo(selectedPayment.getTenant(), selectedPayment.getTenantId());
            } else {
                // For owner dashboard, show a dialog to select tenant or enter manually
                // Create a simple dialog to get tenant information
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Enter Tenant ID");
                dialog.setHeaderText("Tenant ID Required");
                dialog.setContentText("Please enter the Tenant ID for this payment:");
                
                // Show the dialog and wait for user input
                dialog.showAndWait().ifPresent(tenantIdStr -> {
                    try {
                        Long tenantId = Long.parseLong(tenantIdStr);
                        controller.setTenantInfo("Manual Entry", tenantId);
                    } catch (NumberFormatException e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Invalid Tenant ID format. Please enter a valid number.");
                        return;
                    }
                });
            }

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Add New Payment");

            // Show the dialog and wait for user to close
            dialog.showAndWait();

            // Retrieve new payment (if user pressed Save)
            Payment newPayment = controller.getNewPayment();
            if (newPayment != null) {
                paymentsTable.getItems().add(newPayment);
                updateStats();
            }

            // Refresh the payment list after adding a new payment
            loadPaymentsFromBackend();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load Add Payment dialog.");
            alert.showAndWait();
        }
    }

    private void updateStats() {
        if (paymentsTable.getItems() == null) {
            return;
        }
        
        double total = paymentsTable.getItems().stream()
                .filter(p -> p.getStatus() != null && (p.getStatus().equals("Paid") || p.getStatus().equals("Full")))
                .mapToDouble(Payment::getAmount)
                .sum();
        totalCollected.setText("$" + String.format("%.2f", total));

        long pending = paymentsTable.getItems().stream()
                .filter(p -> p.getStatus() != null && p.getStatus().equals("Pending"))
                .count();
        pendingPayments.setText(String.valueOf(pending));

        long overdue = paymentsTable.getItems().stream()
                .filter(p -> p.getStatus() != null && p.getStatus().equals("Overdue"))
                .count();
        overduePayments.setText(String.valueOf(overdue));
        
        // Set refunded to 0 for now, as we don't have refunded status in the current data model
        refundedPayments.setText("0");
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