package com.rentpal.controllers.ownerdashboard;

import com.rentpal.dto.ComplaintDTO;
import com.rentpal.service.ComplaintService;
import com.rentpal.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class OwnerComplaintsController {

    @FXML private TableView<ComplaintDTO> table;
    @FXML private TableColumn<ComplaintDTO, Long> colId;
    @FXML private TableColumn<ComplaintDTO, String> colTenant;
    @FXML private TableColumn<ComplaintDTO, String> colIssue;
    @FXML private TableColumn<ComplaintDTO, String> colStatus;
    @FXML private TableColumn<ComplaintDTO, String> colDate;

    @FXML private ComboBox<String> filterCombo;
    @FXML private Button markInProgressBtn;
    @FXML private Button markResolvedBtn;

    private final ComplaintService complaintService = new ComplaintService();
    private final ObservableList<ComplaintDTO> complaints = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Map FXML columns
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleLongProperty(c.getValue().getComplaintId()).asObject());
        colTenant.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getOwnerId() != null ? "Tenant of Owner #" + c.getValue().getOwnerId() : "N/A"));
        colIssue.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        colDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDateSubmitted()));

        table.setItems(complaints);

        // Populate dropdown
        filterCombo.setItems(FXCollections.observableArrayList("ALL", "PENDING", "IN_PROGRESS", "RESOLVED"));
        filterCombo.getSelectionModel().select("ALL");

        filterCombo.valueProperty().addListener((obs, old, val) -> reload());
        reload();

        // Status change buttons
        markInProgressBtn.setOnAction(e -> updateStatus("IN_PROGRESS"));
        markResolvedBtn.setOnAction(e -> updateStatus("RESOLVED"));
    }

    private void reload() {
        try {
            Long ownerId = SessionManager.getInstance().getCurrentOwner().getOwnerId();
            String status = filterCombo.getValue();
            List<ComplaintDTO> list = complaintService.getComplaintsByOwner(ownerId, status);
            complaints.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load complaints: " + e.getMessage());
        }
    }

    private void updateStatus(String newStatus) {
        ComplaintDTO selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a complaint first.");
            return;
        }
        try {
            complaintService.updateComplaintStatus(selected.getComplaintId(), newStatus);
            reload(); // refresh table
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update complaint status: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
