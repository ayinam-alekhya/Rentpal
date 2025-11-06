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
    private final ObservableList<Payment> allPayments     = FXCollections.observableArrayList();
    private final ObservableList<Payment> filteredPayments = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
       filterComboBox.setItems(FXCollections.observableArrayList("ALL", "PAID", "PENDING", "OVERDUE"));

        colDate.setCellValueFactory(c -> c.getValue().dateProperty());
        colTenant.setCellValueFactory(c -> c.getValue().tenantProperty());
        colAmount.setCellValueFactory(c -> c.getValue().amountProperty());
        colStatus.setCellValueFactory(c -> c.getValue().statusProperty());
        colMethod.setCellValueFactory(c -> c.getValue().methodProperty());

        paymentsTable.setItems(filteredPayments);

        // listeners
        filterComboBox.getSelectionModel().select("ALL");
        filterComboBox.valueProperty().addListener((o, ov, nv) -> applyPaymentsFilter());
        searchField.textProperty().addListener((o, ov, nv) -> applyPaymentsFilter());

        loadPaymentsFromBackend();  // fills allPayments then apply filter
        updateStats();
    }

    private void loadPaymentsFromBackend() {
        try {
            List<PaymentDTO> paymentDTOs = paymentService.getAllPayments();
            allPayments.clear();
            for (PaymentDTO dto : paymentDTOs) {
                String displayDate = formatPaymentDate(dto.getPaymentDate()); // keep your formatter
                allPayments.add(new Payment(
                        displayDate,
                        // TODO: replace placeholder with real tenant name if your API returns it
                        "Tenant " + (dto.getPaymentId() != null ? dto.getPaymentId() : "N/A"),
                        0L,
                        dto.getAmount(),
                        normalize(dto.getStatus()),
                        dto.getModeOfPayment()
                ));
            }
            applyPaymentsFilter();   // <- refresh table based on current dropdown + search
            updateStats();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load payments: " + e.getMessage());
        }
    }
    private void applyPaymentsFilter() {
        String wanted = normalize(filterComboBox.getValue()); // "ALL"/"PAID"/"PENDING"/"OVERDUE"
        String q = (searchField.getText() == null) ? "" : searchField.getText().trim().toLowerCase();

        filteredPayments.setAll(
            allPayments.stream()
                .filter(p -> "ALL".equals(wanted) || normalize(p.getStatus()).equals(wanted))
                .filter(p -> q.isEmpty()
                        || (p.getTenant() != null && p.getTenant().toLowerCase().contains(q))
                        || String.valueOf(p.getAmount()).contains(q)
                        || (p.getMethod() != null && p.getMethod().toLowerCase().contains(q))
                        || (p.getDate() != null && p.getDate().toLowerCase().contains(q)))
                .toList()
        );
    }

    private String normalize(String s) {
        return (s == null) ? "" : s.trim().toUpperCase();
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
            dialogPane.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());

            AddPaymentController controller = loader.getController();

            // OPTIONAL: preselect if a row is highlighted (define preselectTenant in the dialog controller)
            Payment sel = paymentsTable.getSelectionModel().getSelectedItem();
            if (sel != null && sel.getTenantId() != null && sel.getTenantId() > 0) {
                controller.preselectTenant(sel.getTenantId(), sel.getTenant());
            }

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Add New Payment");
            dialog.showAndWait();

            Payment newPayment = controller.getNewPayment();
            if (newPayment != null) {
                allPayments.add(newPayment);  // add to SOURCE
                applyPaymentsFilter();        // re-filter into the table
                updateStats();
            }

            // If you must refetch from server, do this instead of the two lines above:
            // loadPaymentsFromBackend();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load Add Payment dialog: " + e.getMessage());
        }
    }

    private void updateStats() {
        var items = filteredPayments.isEmpty() ? allPayments : filteredPayments;

        double total = items.stream()
                .filter(p -> "PAID".equals(normalize(p.getStatus())) || "FULL".equals(normalize(p.getStatus())))
                .mapToDouble(Payment::getAmount)
                .sum();
        totalCollected.setText("$" + String.format("%.2f", total));

        long pending = items.stream().filter(p -> "PENDING".equals(normalize(p.getStatus()))).count();
        pendingPayments.setText(String.valueOf(pending));

        long overdue = items.stream().filter(p -> "OVERDUE".equals(normalize(p.getStatus()))).count();
        overduePayments.setText(String.valueOf(overdue));

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