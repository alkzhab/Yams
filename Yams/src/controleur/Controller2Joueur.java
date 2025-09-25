package controleur;

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
import javafx.util.Duration;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import model.*;
import java.io.IOException;
import java.util.*;

public class Controller2Joueur {

    @FXML private ImageView de1, de2, de3, de4, de5;
    @FXML private CheckBox cbDe1, cbDe2, cbDe3, cbDe4, cbDe5;
    @FXML private Button rollButton, stopButton;
    @FXML private VBox combinaisonPane;
    @FXML private Label turnLabel, playerTurnLabel;
    @FXML private Label totalScoreJoueur1, totalScoreJoueur2;
    @FXML private ListView<String> scoreListView1, scoreListView2;
    @FXML private ImageView fondImageView;

    private Board board;
    private ScoreSheet player1ScoreSheet;
    private ScoreSheet player2ScoreSheet;
    private int currentRound = 1;
    private boolean isPlayer1Turn = true;
    private boolean waitingForCombination = false;
    private int nbRolls = 0;

    @FXML
    public void initialize() {
    	Image fondImage = new Image(getClass().getResource("/vue/Images/fond.png").toExternalForm());
        fondImageView.setImage(fondImage);
        board = new Board();
        player1ScoreSheet = new ScoreSheet();
        player2ScoreSheet = new ScoreSheet();

        updateUI();
    }

    @FXML
    public void handleRoll() {
        if (waitingForCombination) return;

        if (nbRolls >= 2) {
            Alert alert = new Alert(AlertType.WARNING, "Vous avez déjà effectué 3 lancers.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        int[] toReroll = getSelectedDice();
        if (toReroll.length == 0 && nbRolls > 0) {
            Alert alert = new Alert(AlertType.WARNING, "Veuillez cocher au moins un dé à relancer.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        //Animation des dés sélectionnés
        for (int dieIndex : toReroll) {
            switch (dieIndex) {
                case 1 -> animateDice(de1);
                case 2 -> animateDice(de2);
                case 3 -> animateDice(de3);
                case 4 -> animateDice(de4);
                case 5 -> animateDice(de5);
            }
        }

        board.reroll(toReroll);

        cbDe1.setSelected(false);
        cbDe2.setSelected(false);
        cbDe3.setSelected(false);
        cbDe4.setSelected(false);
        cbDe5.setSelected(false);

        nbRolls++;

        updateUI();

        if (nbRolls >= 2) {
            rollButton.setDisable(true);
            rollButton.setOpacity(0.5);  // devient un peu "grisé"
        }
    }



    @FXML
    private void handleStop() {
        waitingForCombination = true;
        combinaisonPane.setVisible(true);
        combinaisonPane.setManaged(true);

        rollButton.setDisable(true);
        stopButton.setDisable(true);
    }

    @FXML
    public void onComboSelected(ActionEvent event) {
        if (!waitingForCombination) return;

        Button btn = (Button) event.getSource();
        String code = (String) btn.getUserData();

        Combination combination = getCombinationFromCode(code);
        if (combination == null) return;

        ScoreSheet currentSheet = isPlayer1Turn ? player1ScoreSheet : player2ScoreSheet;

        if (currentSheet.isCombinationUsed(combination)) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Combinaison déjà utilisée");
            alert.setHeaderText("Vous avez déjà utilisé cette combinaison.");
            alert.setContentText("Veuillez en choisir une autre.");
            alert.showAndWait();
            return;
        }

        boolean sacrifice = !combination.isValid(board);

        if (sacrifice) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Combinaison invalide");
            alert.setHeaderText("Cette combinaison n'est pas valide.");
            alert.setContentText("Voulez-vous sacrifier cette combinaison (0 points) ?");

            ButtonType oui = new ButtonType("Oui");
            ButtonType non = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(oui, non);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == oui) {
                currentSheet.recordScore(combination, board, true);
                addScoreToList(combination, 0);
                finishTurn();
            }
            // sinon on reste sur la sélection
        } else {
            currentSheet.recordScore(combination, board, false);
            addScoreToList(combination, combination.score(board));
            finishTurn();
        }
    }


    private void finishTurn() {
        // Passer au joueur suivant
        isPlayer1Turn = !isPlayer1Turn;

        if (isPlayer1Turn) {
            currentRound++;
            if (currentRound > 7) {
                endGame();
                return;
            }
        }

        nbRolls = 0;
        waitingForCombination = false;

        board.reroll(new int[] {1, 2, 3, 4, 5});

        combinaisonPane.setVisible(false);
        combinaisonPane.setManaged(false);

        rollButton.setDisable(false);
        rollButton.setOpacity(1.0);  // remettre normal

        stopButton.setDisable(false);

        updateUI();
    }


    private void endGame() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Fin du jeu");
        alert.setHeaderText("La partie est terminée !");
        alert.setContentText(
                "Score Joueur 1 : " + player1ScoreSheet.scoreTotal() + "\n" +
                "Score Joueur 2 : " + player2ScoreSheet.scoreTotal() + "\n" +
                (player1ScoreSheet.scoreTotal() > player2ScoreSheet.scoreTotal() ? "Joueur 1 gagne !" :
                 player1ScoreSheet.scoreTotal() < player2ScoreSheet.scoreTotal() ? "Joueur 2 gagne !" : "Égalité !")
        );
        alert.showAndWait();

        rollButton.setDisable(true);
        stopButton.setDisable(true);
        combinaisonPane.setVisible(false);
        combinaisonPane.setManaged(false);
    }

