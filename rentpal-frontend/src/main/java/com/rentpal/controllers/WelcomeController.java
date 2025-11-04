package com.rentpal.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.List;

public class WelcomeController {

    @FXML private ScrollPane scrollPane;
    @FXML private StackPane heroSection;
    @FXML private StackPane featurePayments;
    @FXML private StackPane featureTenants;
    @FXML private StackPane featureAnalytics;
    @FXML private StackPane featureComplaints;
    @FXML private StackPane ctaSection;

    private boolean heroAnimated = false;
    private boolean paymentsAnimated = false;
    private boolean tenantsAnimated = false;
    private boolean analyticsAnimated = false;
    private boolean complaintsAnimated = false;
    private boolean ctaAnimated = false;

    @FXML
    public void initialize() {
        // Simplified initialization for testing
        System.out.println("WelcomeController initialized");
        // Add scroll listener for section animations
        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            checkAndAnimateSections();
        });
    }

    private void animateHeroSection() {
        // Simplified animation for testing
        System.out.println("animateHeroSection called");
        if (heroAnimated) return;
        heroAnimated = true;
        // Just make elements visible without animation
        VBox heroContent = findVBoxInSection(heroSection);
        if (heroContent != null) {
            for (Node child : heroContent.getChildren()) {
                child.setOpacity(1);
            }
        }
    }

    private void checkAndAnimateSections() {
        if (!paymentsAnimated && isSectionVisible(featurePayments)) {
            animateFeatureSection(featurePayments, false);
            paymentsAnimated = true;
        }

        if (!tenantsAnimated && isSectionVisible(featureTenants)) {
            animateFeatureSection(featureTenants, true);
            tenantsAnimated = true;
        }

        if (!analyticsAnimated && isSectionVisible(featureAnalytics)) {
            animateFeatureSection(featureAnalytics, false);
            analyticsAnimated = true;
        }

        if (!complaintsAnimated && isSectionVisible(featureComplaints)) {
            animateFeatureSection(featureComplaints, true);
            complaintsAnimated = true;
        }

        if (!ctaAnimated && isSectionVisible(ctaSection)) {
            System.out.println("CTA Animation Triggered!");
            animateCTASection();
            ctaAnimated = true;
        }
    }

    private boolean isSectionVisible(Node section) {
        if (section == null) return false;

        try {
            double scrollPos = scrollPane.getVvalue();
            double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
            double viewportHeight = scrollPane.getViewportBounds().getHeight();

            double sectionY = section.getBoundsInParent().getMinY();
            double currentScrollY = scrollPos * (contentHeight - viewportHeight);

            // Section is visible if it's within 80% of viewport (easier to trigger)
            return sectionY < currentScrollY + (viewportHeight * 0.8);
        } catch (Exception e) {
            return false;
        }
    }

    private void animateFeatureSection(StackPane section, boolean isReverse) {
        // Simplified animation for testing
        System.out.println("animateFeatureSection called");
        HBox contentBox = findHBoxInSection(section);
        if (contentBox == null) return;

        List<Node> children = contentBox.getChildren();
        if (children.size() < 2) return;

        // Just make elements visible without animation
        for (Node child : children) {
            child.setOpacity(1);
        }
    }

    private void animateNodeFromSide(Node node, double fromX, long delayMs) {
        FadeTransition fade = new FadeTransition(Duration.millis(1000), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(delayMs));

        TranslateTransition move = new TranslateTransition(Duration.millis(1000), node);
        move.setFromX(fromX);
        move.setToX(0);
        move.setDelay(Duration.millis(delayMs));

        fade.play();
        move.play();
    }

    private void animateCTASection() {
        VBox ctaContent = findVBoxInSection(ctaSection);
        if (ctaContent == null) {
            System.out.println("CTA VBox is NULL!");
            return;
        }

        System.out.println("Animating CTA VBox container");

        // Animate the entire VBox container
        FadeTransition containerFade = new FadeTransition(Duration.millis(1200), ctaContent);
        containerFade.setFromValue(0);
        containerFade.setToValue(1);

        TranslateTransition containerMove = new TranslateTransition(Duration.millis(1200), ctaContent);
        containerMove.setFromY(60);
        containerMove.setToY(0);

        containerFade.play();
        containerMove.play();
    }

    // Helper methods to find containers
    private VBox findVBoxInSection(StackPane section) {
        if (section == null) return null;
        for (Node child : section.getChildren()) {
            if (child instanceof VBox) {
                return (VBox) child;
            }
        }
        return null;
    }

    private HBox findHBoxInSection(StackPane section) {
        if (section == null) return null;
        for (Node child : section.getChildren()) {
            if (child instanceof HBox) {
                return (HBox) child;
            }
        }
        return null;
    }

    @FXML
    private void handleGetStarted() {
        scrollToSection(featurePayments);
    }

    @FXML
    private void scrollToFeatures() {
        scrollToSection(featurePayments);
    }

    private void scrollToSection(Node section) {
        if (section == null) return;

        try {
            double sectionY = section.getBoundsInParent().getMinY();
            double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
            double viewportHeight = scrollPane.getViewportBounds().getHeight();

            double targetVvalue = sectionY / (contentHeight - viewportHeight);

            Timeline scrollTimeline = new Timeline(
                    new KeyFrame(Duration.millis(1000),
                            new KeyValue(scrollPane.vvalueProperty(), targetVvalue, Interpolator.EASE_BOTH))
            );
            scrollTimeline.play();
        } catch (Exception e) {
            System.err.println("Error scrolling to section: " + e.getMessage());
        }
    }

    @FXML
    private void handleSignup() {
        navigateToPage("/com/rentpal/fxml/signup.fxml", "Sign Up - RentPal");
    }

    @FXML
    private void handleLogin() {
        navigateToPage("/com/rentpal/fxml/login.fxml", "Login - RentPal");
    }

    @FXML
    private void handleReportIssue() {
        // You can navigate to a report issue page or show a dialog
        System.out.println("Navigate to report issue");
    }

    private void navigateToPage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) scrollPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading page: " + fxmlPath);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error navigating to page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}