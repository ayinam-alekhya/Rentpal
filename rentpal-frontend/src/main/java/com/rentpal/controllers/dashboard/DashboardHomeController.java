package com.rentpal.controllers.dashboard;

import com.rentpal.dto.ComplaintDTO;
import com.rentpal.dto.PaymentDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.ComplaintService;
import com.rentpal.service.DashboardService;
import com.rentpal.service.PaymentService;
import com.rentpal.service.TenantService;
import com.rentpal.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DashboardHomeController {

    @FXML
    private BarChart<String, Number> paymentChart;
    
    @FXML
    private Label totalTenants;
    
    @FXML
    private Label pendingPayments;
    
    @FXML
    private Label activeComplaints;
    
    @FXML
    private Label vacantUnits;

    private DashboardService dashboardService = new DashboardService();
    private TenantService tenantService = new TenantService();
    private PaymentService paymentService = new PaymentService();
    private ComplaintService complaintService = new ComplaintService();

    @FXML
    public void initialize() {
        loadDashboardData();
    }
    
    private void loadDashboardData() {
        try {
            // Get current owner ID from session
            Long ownerId = null;
            if (SessionManager.getInstance().isOwner() && 
                SessionManager.getInstance().getCurrentOwner() != null) {
                ownerId = SessionManager.getInstance().getCurrentOwner().getOwnerId();
            }
            
            // Load statistics
            loadStatistics(ownerId);
            
            // Load chart data
            loadChartData();
            
        } catch (Exception e) {
            e.printStackTrace();
            // Set default values in case of error
            setDefaultValues();
        }
    }
    
    private void loadStatistics(Long ownerId) {
        try {
            // Total Tenants
            int tenantCount = dashboardService.getTotalTenants(ownerId);
            totalTenants.setText(String.valueOf(tenantCount));
            
            // Pending Payments
            int pendingCount = dashboardService.getPendingPayments();
            pendingPayments.setText(String.valueOf(pendingCount));
            
            // Active Complaints
            int complaintCount = ownerId != null ? dashboardService.getActiveComplaints(ownerId) : 0;
            activeComplaints.setText(String.valueOf(complaintCount));
            
            // Vacant Units
            int vacantCount = dashboardService.getVacantUnits();
            vacantUnits.setText(String.valueOf(vacantCount));
            
        } catch (Exception e) {
            e.printStackTrace();
            setDefaultValues();
        }
    }
    
    private void loadChartData() {
        try {
            // Clear existing data
            paymentChart.getData().clear();
            
            // Get payment data
            List<PaymentDTO> payments = dashboardService.getPaymentData();
            
            // Group payments by month
            Map<String, Double> monthlyPayments = new HashMap<>();
            
            for (PaymentDTO payment : payments) {
                try {
                    // Parse date and extract month
                    String dateStr = payment.getPaymentDate();
                    if (dateStr != null && !dateStr.isEmpty()) {
                        // Assuming date format is "yyyy-MM-dd"
                        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        String month = date.format(DateTimeFormatter.ofPattern("MMM yyyy"));
                        
                        // Add to monthly total
                        monthlyPayments.merge(month, payment.getAmount(), Double::sum);
                    }
                } catch (Exception e) {
                    // Skip invalid dates
                    continue;
                }
            }
            
            // Create chart series
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Payments");
            
            // Add data to series (sort by month if needed)
            monthlyPayments.forEach((month, amount) -> {
                series.getData().add(new XYChart.Data<>(month, amount));
            });
            
            // Add series to chart
            paymentChart.getData().add(series);
            
            // Style the bars
            paymentChart.applyCss();
            paymentChart.layout();
            
            for (XYChart.Data<String, Number> data : series.getData()) {
                // Set bar color to blue
                data.getNode().setStyle("-fx-bar-fill: linear-gradient(to top, #1565c0, #42a5f5);" +
                        "-fx-background-radius: 8;");
            }
            
            // Adjust chart spacing
            paymentChart.setCategoryGap(25);
            paymentChart.setBarGap(5);
            paymentChart.setLegendVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            // Load default chart data if there's an error
            loadDefaultChartData();
        }
    }
    
    private void loadDefaultChartData() {
        // Clear existing data
        paymentChart.getData().clear();
        
        // Create empty chart data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Payments");
        
        // Add data to chart
        paymentChart.getData().add(series);
        
        // Style the bars
        paymentChart.applyCss();
        paymentChart.layout();
        
        // Adjust chart spacing
        paymentChart.setCategoryGap(25);
        paymentChart.setBarGap(5);
        paymentChart.setLegendVisible(true);
    }
    
    private void setDefaultValues() {
        totalTenants.setText("0");
        pendingPayments.setText("0");
        activeComplaints.setText("0");
        vacantUnits.setText("0");
        loadDefaultChartData();
    }
}