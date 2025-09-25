package model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record LargeStraight() implements Combination {

	@Override
	public int score(Board board) {

		return 40;
	}

	@Override
	public String toString() {

		return "La grande suite";
	}
	
	@Override
	public boolean isValid(Board board) {
	    List<Integer> diceValues = board.getDice().stream()
	        .map(Dice::value)
	        .distinct()  // Garder uniquement les valeurs uniques
	        .sorted()  // Trier les valeurs des dés
	        .collect(Collectors.toList());

	    // Les grandes suites possibles
	    List<List<Integer>> possibleStraights = Arrays.asList(
	        Arrays.asList(1, 2, 3, 4, 5),
	        Arrays.asList(2, 3, 4, 5, 6)
	    );

	    // Vérifier si l'une des grandes suites est contenue dans le tirage
	    return possibleStraights.stream().anyMatch(diceValues::containsAll);
	}


}