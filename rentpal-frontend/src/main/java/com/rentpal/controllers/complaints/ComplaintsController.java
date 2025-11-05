package com.rentpal.controllers.complaints;

import com.rentpal.dto.ComplaintDTO;
import com.rentpal.service.ComplaintService;
import com.rentpal.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.StringConverter;
import java.util.List;
import javafx.scene.control.TableCell;



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
    private static final ObservableList<String> STATUS_OPTIONS =
        FXCollections.observableArrayList("Pending", "In Progress", "Resolved", "Rejected");
    private ComplaintService complaintService = new ComplaintService();

    @FXML
    public void initialize() {
        // filters
        filterComboBox.setItems(FXCollections.observableArrayList("All", "Pending", "In Progress", "Resolved", "Rejected"));
        filterComboBox.getSelectionModel().select("All");

        // map columns
        colId.setCellValueFactory(cell -> cell.getValue().idProperty());
        colTenant.setCellValueFactory(cell -> cell.getValue().tenantProperty());
        colIssue.setCellValueFactory(cell -> cell.getValue().issueProperty());
        colStatus.setCellValueFactory(cell -> cell.getValue().statusProperty());
        colDate.setCellValueFactory(cell -> cell.getValue().dateProperty());

        // Make table editable only for owners
        boolean isOwner = SessionManager.getInstance().isOwner() && SessionManager.getInstance().getCurrentOwner() != null;
        complaintsTable.setEditable(isOwner);

        if (isOwner) {
            // Show a ComboBox editor in the Status column for owners
            colStatus.setCellFactory(column -> {
                ComboBoxTableCell<Complaint, String> cell = new ComboBoxTableCell<>(STATUS_OPTIONS);
                // Optional: pretty converter (identity here, but future-proof)
                cell.setConverter(new StringConverter<>() {
                    @Override public String toString(String s) { return s; }
                    @Override public String fromString(String s) { return s; }
                });
                return cell;
            });

            colStatus.setOnEditCommit(evt -> {
                Complaint row = evt.getRowValue();
                String oldVal = evt.getOldValue();
                String newVal = evt.getNewValue();

                if (newVal == null || newVal.isBlank() || newVal.equals(oldVal)) {
                    // nothing to do
                    complaintsTable.refresh();
                    return;
                }

                try {
                    // backend update
                    complaintService.updateComplaintStatus((long) row.getId(), newVal);
                    // reflect success in UI model
                    row.setStatus(newVal);
                    complaintsTable.refresh();
                } catch (Exception e) {
                    // revert on error
                    row.setStatus(oldVal);
                    complaintsTable.refresh();
                    showAlert(Alert.AlertType.ERROR, "Update Failed",
                            "Could not update status: " + e.getMessage());
                }
            });
        } else {
            // Tenants: ensure plain, non-editable cells
            colStatus.setCellFactory(column -> new TableCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            });
        }

        // load + auto refresh
        loadComplaintsFromBackend();
        addRealTimeUpdateListeners();

        // (Optional) filter behavior
        filterComboBox.valueProperty().addListener((obs, ov, nv) -> applyFilter(nv));
        searchField.textProperty().addListener((obs, ov, nv) -> applyFilter(filterComboBox.getValue()));
    }

    private void applyFilter(String statusFilter) {
        if (complaintsData == null) return;
        complaintsTable.setItems(complaintsData.filtered(c -> {
            boolean matchesStatus = "All".equalsIgnoreCase(statusFilter) || c.getStatus().equalsIgnoreCase(statusFilter);
            String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
            boolean matchesSearch = q.isEmpty()
                    || String.valueOf(c.getId()).contains(q)
                    || (c.getIssue() != null && c.getIssue().toLowerCase().contains(q))
                    || (c.getTenant() != null && c.getTenant().toLowerCase().contains(q));
            return matchesStatus && matchesSearch;
        }));
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