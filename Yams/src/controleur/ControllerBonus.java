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

public class ControllerBonus {

    @FXML private ImageView de1, de2, de3, de4, de5, de6;
    @FXML private CheckBox cbDe1, cbDe2, cbDe3, cbDe4, cbDe5, cbDe6;
    @FXML private Button rollButton, stopButton;
    @FXML private VBox combinaisonPane;
    @FXML private Label turnLabel, playerTurnLabel;
    @FXML private Label totalScoreJoueur1, totalScoreJoueur2;
    @FXML private ListView<String> scoreListView1, scoreListView2;
    @FXML private ImageView fondImageView;
    @FXML private ImageView chosenCard1, chosenCard2;
    @FXML private ImageView pCard1, pCard2;
    @FXML private Label CardExplanation;

    private List<Card> player1SelectedCards;
    private List<Card> player2SelectedCards;
    private boolean[] player1CardUsed = new boolean[2];
    private boolean[] player2CardUsed = new boolean[2];
    private boolean isJokerActive = false;
    private boolean isDoubleActive = false;
    private boolean isPlusOneActive = false;
    private boolean isRelanceActive = false;
    private boolean isGardeActive = false;
    private boolean[] player1BlockedDice = new boolean[5];
    private boolean[] player2BlockedDice = new boolean[5];
    private Board board;
    private ScoreSheet player1ScoreSheet;
    private ScoreSheet player2ScoreSheet;
    private int maxRollsPerTurn = 2; 
    private int currentRound = 1;
    private boolean isPlayer1Turn = true;
    private boolean waitingForCombination = false;
    private int nbRolls = 0;
    
    private void useJoker() {
        isJokerActive = true;
        CardExplanation.setVisible(true);
        CardExplanation.setManaged(true);
        CardExplanation.setText("Joker activé : vous pouvez choisir n'importe quelle combinaison (score moitié)");
    }
    
    private void useDouble() {
        isDoubleActive = true;
        CardExplanation.setVisible(true);
        CardExplanation.setManaged(true);
        CardExplanation.setText("Double activé : votre prochain score sera doublé !");
    }
    
    private void useGarde() {
    	isGardeActive = true;
        if (isPlayer1Turn) {
            blockOpponentDice(player2BlockedDice);
        } else {
            blockOpponentDice(player1BlockedDice);
        }
        CardExplanation.setVisible(true);
        CardExplanation.setManaged(true);
        CardExplanation.setText("Effet Garde activé : 2 dés adverses seront bloqués au prochain tour");
    }
    
    private void usePlusOne() {
        isPlusOneActive = true;

        board.addSixthDie();

        de6.setVisible(true);
        cbDe6.setVisible(true);
        cbDe6.setDisable(false);
        
        updateUI();
        
        CardExplanation.setVisible(true);
        CardExplanation.setManaged(true);
        CardExplanation.setText("Effet Plus activé : Un dé suplémentaire ajouté");
    }
    
    public void useRelance() {
        maxRollsPerTurn++;
        isRelanceActive = true;
        CardExplanation.setVisible(true);
        CardExplanation.setManaged(true);
        CardExplanation.setText("Effet Relance activé : Une disposez d'une relance suplémentaire ");
 
        updateUI();
    }
    
