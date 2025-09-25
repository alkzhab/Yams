package controleur;

import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;
import java.io.IOException;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;

public class ControllerJeu {

    @FXML private Label turnLabel;
    @FXML private Label playerTurnLabel;
    @FXML private ImageView de1, de2, de3, de4, de5;
    @FXML private CheckBox cbDe1, cbDe2, cbDe3, cbDe4, cbDe5;
    @FXML private Button rollButton;
    @FXML private Button stopButton;
    @FXML private VBox combinaisonPane;
    @FXML private ListView<String> scoreListView;
    @FXML private ListView<String> aiScoreListView;
    @FXML private Label totalScoreLabel;
    @FXML private Label aiTotalScore;
    @FXML private ImageView fondImageView;

    private Board board;
    private ScoreSheet playerScoreSheet;
    private ScoreSheet aiScoreSheet;
    private AIPlayer aiPlayer;
    private int nbRolls;
    private int currentRound;
    private boolean isPlayerTurn;
    private boolean waitingForCombination = false;
    
    @FXML
    public void initialize() {
    	Image fondImage = new Image(getClass().getResource("/vue/Images/fond.png").toExternalForm());
        fondImageView.setImage(fondImage);
        board = new Board();
        playerScoreSheet = new ScoreSheet();
        aiScoreSheet = new ScoreSheet();
        aiPlayer = new AIPlayer();

        currentRound = 1;
        isPlayerTurn = true;
        nbRolls = 0;

        updateUI();
    }

    @FXML
    private void handleRoll() {
        if (waitingForCombination) return;

        if (nbRolls >= 2) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vous avez déjà effectué 3 lancers.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        List<Integer> toReroll = new ArrayList<>();
        if (cbDe1.isSelected()) toReroll.add(1);
        if (cbDe2.isSelected()) toReroll.add(2);
        if (cbDe3.isSelected()) toReroll.add(3);
        if (cbDe4.isSelected()) toReroll.add(4);
        if (cbDe5.isSelected()) toReroll.add(5);

        if (toReroll.isEmpty() && nbRolls > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez cocher au moins un dé à relancer.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        
        if (toReroll.contains(1)) animateDice(de1);
        if (toReroll.contains(2)) animateDice(de2);
        if (toReroll.contains(3)) animateDice(de3);
        if (toReroll.contains(4)) animateDice(de4);
        if (toReroll.contains(5)) animateDice(de5);

        board.reroll(toReroll.stream().mapToInt(i -> i).toArray());

        cbDe1.setSelected(false);
        cbDe2.setSelected(false);
        cbDe3.setSelected(false);
        cbDe4.setSelected(false);
        cbDe5.setSelected(false);

        nbRolls++;

        updateUI();

        if (nbRolls >= 2) {
            rollButton.setDisable(true); // plus de relance possible
        }
    }

    @FXML
    private void handleStop() {
        if (waitingForCombination) return;

        // On affiche le panneau de combinaisons et on cache les boutons
        combinaisonPane.setVisible(true);
        combinaisonPane.setManaged(true);

        rollButton.setDisable(true);
        stopButton.setDisable(true);

        waitingForCombination = true;
    }

    @FXML
    private void onComboSelected(javafx.event.ActionEvent event) {
        if (!waitingForCombination) return;

        Button btn = (Button) event.getSource();
        String choice = (String) btn.getUserData();

        Combination combination = switch (choice) {
            case "T" -> new ThreeOfAKind();
            case "F" -> new FullHouse();
            case "C" -> new ChanceCombination();
            case "4" -> new FourOfAKind();
            case "S" -> new SmallStraight();
            case "L" -> new LargeStraight();
            case "Y" -> new YamsCombination();
            default -> null;
        };

        if (combination == null) return;
        
        if (playerScoreSheet.isCombinationUsed(combination)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Combinaison déjà utilisée");
            alert.setHeaderText("Vous avez déjà utilisé cette combinaison.");
            alert.setContentText("Veuillez en choisir une autre.");
            alert.showAndWait();
            return;
        }

        // Si combo valide ou demander sacrifice
        if (combination.isValid(board)) {
            playerScoreSheet.recordScore(combination, board, false);
            finishTurn();
        } else {
            // Demander confirmation sacrifice
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Combinaison invalide");
            alert.setHeaderText("Cette combinaison n'est pas valide.");
            alert.setContentText("Voulez-vous sacrifier cette combinaison (0 points) ?");

            ButtonType oui = new ButtonType("Oui");
            ButtonType non = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(oui, non);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == oui) {
                playerScoreSheet.recordScore(combination, board, true);
                finishTurn();
            }
            // Sinon on reste sur le panneau combo pour choisir autre chose
        }
    }

