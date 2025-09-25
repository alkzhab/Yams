package controleur;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Card;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.io.IOException;
import java.util.*;

public class ControllerSelectionCarte {

    @FXML private Label playerTurnLabel;
    @FXML private ImageView card1, card2, card3, card4, card5, card6;
    @FXML private ImageView fondImageView;

    private List<Card> allCards = new ArrayList<>();
    private Map<ImageView, Card> cardMap = new HashMap<>();
    private List<ImageView> cardImageViews;
    private List<Card> player1SelectedCards = new ArrayList<>();
    private List<Card> player2SelectedCards = new ArrayList<>();
    private boolean isChoosingCards = true;
    private boolean isPlayer1Choosing = true;
    private Image cardBackImage;

    @FXML
    public void initialize() {
        cardBackImage = new Image(getClass().getResourceAsStream("/vue/Images/dos_carte.png"));
        Image fondImage = new Image(getClass().getResource("/vue/Images/fond.png").toExternalForm());
        fondImageView.setImage(fondImage);

        // Créer les objets Card
        allCards.add(new Card("joker.png", "Joker"));
        allCards.add(new Card("relance.png", "Relance"));
        allCards.add(new Card("garde.png", "Garde"));
        allCards.add(new Card("plus.png", "+1"));
        allCards.add(new Card("xdouble.png", "Double"));
        allCards.add(new Card("choix.png", "Choix"));

        // Liste des ImageView
        cardImageViews = List.of(card1, card2, card3, card4, card5, card6);

        // Initialiser premier affichage
        setupCardSelection();

        playerTurnLabel.setText("Joueur 1 : Choisissez 2 cartes");
    }

    private void setupCardSelection() {
        // Mélanger les cartes
        Collections.shuffle(allCards);

        // Associer les cartes aux ImageView
        cardMap.clear();
        for (int i = 0; i < allCards.size(); i++) {
            Card card = allCards.get(i);
            ImageView iv = cardImageViews.get(i);
            iv.setImage(cardBackImage);
            cardMap.put(iv, card);

            // Remettre le listener
            iv.setOnMouseClicked(e -> onCardClicked(iv));
        }
    }

    private void onCardClicked(ImageView imageView) {
        if (!isChoosingCards) return;

        Card card = cardMap.get(imageView);
        List<Card> currentPlayerSelection = isPlayer1Choosing ? player1SelectedCards : player2SelectedCards;

        if (currentPlayerSelection.contains(card)) return;

        currentPlayerSelection.add(card);

        imageView.setImage(new Image(getClass().getResourceAsStream("/vue/Images/" + card.getName())));

        if (currentPlayerSelection.size() == 2) {
            isChoosingCards = false;  // bloquer clics pendant le délai

            PauseTransition pause = new PauseTransition(Duration.seconds(1));

            if (isPlayer1Choosing) {
                pause.setOnFinished(e -> {
                    isPlayer1Choosing = false;
                    playerTurnLabel.setText("Joueur 2 : Choisissez 2 cartes");
                    setupCardSelection(); // mélange + retourne les cartes
                    isChoosingCards = true;  // réactiver clics
                });
            } else {
                pause.setOnFinished(e -> {
                    startGame();
                });
            }

            pause.play();
        }
    }


    private void startGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/interfaceBonus.fxml"));
            Parent root = loader.load();

            ControllerBonus gameController = loader.getController();
            gameController.setPlayerSelectedCards(player1SelectedCards, player2SelectedCards);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/vue/application.css").toExternalForm());

            Stage stage = (Stage) playerTurnLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Jeu de dés");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
