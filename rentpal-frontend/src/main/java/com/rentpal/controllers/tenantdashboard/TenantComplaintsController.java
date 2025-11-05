package com.rentpal.controllers.tenantdashboard;

import com.rentpal.dto.ComplaintDTO;
import com.rentpal.dto.TenantDTO;
import com.rentpal.service.ComplaintService;
import com.rentpal.utils.SessionManager;
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

public class TenantComplaintsController {

    @FXML private TextField searchComplaints;
    @FXML private ComboBox<String> filterComboBox;

    @FXML private TableView<ComplaintDTO> complaintsTable;
    @FXML private TableColumn<ComplaintDTO, Long>   colId;
    @FXML private TableColumn<ComplaintDTO, String> colCategory;
    @FXML private TableColumn<ComplaintDTO, String> colDescription;
    @FXML private TableColumn<ComplaintDTO, String> colStatus;   // <-- NEW

    @FXML private Button addComplaint;

    private final ComplaintService complaintService = new ComplaintService();
    private final ObservableList<ComplaintDTO> complaintData         = FXCollections.observableArrayList();
    private final ObservableList<ComplaintDTO> filteredComplaintData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Use the exact statuses your backend returns (adjust if yours are different)
        // Common: "Pending", "In Progress", "Resolved"
        filterComboBox.setItems(FXCollections.observableArrayList("All", "Pending", "In Progress", "Resolved"));
        filterComboBox.setValue("All");

        // Table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("complaintId"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));   // <-- show status

        complaintsTable.setItems(filteredComplaintData);

        // Listeners
        filterComboBox.valueProperty().addListener((obs, o, n) -> filterComplaints());
        searchComplaints.textProperty().addListener((obs, o, n) -> filterComplaints());

        // Load data
        loadComplaintData();
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

            loadComplaintData(); // refresh after add
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open add complaint form: " + e.getMessage());
        }
    }

    public void loadComplaintData() {
        try {
            TenantDTO currentTenant = SessionManager.getInstance().getCurrentTenant();
            if (currentTenant != null) {
                Long tenantId = currentTenant.getTenantId();
                List<ComplaintDTO> complaints = complaintService.getComplaintsByTenant(tenantId);
                complaintData.setAll(complaints);
                filterComplaints();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load complaint data: " + e.getMessage());
        }
    }

    private void filterComplaints() {
        String filter = filterComboBox.getValue();
        String search = searchComplaints.getText() == null ? "" : searchComplaints.getText().toLowerCase();

        filteredComplaintData.clear();

        for (ComplaintDTO c : complaintData) {
            String status = c.getStatus() == null ? "" : c.getStatus();
            boolean statusMatch = "All".equalsIgnoreCase(filter) || filter.equalsIgnoreCase(status);

            boolean searchMatch =
                    search.isEmpty()
                    || (c.getTitle() != null && c.getTitle().toLowerCase().contains(search))
                    || (c.getDescription() != null && c.getDescription().toLowerCase().contains(search))
                    || String.valueOf(c.getComplaintId()).contains(search);

            if (statusMatch && searchMatch) {
                filteredComplaintData.add(c);
            }
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