    private Combination getCombinationFromCode(String code) {
        return switch (code) {
            case "T" -> new ThreeOfAKind();
            case "4" -> new FourOfAKind();
            case "F" -> new FullHouse();
            case "S" -> new SmallStraight();
            case "L" -> new LargeStraight();
            case "C" -> new ChanceCombination();
            case "Y" -> new YamsCombination();
            default -> throw new IllegalArgumentException("Code combinaison inconnu: " + code);
        };
    }

    private void addScoreToList(Combination combo, int score) {
        String entry = combo + " : " + score;

        if (isPlayer1Turn) {
            scoreListView1.getItems().add(entry);
            totalScoreJoueur1.setText(String.valueOf(player1ScoreSheet.scoreTotal()));
        } else {
            scoreListView2.getItems().add(entry);
            totalScoreJoueur2.setText(String.valueOf(player2ScoreSheet.scoreTotal()));
        }
    }

    private void updateUI() {
        List<Dice> dice = board.getDice();
        updateDieImage(de1, dice.get(0).value());
        updateDieImage(de2, dice.get(1).value());
        updateDieImage(de3, dice.get(2).value());
        updateDieImage(de4, dice.get(3).value());
        updateDieImage(de5, dice.get(4).value());

        cbDe1.setSelected(false);
        cbDe2.setSelected(false);
        cbDe3.setSelected(false);
        cbDe4.setSelected(false);
        cbDe5.setSelected(false);

        cbDe1.setDisable(rollButton.isDisable());
        cbDe2.setDisable(rollButton.isDisable());
        cbDe3.setDisable(rollButton.isDisable());
        cbDe4.setDisable(rollButton.isDisable());
        cbDe5.setDisable(rollButton.isDisable());

        turnLabel.setText("Tour " + currentRound);
        playerTurnLabel.setText(isPlayer1Turn ? "Au tour du Joueur 1" : "Au tour du Joueur 2");
    }


    private void updateDieImage(ImageView imageView, int value) {
        String imagePath = "/vue/Images/de" + value + ".png";
        imageView.setImage(new Image(getClass().getResourceAsStream(imagePath)));
    }

    private int[] getSelectedDice() {
        List<Integer> selected = new ArrayList<>();
        if (cbDe1.isSelected()) selected.add(1);
        if (cbDe2.isSelected()) selected.add(2);
        if (cbDe3.isSelected()) selected.add(3);
        if (cbDe4.isSelected()) selected.add(4);
        if (cbDe5.isSelected()) selected.add(5);

        return selected.stream().mapToInt(Integer::intValue).toArray();
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
}
