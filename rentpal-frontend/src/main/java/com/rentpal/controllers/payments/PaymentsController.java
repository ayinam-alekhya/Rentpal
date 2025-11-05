package com.rentpal.controllers.payments;

import com.rentpal.dto.PaymentDTO;
import com.rentpal.service.PaymentService;
import com.rentpal.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;          // TableView, TableColumn, Button, Label, ComboBox, etc.
import javafx.stage.Modality;
import javafx.stage.Stage;

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
            // Open the Add Payment FORM (with the tenant dropdown)
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/rentpal/fxml/add_payment_dialog.fxml")); 
            Parent root = loader.load();

            AddPaymentController controller = loader.getController();

            // Optional: if you want to preselect a tenant based on the selected row
            Payment selected = paymentsTable.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getTenantId() != null && selected.getTenantId() > 0) {
                controller.setTenantInfo(selected.getTenant(), selected.getTenantId());
            }
            // If nothing is selected, the form will show the ComboBox for owner to choose

            // Show as a modal dialog (no TextInputDialog)
            Stage stage = new Stage();
            stage.setTitle("+ Add Payment");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();

            // If a payment was created, refresh table & stats
            Payment newPayment = controller.getNewPayment();
            if (newPayment != null) {
                // Either append or just reload from backend:
                // paymentsTable.getItems().add(newPayment);
                loadPaymentsFromBackend(); // keeps things in sync with backend
                updateStats();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Add Payment: " + e.getMessage());
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