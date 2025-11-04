package com.rentpal.controllers.dashboard;

import com.rentpal.dto.OwnerDTO;
import com.rentpal.service.OwnerService;
import com.rentpal.utils.SessionManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class OwnerProfileController {

    @FXML private AnchorPane rootPane;
    @FXML private Circle profilePic;
    @FXML private Button changePhotoBtn;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField propertyCountField;
    @FXML private TextField joinDateField;
    @FXML private Button saveBtn;

    private boolean editMode = false;
    private OwnerService ownerService = new OwnerService();

    @FXML
    public void initialize() {
        loadOwnerProfileData();
        setEditable(false);
        playFadeIn(rootPane);
    }

    private void loadOwnerProfileData() {
        try {
            // Get current owner from session
            OwnerDTO currentOwner = SessionManager.getInstance().getCurrentOwner();
            if (currentOwner != null) {
                // Load owner data from backend
                OwnerDTO owner = ownerService.getOwnerById(currentOwner.getOwnerId());
                if (owner != null) {
                    nameField.setText(owner.getName() != null ? owner.getName() : "");
                    emailField.setText(owner.getEmail() != null ? owner.getEmail() : "");
                    phoneField.setText(owner.getPhone() != null ? owner.getPhone() : "");
                    
                    // For property count and join date, we'll use placeholder values for now
                    // TODO: Implement proper property count and join date
                    propertyCountField.setText("0"); // Placeholder
                    joinDateField.setText("2024-01-01"); // Placeholder
                }
            } else {
                // Fallback to placeholder data
                nameField.setText("");
                emailField.setText("");
                phoneField.setText("");
                propertyCountField.setText("0");
                joinDateField.setText("2024-01-01");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to placeholder data in case of error
            nameField.setText("");
            emailField.setText("");
            phoneField.setText("");
            propertyCountField.setText("0");
            joinDateField.setText("2024-01-01");
        }
    }

    private void setEditable(boolean val) {
        nameField.setEditable(val);
        emailField.setEditable(val);
        phoneField.setEditable(val);
        changePhotoBtn.setVisible(val);
        editMode = val;
    }

    @FXML
    private void handleChangePhoto(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File f = chooser.showOpenDialog(stage);
        if (f != null) {
            Image img = new Image(f.toURI().toString());
            profilePic.setFill(new ImagePattern(img));
            // TODO: Upload image to backend (if you want persistence)
        }
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        setEditable(true);
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            // Get current owner from session
            OwnerDTO currentOwner = SessionManager.getInstance().getCurrentOwner();
            if (currentOwner != null) {
                // Update owner data
                currentOwner.setName(nameField.getText());
                currentOwner.setEmail(emailField.getText());
                currentOwner.setPhone(phoneField.getText());
                
                // Save to backend
                OwnerDTO updatedOwner = ownerService.updateOwner(currentOwner.getOwnerId(), currentOwner);
                
                // Update session
                SessionManager.getInstance().setCurrentOwner(updatedOwner);
                
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile: " + e.getMessage());
        }

        setEditable(false);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleViewProperties(ActionEvent event) {
        // optional: load owner_properties.fxml into dashboard contentArea
        // This controller doesn't have direct reference to contentArea; dashboard controller should load child views
    }

    @FXML
    private void handleViewComplaints(ActionEvent event) {
        // optional: load owner_complaints.fxml via dashboard controller
    }

    /**
     * Utility: Plays a fade-in animation on the provided node.
     * Controller-based animation ensures it runs when loaded dynamically.
     */
    private void playFadeIn(Node node) {
        if (node == null) return;
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(300), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    /**
     * Optional external loader method: if dashboard wants to populate this view with real owner data,
     * call this method after loading the FXML:
     *
     * FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rentpal/fxml/owner_profile.fxml"));
     * Parent p = loader.load();
     * OwnerProfileController ctrl = loader.getController();
     * ctrl.loadOwner(ownerModel);
     */
    public void loadOwnerData(String fullName, String email, String phone, int propertiesOwned, String joinDate) {
        nameField.setText(fullName);
        emailField.setText(email);
        phoneField.setText(phone);
        propertyCountField.setText(String.valueOf(propertiesOwned));
        joinDateField.setText(joinDate);
        setEditable(false);
        playFadeIn(rootPane);
    }
}
