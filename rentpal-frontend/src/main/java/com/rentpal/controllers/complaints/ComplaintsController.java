package com.rentpal.controllers.complaints;

import com.rentpal.dto.ComplaintDTO;
import com.rentpal.service.ComplaintService;
import com.rentpal.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ComplaintsController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private TableView<Complaint> complaintsTable;

    @FXML private TableColumn<Complaint, Number> colId;
    @FXML private TableColumn<Complaint, String> colTenant;
    @FXML private TableColumn<Complaint, String> colIssue;
    @FXML private TableColumn<Complaint, String> colStatus;
    @FXML private TableColumn<Complaint, String> colDate;

    private ObservableList<Complaint> complaintsData;
    private ComplaintService complaintService = new ComplaintService();

    @FXML
    public void initialize() {
        // Setup filters
        filterComboBox.setItems(FXCollections.observableArrayList("All", "Pending", "In Progress", "Resolved"));
        filterComboBox.getSelectionModel().select("All");

        // Map table columns
        colId.setCellValueFactory(cell -> cell.getValue().idProperty());
        colTenant.setCellValueFactory(cell -> cell.getValue().tenantProperty());
        colIssue.setCellValueFactory(cell -> cell.getValue().issueProperty());
        colStatus.setCellValueFactory(cell -> cell.getValue().statusProperty());
        colDate.setCellValueFactory(cell -> cell.getValue().dateProperty());

        // Load complaints from backend
        loadComplaintsFromBackend();
        
        // Add listeners for real-time updates
        addRealTimeUpdateListeners();
    }

    private void loadComplaintsFromBackend() {
        try {
            // Load complaints based on current user (owner or tenant)
            List<ComplaintDTO> complaintDTOs;
            
            if (SessionManager.getInstance().isOwner() && SessionManager.getInstance().getCurrentOwner() != null) {
                // Load complaints for owner
                complaintDTOs = complaintService.getComplaintsByOwner(
                    SessionManager.getInstance().getCurrentOwner().getOwnerId());
            } else if (SessionManager.getInstance().isTenant() && SessionManager.getInstance().getCurrentTenant() != null) {
                // Load complaints for tenant
                complaintDTOs = complaintService.getComplaintsByTenant(
                    SessionManager.getInstance().getCurrentTenant().getTenantId());
            } else {
                // Fallback to dummy owner ID if no session
                complaintDTOs = complaintService.getComplaintsByOwner(1L);
            }
            
            complaintsData = FXCollections.observableArrayList();
            
            for (ComplaintDTO complaintDTO : complaintDTOs) {
                Complaint complaint = new Complaint(
                    complaintDTO.getComplaintId().intValue(),
                    complaintDTO.getTitle(), // Changed from getTenantName() to getTitle()
                    complaintDTO.getDescription(),
                    complaintDTO.getStatus(),
                    complaintDTO.getDateSubmitted() // Changed from getDate() to getDateSubmitted()
                );
                complaintsData.add(complaint);
            }
            
            complaintsTable.setItems(complaintsData);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load complaints: " + e.getMessage());
        }
    }

    private void addRealTimeUpdateListeners() {
        // Add a periodic refresh to check for new complaints
        // This is a simple solution - in a real application, you might use WebSockets or Server-Sent Events
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(
                javafx.util.Duration.seconds(30),
                event -> loadComplaintsFromBackend()
            )
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void handleAddComplaint() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add Complaint");
        alert.setHeaderText(null);
        alert.setContentText("Add Complaint button clicked! (will open form later)");
        alert.showAndWait();
    }
    
    // ✅ Utility method for showing alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // ✅ Method to manually refresh complaints (can be called from other controllers)
    public void refreshComplaints() {
        loadComplaintsFromBackend();
    }
}