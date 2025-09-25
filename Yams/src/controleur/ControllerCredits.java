package controleur;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ControllerCredits {
	@FXML
    private void handleRetour(ActionEvent event) {
        try {
            Parent creationRoot = FXMLLoader.load(getClass().getResource("/vue/interfaceAccueil.fxml"));
            Scene creationScene = new Scene(creationRoot);
            creationScene.getStylesheets().add(getClass().getResource("/vue/application.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(creationScene);
            stage.setTitle("Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
