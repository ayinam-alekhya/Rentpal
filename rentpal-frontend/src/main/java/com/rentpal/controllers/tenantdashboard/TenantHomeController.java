package com.rentpal.controllers.tenantdashboard;

import com.rentpal.dto.ComplaintDTO;
import com.rentpal.dto.PaymentDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.ComplaintService;
import com.rentpal.service.PaymentService;
import com.rentpal.service.TenantService;
import com.rentpal.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class TenantHomeController {

    @FXML
    private Label totalRentPaid;
    
    @FXML
    private Label pendingDues;

    @FXML
    private Label overdues;

    
    @FXML
    private Label complaintsRaised;
    
    @FXML
    private TableView<PaymentDTO> recentPaymentsTable;
    
    @FXML
    private TableColumn<PaymentDTO, String> dateColumn;
    
    @FXML
    private TableColumn<PaymentDTO, String> descriptionColumn;
    
    @FXML
    private TableColumn<PaymentDTO, String> statusColumn;
    
    @FXML
    private TableView<ComplaintDTO> recentComplaintsTable;
    
    @FXML
    private TableColumn<ComplaintDTO, String> complaintDateColumn;
    
    @FXML
    private TableColumn<ComplaintDTO, String> complaintDescriptionColumn;
    
    @FXML
    private TableColumn<ComplaintDTO, String> complaintStatusColumn;
    
    private PaymentService paymentService = new PaymentService();
    private ComplaintService complaintService = new ComplaintService();
    private TenantService tenantService = new TenantService();
    
    private ObservableList<PaymentDTO> paymentData = FXCollections.observableArrayList();
    private ObservableList<ComplaintDTO> complaintData = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        System.out.println("=== TenantHomeController.initialize() START ===");
        
        try {
            // Initialize table columns
            initializeTableColumns();
            
            // Set table items
            setTableItems();
            
            // Load tenant data
            System.out.println("Calling loadTenantData()");
            loadTenantData();
            System.out.println("=== TenantHomeController.initialize() END ===");
        } catch (Exception e) {
            System.err.println("Error in initialize(): " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeTableColumns() {
        System.out.println("Initializing table columns...");
        
        try {
            if (dateColumn != null) {
                dateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
                System.out.println("dateColumn initialized");
            } else {
                System.err.println("dateColumn is null");
            }
            
            if (descriptionColumn != null) {
                descriptionColumn.setCellValueFactory(cellData -> {
                    PaymentDTO payment = cellData.getValue();
                    return new javafx.beans.property.SimpleStringProperty("Payment of $" + payment.getAmount());
                });
                System.out.println("descriptionColumn initialized");
            } else {
                System.err.println("descriptionColumn is null");
            }
            
            if (statusColumn != null) {
                statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
                System.out.println("statusColumn initialized");
            } else {
                System.err.println("statusColumn is null");
            }
            
            if (complaintDateColumn != null) {
                complaintDateColumn.setCellValueFactory(new PropertyValueFactory<>("complaintId"));
                System.out.println("complaintDateColumn initialized");
            } else {
                System.err.println("complaintDateColumn is null");
            }
            
            if (complaintDescriptionColumn != null) {
                complaintDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
                System.out.println("complaintDescriptionColumn initialized");
            } else {
                System.err.println("complaintDescriptionColumn is null");
            }
            
            if (complaintStatusColumn != null) {
                complaintStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
                System.out.println("complaintStatusColumn initialized");
            } else {
                System.err.println("complaintStatusColumn is null");
            }
        } catch (Exception e) {
            System.err.println("Error initializing table columns: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setTableItems() {
        System.out.println("Setting table items...");
        
        try {
            if (recentPaymentsTable != null) {
                recentPaymentsTable.setItems(paymentData);
                System.out.println("recentPaymentsTable items set");
            } else {
                System.err.println("recentPaymentsTable is null");
            }
            
            if (recentComplaintsTable != null) {
                recentComplaintsTable.setItems(complaintData);
                System.out.println("recentComplaintsTable items set");
            } else {
                System.err.println("recentComplaintsTable is null");
            }
        } catch (Exception e) {
            System.err.println("Error setting table items: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadTenantData() {
        try {
            System.out.println("TenantHomeController.loadTenantData() called");
            
            // Get current tenant from session
            TenantDTO currentTenant = SessionManager.getInstance().getCurrentTenant();
            System.out.println("Current tenant from session: " + currentTenant);
            
            if (currentTenant != null) {
                System.out.println("Tenant ID: " + currentTenant.getTenantId());
                Long tenantId = currentTenant.getTenantId();
                
                // Load payments for this tenant
                System.out.println("Calling loadPayments()");
                loadPayments(tenantId);
                
                // Load complaints for this tenant
                System.out.println("Calling loadComplaints()");
                loadComplaints(tenantId);
                
                // Update statistics
                System.out.println("Calling updateStatistics()");
                updateStatistics();
                System.out.println("updateStatistics() completed");
            } else {
                System.out.println("No tenant found in session");
                // Set default values
                if (totalRentPaid != null) totalRentPaid.setText("$0.00");
                if (pendingDues != null) pendingDues.setText("$0.00");
                if (complaintsRaised != null) complaintsRaised.setText("0");
            }
        } catch (Exception e) {
            System.err.println("Error in loadTenantData(): " + e.getMessage());
            e.printStackTrace();
            // Set default values in case of error
            try {
                if (totalRentPaid != null) totalRentPaid.setText("$0.00");
                if (pendingDues != null) pendingDues.setText("$0.00");
                if (complaintsRaised != null) complaintsRaised.setText("0");
            } catch (Exception ex) {
                System.err.println("Error setting default values: " + ex.getMessage());
            }
        }
    }
    
    private void loadPayments(Long tenantId) {
        try {
            System.out.println("Loading payments for tenant ID: " + tenantId);
            List<PaymentDTO> payments = paymentService.getPaymentsByTenant(tenantId);
            System.out.println("Found " + payments.size() + " payments");
            paymentData.clear();
            paymentData.addAll(payments);
        } catch (Exception e) {
            System.err.println("Error loading payments for tenant ID " + tenantId + ": " + e.getMessage());
            e.printStackTrace();
            // Show error in UI
            try {
                if (recentPaymentsTable != null) {
                    // Create a simple error message in the table
                    PaymentDTO errorPayment = new PaymentDTO();
                    errorPayment.setPaymentDate("Error");
                    errorPayment.setAmount(0.0);
                    errorPayment.setStatus("Failed to load payments: " + e.getMessage());
                    paymentData.clear();
                    paymentData.add(errorPayment);
                }
            } catch (Exception ex) {
                System.err.println("Error showing payment error in UI: " + ex.getMessage());
            }
        }
    }
    
    private void loadComplaints(Long tenantId) {
        try {
            System.out.println("Loading complaints for tenant ID: " + tenantId);
            List<ComplaintDTO> complaints = complaintService.getComplaintsByTenant(tenantId);
            System.out.println("Found " + complaints.size() + " complaints");
            complaintData.clear();
            complaintData.addAll(complaints);
        } catch (Exception e) {
            System.err.println("Error loading complaints for tenant ID " + tenantId + ": " + e.getMessage());
            e.printStackTrace();
            // Show error in UI
            try {
                if (recentComplaintsTable != null) {
                    // Create a simple error message in the table
                    ComplaintDTO errorComplaint = new ComplaintDTO();
                    errorComplaint.setComplaintId(0L);
                    errorComplaint.setDescription("Error");
                    errorComplaint.setStatus("Failed to load complaints: " + e.getMessage());
                    complaintData.clear();
                    complaintData.add(errorComplaint);
                }
            } catch (Exception ex) {
                System.err.println("Error showing complaint error in UI: " + ex.getMessage());
            }
        }
    }
    
    private void updateStatistics() {
        try {
            // Total rent paid (as you have)
            double totalPaid = paymentData.stream()
                    .filter(p -> "Paid".equalsIgnoreCase(p.getStatus())
                            || "Completed".equalsIgnoreCase(p.getStatus()))
                    .mapToDouble(PaymentDTO::getAmount)
                    .sum();
            if (totalRentPaid != null) {
                totalRentPaid.setText(String.format("$%.2f", totalPaid));
            }

            // Pending dues from tenant session (as you have)
            TenantDTO currentTenant = SessionManager.getInstance().getCurrentTenant();
            if (currentTenant != null) {
                double remainingRent = currentTenant.getRemainingRent();
                if (pendingDues != null) {
                    pendingDues.setText(String.format("$%.2f", remainingRent));
                }
            } else if (pendingDues != null) {
                pendingDues.setText("$0.00");
            }

            // Complaints count (as you have)
            int complaintCount = complaintData.size();
            if (complaintsRaised != null) {
                complaintsRaised.setText(String.valueOf(complaintCount));
            }

            // 🆕 Overdues count = number of payments with status OVERDUE
            long overdueCount = paymentData.stream()
                    .filter(p -> "OVERDUE".equalsIgnoreCase(p.getStatus()))
                    .count();
            if (overdues != null) {
                overdues.setText(String.valueOf(overdueCount));
            }

        } catch (Exception e) {
            // fallbacks
            try {
                if (totalRentPaid != null) totalRentPaid.setText("$0.00");
                if (pendingDues != null) pendingDues.setText("$0.00");
                if (complaintsRaised != null) complaintsRaised.setText("0");
                if (overdues != null) overdues.setText("0"); // 🆕
            } catch (Exception ignored) {}
        }
    }

}