package model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record SmallStraight() implements Combination {

	@Override
	public int score(Board board) {

		return 30;
	}

	@Override
	public String toString() {

		return "La petite suite";
	}
	
	@Override
	public boolean isValid(Board board) {
	    List<Integer> diceValues = board.getDice().stream()
	        .map(Dice::value)
	        .distinct()
	        .sorted()  // Trier les valeurs des dés pour vérifier les suites
	        .collect(Collectors.toList());

	    // Les petites suites possibles
	    List<List<Integer>> possibleStraights = Arrays.asList(
	        Arrays.asList(1, 2, 3, 4),
	        Arrays.asList(2, 3, 4, 5),
	        Arrays.asList(3, 4, 5, 6)
	    );

	    // Vérifier si l'une des petites suites est contenue dans le tirage
	    return possibleStraights.stream().anyMatch(diceValues::containsAll);
	}


}