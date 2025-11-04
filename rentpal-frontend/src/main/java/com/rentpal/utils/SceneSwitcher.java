package com.rentpal.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {
    public static void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        try{
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(SceneSwitcher.class.getResource(fxmlPath));
            stage.setScene(new Scene(root));
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void switchToWelcome(ActionEvent event) throws IOException {
        try{
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(SceneSwitcher.class.getResource("/com/rentpal/fxml/welcome.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("RentPal - Welcome");
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
