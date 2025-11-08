package com.rentpal.controllers.dashboard;

import com.rentpal.dto.PaymentDTO;
import com.rentpal.service.DashboardService;
import com.rentpal.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardHomeController {

    @FXML private BarChart<String, Number> paymentChart;

    @FXML private Label totalTenants;
    @FXML private Label pendingPayments;
    @FXML private Label activeComplaints;
    @FXML private Label vacantUnits;

    private final DashboardService dashboardService = new DashboardService();

    @FXML
    public void initialize() {
        loadDashboardData();
    }

    private void loadDashboardData() {
        Long ownerId = null;
        try {
            if (SessionManager.getInstance().isOwner()
                    && SessionManager.getInstance().getCurrentOwner() != null) {
                ownerId = SessionManager.getInstance().getCurrentOwner().getOwnerId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadStatistics(ownerId);
        loadChartData(ownerId);
    }

    private void loadStatistics(Long ownerId) {
        try {
            totalTenants.setText(String.valueOf(dashboardService.getTotalTenants(ownerId)));
        } catch (Exception e) {
            e.printStackTrace();
            totalTenants.setText("0");
        }

        try {
            pendingPayments.setText(String.valueOf(dashboardService.getPendingPayments(ownerId)));
        } catch (Exception e) {
            e.printStackTrace();
            pendingPayments.setText("0");
        }

        try {
            activeComplaints.setText(String.valueOf(dashboardService.getActiveComplaints(ownerId)));
        } catch (Exception e) {
            e.printStackTrace();
            activeComplaints.setText("0");
        }

        try {
            vacantUnits.setText(String.valueOf(dashboardService.getVacantUnits(ownerId)));
        } catch (Exception e) {
            e.printStackTrace();
            vacantUnits.setText("0");
        }
    }

    private void loadChartData(Long ownerId) {
        try {
            paymentChart.getData().clear();

            List<PaymentDTO> payments = dashboardService.getPaymentsForOwner(ownerId);

            Map<String, Double> monthly = new HashMap<>();
            DateTimeFormatter inFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("MMM yyyy");

            for (PaymentDTO p : payments) {
                String dateStr = p.getPaymentDate();
                if (dateStr == null || dateStr.isBlank()) continue;
                try {
                    LocalDate d = LocalDate.parse(dateStr.substring(0, Math.min(10, dateStr.length())), inFmt);
                    String key = d.format(outFmt);
                    monthly.merge(key, p.getAmount(), Double::sum);
                } catch (Exception ignore) { /* skip bad dates */ }
            }

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Payments");
            monthly.forEach((month, amount) -> series.getData().add(new XYChart.Data<>(month, amount)));
            paymentChart.getData().add(series);

            paymentChart.applyCss();
            paymentChart.layout();
            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle(
                        "-fx-bar-fill: linear-gradient(to top, #1565c0, #42a5f5); -fx-background-radius: 8;");
                }
            }
            paymentChart.setCategoryGap(25);
            paymentChart.setBarGap(5);
            paymentChart.setLegendVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            loadDefaultChartData();
        }
    }

    private void loadDefaultChartData() {
        paymentChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Payments");
        paymentChart.getData().add(series);
        paymentChart.applyCss();
        paymentChart.layout();
        paymentChart.setCategoryGap(25);
        paymentChart.setBarGap(5);
        paymentChart.setLegendVisible(true);
    }
}
