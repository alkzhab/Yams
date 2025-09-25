package model;

import java.util.ArrayList;
import java.util.List;

public class AIPlayer {

    // Choisit les dés à relancer pour améliorer la meilleure combinaison possible
    public int[] chooseDiceToReroll(Board board) {
        List<Dice> dice = board.getDice();

        int[] counts = new int[7];
        for (Dice d : dice) counts[d.value()]++;

        int maxValue = 1;
        int maxCount = counts[1];
        for (int i = 2; i <= 6; i++) {
            if (counts[i] > maxCount) {
                maxValue = i;
                maxCount = counts[i];
            }
        }

        List<Integer> toReroll = new ArrayList<>();
        for (int i = 0; i < dice.size(); i++) {
            if (dice.get(i).value() != maxValue) {
                toReroll.add(i + 1);
            }
        }

        return toReroll.stream().mapToInt(Integer::intValue).toArray();
    }

    // Choisit la meilleure combinaison encore disponible
    public Combination chooseCombination(ScoreSheet scoreSheet, Board board) {
        Combination[] combosPriority = {
            new YamsCombination(),
            new LargeStraight(),
            new SmallStraight(),
            new FullHouse(),
            new FourOfAKind(),
            new ThreeOfAKind(),
            new ChanceCombination()
        };

        for (Combination combo : combosPriority) {
            if (!scoreSheet.isCombinationUsed(combo) && combo.isValid(board)) {
                return combo;
            }
        }

        // Si aucune valide, on sacrifie la première non utilisée
        for (Combination combo : combosPriority) {
            if (!scoreSheet.isCombinationUsed(combo)) {
                return combo;
            }
        }

        // Au pire, retourne Chance
        return new ChanceCombination();
    }
}
