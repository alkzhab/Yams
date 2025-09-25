package model;

import java.util.Map;
import java.util.stream.Collectors;

public record FullHouse() implements Combination {

	@Override
	public int score(Board board) {

		return 25;
	}

	@Override
	public String toString() {

		return "Le Full";
	}
	
	@Override
    public boolean isValid(Board board) {
        // Compte combien de fois chaque valeur de dé apparaît
        Map<Integer, Long> counts = board.getDice().stream()
            .collect(Collectors.groupingBy(Dice::value, Collectors.counting()));

        // Il faut avoir exactement une valeur qui apparait 3 fois et une autre qui apparait 2 fois
        return counts.containsValue(3L) && counts.containsValue(2L) && counts.size() == 2;
    }

}