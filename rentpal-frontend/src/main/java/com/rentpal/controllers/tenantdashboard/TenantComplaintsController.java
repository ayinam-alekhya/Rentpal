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

    @FXML
    private TextField searchComplaints;
    
    @FXML
    private ComboBox<String> filterComboBox;
    
    @FXML
    private TableView<ComplaintDTO> complaintsTable;
    
    @FXML
    private TableColumn<ComplaintDTO, Long> colId;
    
    @FXML
    private TableColumn<ComplaintDTO, String> colCategory;
    
    @FXML
    private TableColumn<ComplaintDTO, String> colDescription;
    
    @FXML
    private Button addComplaint;
    
    private ComplaintService complaintService = new ComplaintService();
    private ObservableList<ComplaintDTO> complaintData = FXCollections.observableArrayList();
    private ObservableList<ComplaintDTO> filteredComplaintData = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        // Initialize filter combo box
        filterComboBox.setItems(FXCollections.observableArrayList("All", "Open", "In Progress", "Resolved"));
        filterComboBox.setValue("All");
        
        // Initialize table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("complaintId"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        // Set table items
        complaintsTable.setItems(filteredComplaintData);
        
        // Add listeners
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterComplaints());
        searchComplaints.textProperty().addListener((observable, oldValue, newValue) -> filterComplaints());
        
        // Load complaint data
        loadComplaintData();
    }
    
    @FXML
    private void handleAddComplaint() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rentpal/fxml/add_complaint_form.fxml"));
            Parent root = loader.load();

            root.getStylesheets().add(
                    getClass().getResource("/css/tenant_dashboard.css").toExternalForm()
            );

            Stage popup = new Stage();
            popup.setTitle("Add Complaint");
            popup.setScene(new Scene(root));
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setResizable(false);
            popup.showAndWait();
            
            // Refresh complaints after adding
            loadComplaintData();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open add complaint form: " + e.getMessage());
        }
    }
    
    public void loadComplaintData() {
        try {
            // Get current tenant from session
            TenantDTO currentTenant = SessionManager.getInstance().getCurrentTenant();
            if (currentTenant != null) {
                Long tenantId = currentTenant.getTenantId();
                
                // Load complaints for this tenant
                List<ComplaintDTO> complaints = complaintService.getComplaintsByTenant(tenantId);
                complaintData.clear();
                complaintData.addAll(complaints);
                
                // Update filtered data
                filterComplaints();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load complaint data: " + e.getMessage());
        }
    }
    
    private void filterComplaints() {
        try {
            String filter = filterComboBox.getValue();
            String search = searchComplaints.getText().toLowerCase();
            
            filteredComplaintData.clear();
            
            for (ComplaintDTO complaint : complaintData) {
                boolean matchesFilter = "All".equals(filter) || 
                                       (filter != null && filter.equalsIgnoreCase(complaint.getStatus()));
                
                boolean matchesSearch = search.isEmpty() || 
                                       (complaint.getTitle() != null && complaint.getTitle().toLowerCase().contains(search)) ||
                                       (complaint.getDescription() != null && complaint.getDescription().toLowerCase().contains(search)) ||
                                       String.valueOf(complaint.getComplaintId()).contains(search);
                
                if (matchesFilter && matchesSearch) {
                    filteredComplaintData.add(complaint);
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