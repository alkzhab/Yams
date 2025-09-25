package model;

public record ChanceCombination() implements Combination {

	@Override
	public int score(Board board) {

		return board.sumOfDice();
	}

	@Override
	public String toString() {

		return "La Chance";
	}
	
	@Override
    public boolean isValid(Board board) {
        return true;
    }

}