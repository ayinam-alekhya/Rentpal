package com.rentpal.controllers.ownerdashboard;

import com.rentpal.dto.ComplaintDTO;
import com.rentpal.dto.OwnerDTO;
import com.rentpal.service.ComplaintService;
import com.rentpal.utils.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;                       // <-- JavaFX event
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;              // <-- JavaFX TableColumn
import javafx.scene.control.TableView;                // <-- JavaFX TableView
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class OwnerComplaintsController {

    @FXML private TableView<ComplaintDTO> complaintsTable;
    @FXML private TableColumn<ComplaintDTO, Long> colId;
    @FXML private TableColumn<ComplaintDTO, String> colTitle;
    @FXML private TableColumn<ComplaintDTO, String> colDescription;
    @FXML private TableColumn<ComplaintDTO, String> colStatus;

    @FXML private ComboBox<String> statusCombo;      // e.g. Pending/IN_PROGRESS/RESOLVED
    @FXML private Button updateStatusBtn;

    private final ComplaintService complaintService = new ComplaintService();
    private final ObservableList<ComplaintDTO> complaints = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Table column bindings (match DTO getters)
        colId.setCellValueFactory(new PropertyValueFactory<>("complaintId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        complaintsTable.setItems(complaints);

        // Populate status options (adjust to your backend values)
        statusCombo.setItems(FXCollections.observableArrayList("Pending", "IN_PROGRESS", "RESOLVED"));

        loadComplaintsForOwner();
    }

    private void loadComplaintsForOwner() {
        try {
            OwnerDTO owner = SessionManager.getInstance().getCurrentOwner();
            if (owner == null || owner.getOwnerId() == null) {
                showAlert(Alert.AlertType.ERROR, "No Owner", "You must be logged in as an owner.");
                return;
            }
            List<ComplaintDTO> list = complaintService.getComplaintsByOwner(owner.getOwnerId());
            complaints.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Failed", "Could not load complaints: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateStatus(ActionEvent evt) {
        ComplaintDTO selected = complaintsTable.getSelectionModel().getSelectedItem();
        String newStatus = statusCombo.getValue();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No selection", "Select a complaint first.");
            return;
        }
        if (newStatus == null || newStatus.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "No status", "Choose a status to set.");
            return;
        }

        try {
            ComplaintDTO updated = complaintService.updateComplaintStatus(selected.getComplaintId(), newStatus);
            // Update the table model
            selected.setStatus(updated.getStatus());
            complaintsTable.refresh();
            showAlert(Alert.AlertType.INFORMATION, "Updated", "Status updated to " + updated.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update status: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
