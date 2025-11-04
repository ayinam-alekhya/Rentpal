package com.rentpal.controllers.tenantdashboard;

import com.rentpal.dto.PaymentDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.PaymentService;
import com.rentpal.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class TenantPaymentsController {

    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> filterComboBox;
    
    @FXML
    private TableView<PaymentDTO> paymentsTable;
    
    @FXML
    private TableColumn<PaymentDTO, String> dateColumn;
    
    @FXML
    private TableColumn<PaymentDTO, Double> amountColumn;
    
    @FXML
    private TableColumn<PaymentDTO, String> statusColumn;
    
    @FXML
    private TableColumn<PaymentDTO, String> methodColumn;
    
    private PaymentService paymentService = new PaymentService();
    private ObservableList<PaymentDTO> paymentData = FXCollections.observableArrayList();
    private ObservableList<PaymentDTO> filteredPaymentData = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        // Initialize filter combo box
        filterComboBox.setItems(FXCollections.observableArrayList("All", "Paid", "Pending", "Overdue"));
        filterComboBox.setValue("All");
        
        // Initialize table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        methodColumn.setCellValueFactory(new PropertyValueFactory<>("modeOfPayment"));
        
        // Set table items
        paymentsTable.setItems(filteredPaymentData);
        
        // Add listener to filter combo box
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterPayments());
        
        // Add listener to search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterPayments());
        
        // Load payment data
        loadPaymentData();
    }
    
    private void loadPaymentData() {
        try {
            // Get current tenant from session
            TenantDTO currentTenant = SessionManager.getInstance().getCurrentTenant();
            if (currentTenant != null) {
                Long tenantId = currentTenant.getTenantId();
                
                // Load payments for this tenant
                List<PaymentDTO> payments = paymentService.getPaymentsByTenant(tenantId);
                paymentData.clear();
                paymentData.addAll(payments);
                
                // Update filtered data
                filterPayments();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No tenant session found. Please log in again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load payment data: " + e.getMessage());
        }
    }
    
    private void filterPayments() {
        try {
            String filter = filterComboBox.getValue();
            String search = searchField.getText().toLowerCase();
            
            filteredPaymentData.clear();
            
            for (PaymentDTO payment : paymentData) {
                boolean matchesFilter = "All".equals(filter) || 
                                       (filter != null && filter.equalsIgnoreCase(payment.getStatus()));
                
                boolean matchesSearch = search.isEmpty() || 
                                       String.valueOf(payment.getAmount()).contains(search) ||
                                       payment.getPaymentDate().toLowerCase().contains(search) ||
                                       (payment.getStatus() != null && payment.getStatus().toLowerCase().contains(search)) ||
                                       (payment.getModeOfPayment() != null && payment.getModeOfPayment().toLowerCase().contains(search));
                
                if (matchesFilter && matchesSearch) {
                    filteredPaymentData.add(payment);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}