package vue;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Chargement du fichier FXML
        URL fxmlLocation = getClass().getResource("interfaceAccueil.fxml");
            if (fxmlLocation == null) {
                System.err.println("FXML non trouvé !");
                return;
            }

            Parent root = FXMLLoader.load(fxmlLocation);

            // Création de la scène
            Scene scene = new Scene(root);

            // Chargement du CSS
            URL cssLocation = getClass().getResource("application.css");
            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            } else {
                System.err.println("CSS non trouvé !");
            }

            // Configuration de la fenêtre
            primaryStage.setTitle("Yam’s");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
