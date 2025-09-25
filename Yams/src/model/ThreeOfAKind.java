package model;

import java.util.Map;
import java.util.stream.Collectors;

public record ThreeOfAKind() implements Combination {

	@Override
	public int score(Board board) {

		return 15;
	}

	@Override
	public String toString() {

		return "Le Brelan";
	}
	
	@Override
    public boolean isValid(Board board) {
        Map<Integer, Long> counts = board.getDice().stream().collect(Collectors.groupingBy(Dice::value, Collectors.counting()));

        return counts.values().stream().anyMatch(count -> count >= 3);
    }

}