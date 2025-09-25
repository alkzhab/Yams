package controleur;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.io.File;


public class ControllerAccueil {
	@FXML
    private void handleCredits(ActionEvent event) {
        try {
            Parent creationRoot = FXMLLoader.load(getClass().getResource("/vue/interfaceCredits.fxml"));
            Scene creationScene = new Scene(creationRoot);
            creationScene.getStylesheets().add(getClass().getResource("/vue/application.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(creationScene);
            stage.setTitle("Credits");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	@FXML
    private void handleJouer(ActionEvent event) {
        try {
            Parent creationRoot = FXMLLoader.load(getClass().getResource("/vue/interfaceJouer.fxml"));
            Scene creationScene = new Scene(creationRoot);
            creationScene.getStylesheets().add(getClass().getResource("/vue/application.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(creationScene);
            stage.setTitle("Jouer");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@FXML
	private void handleRegle() {
	    try {
	        String pdfPath = "regleYams.pdf";

	        // Vérifie si le fichier existe
	        File pdfFile = new File(pdfPath);
	        if (!pdfFile.exists()) {
	            System.out.println("Le fichier PDF n'existe pas.");
	            return;
	        }

	        // Si Desktop est supporté
	        if (Desktop.isDesktopSupported()) {
	            Desktop.getDesktop().open(pdfFile);
	        } else {
	            // Sinon, on tente avec la ligne de commande (Linux/Mac/Windows)
	            String osName = System.getProperty("os.name").toLowerCase();

	            ProcessBuilder pb;
	            if (osName.contains("win")) {
	                pb = new ProcessBuilder("cmd", "/c", "start", "\"\"", pdfFile.getAbsolutePath());
	            } else if (osName.contains("mac")) {
	                pb = new ProcessBuilder("open", pdfFile.getAbsolutePath());
	            } else {
	                pb = new ProcessBuilder("xdg-open", pdfFile.getAbsolutePath());
	            }
	            pb.start();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}


	

}
