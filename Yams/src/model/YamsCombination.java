package model;

import java.util.List;
import java.util.stream.Collectors;

public record YamsCombination() implements Combination {

	@Override
	public int score(Board board) {

		return 50;
	}

	@Override
	public String toString() {

		return "Le Yams";
	}
	
	@Override
    public boolean isValid(Board board) {
        List<Integer> diceValues = board.getDice().stream()
            .map(Dice::value)
            .distinct()  // On ne garde que les valeurs uniques
            .collect(Collectors.toList());

        // Si il y a une seule valeur unique dans les dés, c'est un Yams
        return diceValues.size() == 1;
    }

}