    private void finishTurn() {
        // Switch entre joueur et IA
        if (isPlayerTurn) {
            isPlayerTurn = false;
            nbRolls = 0;

            board.reroll(new int[] {1, 2, 3, 4, 5});
            updateUI();

            runAITurn();

        } else {
            // Après tour IA, passe tour joueur
            isPlayerTurn = true;
            nbRolls = 0;

            currentRound++;

            if (currentRound > 7) {
                // Fin du jeu
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Fin du jeu");
                alert.setHeaderText("La partie est terminée !");
                alert.setContentText(
                    "Votre score final est : " + playerScoreSheet.scoreTotal() + "\n" +
                    "Score IA : " + aiScoreSheet.scoreTotal()
                );
                alert.showAndWait();

                rollButton.setDisable(true);
                stopButton.setDisable(true);
                combinaisonPane.setVisible(false);
                combinaisonPane.setManaged(false);
                return;
            }

            board.reroll(new int[] {1, 2, 3, 4, 5});

            combinaisonPane.setVisible(false);
            combinaisonPane.setManaged(false);

            rollButton.setDisable(false);
            stopButton.setDisable(false);

            waitingForCombination = false;

            updateUI();
        }
    }

    private void runAITurn() {
        new Thread(() -> {
            try {
                // Simuler 1 à 3 lancers pour IA
                while (nbRolls < 3) {
                    int[] toReroll = aiPlayer.chooseDiceToReroll(board);
                    board.reroll(toReroll);
                    nbRolls++;

                    Thread.sleep(1000); // pause pour simuler réflexion

                    javafx.application.Platform.runLater(this::updateUI);
                }

                // Choisir la combinaison
                Combination bestCombo = aiPlayer.chooseCombination(aiScoreSheet, board);
                boolean sacrifice = !bestCombo.isValid(board);

                // Enregistrer le score
                aiScoreSheet.recordScore(bestCombo, board, sacrifice);

                // Mettre à jour l'UI + passer au joueur
                javafx.application.Platform.runLater(() -> {
                    updateUI();  // mettre à jour le score IA visible
                    finishTurn();
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace(); // au cas où une exception arrive
            }
        }).start();
    }

    private void updateUI() {
        turnLabel.setText("Tour " + currentRound);
        playerTurnLabel.setText(isPlayerTurn ? "Au tour du joueur" : "Au tour de l'IA");

        List<Dice> dice = board.getDice();
        setDiceImage(de1, dice.get(0).value());
        setDiceImage(de2, dice.get(1).value());
        setDiceImage(de3, dice.get(2).value());
        setDiceImage(de4, dice.get(3).value());
        setDiceImage(de5, dice.get(4).value());

        // Reset checkbox état
        cbDe1.setSelected(false);
        cbDe2.setSelected(false);
        cbDe3.setSelected(false);
        cbDe4.setSelected(false);
        cbDe5.setSelected(false);

        // Activer/désactiver selon si on peut relancer
        cbDe1.setDisable(rollButton.isDisable());
        cbDe2.setDisable(rollButton.isDisable());
        cbDe3.setDisable(rollButton.isDisable());
        cbDe4.setDisable(rollButton.isDisable());
        cbDe5.setDisable(rollButton.isDisable());

     // Mise à jour score joueur
        scoreListView.getItems().clear();
        playerScoreSheet.getScores().forEach((comb, score) -> {
            scoreListView.getItems().add(comb.toString() + ": " + score);
        });
        totalScoreLabel.setText("Total: " + playerScoreSheet.scoreTotal());

        // Mise à jour score IA
        aiScoreListView.getItems().clear();
        aiScoreSheet.getScores().forEach((comb, score) -> {
            aiScoreListView.getItems().add(comb.toString() + ": " + score);
        });
        aiTotalScore.setText("Total: " + aiScoreSheet.scoreTotal());

    }

    private void setDiceImage(ImageView imageView, int value) {
        String path = "/vue/Images/de" + value + ".png";
        Image image = new Image(getClass().getResourceAsStream(path));
        imageView.setImage(image);
    }
    
    private void animateDice(ImageView diceImageView) {
        RotateTransition rotate = new RotateTransition(Duration.millis(500), diceImageView);
        rotate.setByAngle(360);
        rotate.setCycleCount(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(250), diceImageView);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.3);
        scale.setToY(1.3);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);

        ParallelTransition parallel = new ParallelTransition(rotate, scale);
        parallel.play();
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/interfaceAccueil.fxml"));
            Parent creationRoot = loader.load();
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
}