    private void useChoix() {
    	CardExplanation.setVisible(true);
        CardExplanation.setManaged(true);
        CardExplanation.setText("Effet Choix activé : Vous pouvez choisir la valeur du dé");

        List<Dice> dice = board.getDice();
        int nbDes = dice.size();

        // Demander quel dé modifier
        TextInputDialog dialog1 = new TextInputDialog();
        dialog1.setTitle("Choisir le dé à modifier");
        dialog1.setHeaderText(null);
        dialog1.setContentText("Quel dé voulez-vous modifier ? (1-" + nbDes + ")");

        Optional<String> result1 = dialog1.showAndWait();
        if (result1.isEmpty()) return;

        int deIndex;
        try {
            deIndex = Integer.parseInt(result1.get()) - 1;  // index 0-based
            if (deIndex < 0 || deIndex >= nbDes) {
                showError("Numéro de dé invalide.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Entrée non valide.");
            return;
        }

        // Demander la nouvelle valeur
        TextInputDialog dialog2 = new TextInputDialog();
        dialog2.setTitle("Choisir la valeur du dé");
        dialog2.setHeaderText(null);
        dialog2.setContentText("Quelle valeur voulez-vous mettre ? (1-6)");

        Optional<String> result2 = dialog2.showAndWait();
        if (result2.isEmpty()) return;

        int newValue;
        try {
            newValue = Integer.parseInt(result2.get());
            if (newValue < 1 || newValue > 6) {
                showError("Valeur de dé invalide.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Entrée non valide.");
            return;
        }

        Dice d = dice.get(deIndex);
        d.forceValue(newValue);
        
        CardExplanation.setVisible(false);
        CardExplanation.setManaged(false);
        
        // Mettre à jour l'interface
        updateUI();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
    	Image fondImage = new Image(getClass().getResource("/vue/Images/fond.png").toExternalForm());
        fondImageView.setImage(fondImage);
        board = new Board();
        player1ScoreSheet = new ScoreSheet();
        player2ScoreSheet = new ScoreSheet();

        updateUI();
        setupCardClickHandlers();
    }
 
    private void setupCardClickHandlers() {
        pCard1.setOnMouseClicked(e -> onPlayerCardClicked(0));
        pCard2.setOnMouseClicked(e -> onPlayerCardClicked(1));
    }

    private void onPlayerCardClicked(int cardIndex) {
        boolean[] currentPlayerCardUsed = isPlayer1Turn ? player1CardUsed : player2CardUsed;
        List<Card> currentPlayerCards = isPlayer1Turn ? player1SelectedCards : player2SelectedCards;

        if (currentPlayerCards == null || currentPlayerCards.size() < 2) return;

        if (currentPlayerCardUsed[cardIndex]) {
            return;
        }

        // NE PAS MARQUER ICI !

        // Appelle activateCard pour appliquer effet et marquer la carte utilisée
        activateCard(cardIndex);
    }

    
    private void activateCard(int cardIndex) {
        List<Card> currentCards = isPlayer1Turn ? player1SelectedCards : player2SelectedCards;
        boolean[] currentCardUsed = isPlayer1Turn ? player1CardUsed : player2CardUsed;

        if (cardIndex < 0 || cardIndex >= currentCards.size()) return;
        if (currentCardUsed[cardIndex]) return; // déjà utilisée

        Card card = currentCards.get(cardIndex);

        switch (card.getName()) {
            case "joker.png":
                useJoker();
                break;
            case "xdouble.png":
                useDouble();
                break;
            case "garde.png":
                useGarde();
                break;
            case "plus.png":
            	usePlusOne();
            	break;
            case "relance.png":
            	useRelance();
            	break;
            case "choix.png":
            	useChoix();
            	break;
            
        }

        currentCardUsed[cardIndex] = true;  // Marquer utilisée APRÈS l'effet
        updatePlayerCardsDisplay();
    }
    
    public void setPlayerSelectedCards(List<Card> player1Cards, List<Card> player2Cards) {
        // Par exemple on affiche les cartes choisies en haut, 2 cartes par joueur
        if (player1Cards.size() >= 2 && player2Cards.size() >= 2) {
            // Affiche uniquement les cartes du joueur courant en haut (exemple joueur 1)
            // Ou affiche les 4 cartes si tu veux (ajuste selon le design)

            // Exemple : on stocke les cartes dans des listes privées si besoin
            this.player1SelectedCards = player1Cards;
            this.player2SelectedCards = player2Cards;

            // Mets à jour les ImageViews correspondants pour affichage
            updatePlayerCardsDisplay();
        }
    }
    
    private void updatePlayerCardsDisplay() {
        // Afficher les cartes du joueur courant
        List<Card> currentCards = isPlayer1Turn ? player1SelectedCards : player2SelectedCards;
        boolean[] currentCardUsed = isPlayer1Turn ? player1CardUsed : player2CardUsed;

        if (currentCards != null && currentCards.size() >= 2) {
            pCard1.setImage(new Image(getClass().getResourceAsStream("/vue/Images/" + currentCards.get(0).getName())));
            pCard2.setImage(new Image(getClass().getResourceAsStream("/vue/Images/" + currentCards.get(1).getName())));

            // Mettre l'opacité selon si la carte a été utilisée pour CE joueur
            pCard1.setOpacity(currentCardUsed[0] ? 0.4 : 1.0);
            pCard2.setOpacity(currentCardUsed[1] ? 0.4 : 1.0);
        } else {
            // Sécurité : aucune carte → rien afficher
            pCard1.setImage(null);
            pCard2.setImage(null);
            pCard1.setOpacity(1.0);
            pCard2.setOpacity(1.0);
        }
    }
    
    private void blockOpponentDice(boolean[] blockedDice) {
        // Réinitialise avant de bloquer (au cas où)
        Arrays.fill(blockedDice, false);
        
        Random rand = new Random();
        int blockedCount = 0;
        while (blockedCount < 2) {
            int index = rand.nextInt(5);  // entre 0 et 4
            if (!blockedDice[index]) {
                blockedDice[index] = true;
                blockedCount++;
            }
        }
    }
    
    private void updateBlockedDiceUI() {
        boolean[] blockedDice = isPlayer1Turn ? player1BlockedDice : player2BlockedDice;

        cbDe1.setDisable(blockedDice[0]);
        cbDe2.setDisable(blockedDice[1]);
        cbDe3.setDisable(blockedDice[2]);
        cbDe4.setDisable(blockedDice[3]);
        cbDe5.setDisable(blockedDice[4]);

    }
    
    private void clearBlockedDiceForCurrentPlayer() {
        if (isPlayer1Turn) {
            Arrays.fill(player1BlockedDice, false);
        } else {
            Arrays.fill(player2BlockedDice, false);
        }
    }

    @FXML
    private void handleRoll() {
        if (waitingForCombination) return;

        if (nbRolls >= maxRollsPerTurn) {
            Alert alert = new Alert(AlertType.WARNING, "Vous avez déjà effectué 3 lancers.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        
        int[] toReroll = getSelectedDice();
        if (isPlusOneActive && cbDe6.isSelected()) {
            // Ajouter l'indice 6 (ou 5 selon comment tu indexe) pour le 6ème dé
            toReroll = Arrays.copyOf(toReroll, toReroll.length + 1);
            toReroll[toReroll.length - 1] = 6; // Attention à l'indexation (0-based ou 1-based)
        }

        if (toReroll.length == 0 && nbRolls > 0) {
            Alert alert = new Alert(AlertType.WARNING, "Veuillez cocher au moins un dé à relancer.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        
        for (int dieIndex : toReroll) {
            switch (dieIndex) {
                case 1 -> animateDice(de1);
                case 2 -> animateDice(de2);
                case 3 -> animateDice(de3);
                case 4 -> animateDice(de4);
                case 5 -> animateDice(de5);
                case 6 -> animateDice(de6); // si PlusOne actif
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

        if (nbRolls >= maxRollsPerTurn) {
            rollButton.setDisable(true);
            rollButton.setOpacity(0.5);  // devient un peu "grisé"
        }
        
        clearBlockedDiceForCurrentPlayer();
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
        
        boolean sacrifice;
        if (isJokerActive) {
        	sacrifice = false;
        } else {
        	sacrifice = !combination.isValid(board);
        }
        
        if (currentSheet.isCombinationUsed(combination)) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Combinaison déjà utilisée");
            alert.setHeaderText("Vous avez déjà utilisé cette combinaison.");
            alert.setContentText("Veuillez en choisir une autre.");
            alert.showAndWait();
            return;
        }

        // Joker non actif, comportement normal
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
        	 int score = combination.score(board);
             if (isJokerActive) {
                 score /= 2;
                 isJokerActive = false;
                 CardExplanation.setVisible(false);
                 CardExplanation.setManaged(false);
             }
             if (isDoubleActive) {
            	    score *= 2;
            	    isDoubleActive = false;  // reset après usage
            	    CardExplanation.setVisible(false);
                    CardExplanation.setManaged(false);
             }
             currentSheet.recordScore(combination, score, false);
             addScoreToList(combination, score);
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
        
        if (isPlusOneActive) {
            board.removeSixthDie();
            isPlusOneActive = false;
            CardExplanation.setVisible(false);
            CardExplanation.setManaged(false);
        }

        de6.setVisible(false);
        cbDe6.setVisible(false);

        if (isRelanceActive) {
        	maxRollsPerTurn = maxRollsPerTurn - 1;
        	isRelanceActive = false;
        	CardExplanation.setVisible(false);
            CardExplanation.setManaged(false);
        }
        
        if (isGardeActive) {
            isGardeActive = false;
            CardExplanation.setVisible(false);
            CardExplanation.setManaged(false);
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
        updatePlayerCardsDisplay();
        updateBlockedDiceUI();
        
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
        
        if (isPlusOneActive && board.getDice().size() >= 6) {
            de6.setVisible(true);
            cbDe6.setVisible(true);
            updateDieImage(de6, board.getDice().get(5).value());
            cbDe6.setDisable(rollButton.isDisable());
        } else {
            de6.setVisible(false);
            cbDe6.setVisible(false);
        }

        turnLabel.setText("Tour " + currentRound);
        playerTurnLabel.setText(isPlayer1Turn ? "Au tour du Joueur 1" : "Au tour du Joueur 2");
        updateBlockedDiceUI();
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
