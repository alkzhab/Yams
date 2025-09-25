package controleur;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ControllerJouer {
	@FXML
    private void handleRetour(ActionEvent event) {
        try {
            Parent creationRoot = FXMLLoader.load(getClass().getResource("/vue/interfaceAccueil.fxml"));
            Scene creationScene = new Scene(creationRoot);
            creationScene.getStylesheets().add(getClass().getResource("/vue/application.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(creationScene);
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	@FXML
    private void handleSolo(ActionEvent event) {
        try {
            Parent creationRoot = FXMLLoader.load(getClass().getResource("/vue/interfaceJeu.fxml"));
            Scene creationScene = new Scene(creationRoot);
            creationScene.getStylesheets().add(getClass().getResource("/vue/application.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(creationScene);
            stage.setTitle("SoloYams");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	@FXML
    private void handleDuo(ActionEvent event) {
        try {
            Parent creationRoot = FXMLLoader.load(getClass().getResource("/vue/interface2Joueur.fxml"));
            Scene creationScene = new Scene(creationRoot);
            creationScene.getStylesheets().add(getClass().getResource("/vue/application.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(creationScene);
            stage.setTitle("DuoYams");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	@FXML
    private void handleBonus(ActionEvent event) {
        try {
            Parent creationRoot = FXMLLoader.load(getClass().getResource("/vue/interfaceCarte.fxml"));
            Scene creationScene = new Scene(creationRoot);
            creationScene.getStylesheets().add(getClass().getResource("/vue/application.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(creationScene);
            stage.setTitle("Bonus Yams");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
