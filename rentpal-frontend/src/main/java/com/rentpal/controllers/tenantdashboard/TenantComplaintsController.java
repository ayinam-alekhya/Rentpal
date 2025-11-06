package com.rentpal.controllers.tenantdashboard;

import com.rentpal.dto.ComplaintDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.ComplaintService;
import com.rentpal.utils.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TenantComplaintsController {

    @FXML private TextField searchComplaints;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private TableView<ComplaintDTO> complaintsTable;
    @FXML private TableColumn<ComplaintDTO, Long> colId;
    @FXML private TableColumn<ComplaintDTO, String> colCategory;
    @FXML private TableColumn<ComplaintDTO, String> colDescription;
    // NEW: show status
    @FXML private TableColumn<ComplaintDTO, String> colStatus;

    @FXML private Button addComplaint;

    private final ComplaintService complaintService = new ComplaintService();
    private final ObservableList<ComplaintDTO> complaintData = FXCollections.observableArrayList();       // full set from server (by status)
    private final ObservableList<ComplaintDTO> filteredComplaintData = FXCollections.observableArrayList(); // searched subset

    @FXML
    public void initialize() {
        // Columns
        colId.setCellValueFactory(new PropertyValueFactory<>("complaintId"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getStatus() == null ? "PENDING" : c.getValue().getStatus()
        ));
                // Use backend-consistent status names
        filterComboBox.setItems(FXCollections.observableArrayList("ALL", "PENDING", "IN_PROGRESS", "RESOLVED"));
        filterComboBox.setValue("ALL");

        complaintsTable.setItems(filteredComplaintData);

        // When filter changes -> fetch from server using that status
        filterComboBox.valueProperty().addListener((obs, oldV, newV) -> reloadFromServer());

        // Local search only
        searchComplaints.textProperty().addListener((obs, oldV, newV) -> applySearch());

        // Initial load
        reloadFromServer();
    }

    @FXML
    private void handleAddComplaint() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rentpal/fxml/add_complaint_form.fxml"));
            Parent root = loader.load();

            root.getStylesheets().add(getClass().getResource("/css/tenant_dashboard.css").toExternalForm());

            Stage popup = new Stage();
            popup.setTitle("Add Complaint");
            popup.setScene(new Scene(root));
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setResizable(false);
            popup.showAndWait();

            // Refresh using current status filter after adding
            reloadFromServer();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open add complaint form: " + e.getMessage());
        }
    }

    public void reloadFromServer() {
        try {
            TenantDTO currentTenant = SessionManager.getInstance().getCurrentTenant();
            if (currentTenant == null) return;

            Long tenantId = currentTenant.getTenantId();
            String status = normalizeToBackendStatus(filterComboBox.getValue());

            // ask backend for list already filtered by status
            List<ComplaintDTO> list = complaintService.getComplaintsByTenant(tenantId, status);
            complaintData.setAll(list);

            applySearch();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load complaint data: " + e.getMessage());
        }
    }

    private void applySearch() {
        String q = (searchComplaints.getText() == null) ? "" : searchComplaints.getText().trim().toLowerCase(Locale.ROOT);

        if (q.isEmpty()) {
            filteredComplaintData.setAll(complaintData);
        } else {
            filteredComplaintData.setAll(
                complaintData.stream()
                    .filter(c ->
                        (c.getTitle() != null && c.getTitle().toLowerCase(Locale.ROOT).contains(q)) ||
                        (c.getDescription() != null && c.getDescription().toLowerCase(Locale.ROOT).contains(q)) ||
                        (String.valueOf(c.getComplaintId()).contains(q))
                    )
                    .collect(Collectors.toList())
            );
        }
    }

    private String normalizeToBackendStatus(String ui) {
        if (ui == null) return "ALL";
        // Already using backend names in the combo; keep a mapper in case FXML/text changes later
        switch (ui.toUpperCase(Locale.ROOT)) {
            case "ALL": return "ALL";
            case "PENDING": return "PENDING";
            case "IN_PROGRESS": return "IN_PROGRESS";
            case "RESOLVED": return "RESOLVED";
            // legacy labels just in case
            case "OPEN": return "PENDING";
            default: return "ALL";
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
