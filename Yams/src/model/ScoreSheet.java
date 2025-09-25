package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScoreSheet {

    private final HashMap<Combination, Integer> scoreMap = new HashMap<>();

    public boolean isCombinationUsed(Combination combination) {
        return scoreMap.containsKey(combination);
    }

    public boolean canScoreCombination(Combination combination, Board board) {
        if (isCombinationUsed(combination)) return false;
        return combination.isValid(board);
    }

    public void recordScore(Combination combination, Board board, boolean sacrifice) {
        Objects.requireNonNull(combination);
        if (isCombinationUsed(combination)) {
            throw new IllegalArgumentException("Combination already scored");
        }
        if (!combination.isValid(board) && !sacrifice) {
            throw new IllegalArgumentException("Combination invalid and not sacrificed");
        }
        int score = sacrifice ? 0 : combination.score(board);
        scoreMap.put(combination, score);
    }

    public void recordScore(Combination combination, int score, boolean sacrifice) {
        Objects.requireNonNull(combination);
        if (isCombinationUsed(combination)) {
            throw new IllegalArgumentException("Combination already scored");
        }
        if (sacrifice && score != 0) {
            throw new IllegalArgumentException("Sacrifice must have zero score");
        }
        scoreMap.put(combination, score);
    }



    public int scoreTotal() {
        return scoreMap.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        scoreMap.forEach((combination, score) -> {
            builder.append(combination.toString())
                   .append(": ")
                   .append(score)
                   .append("\n");
        });
        return builder.toString();
    }
    
    public Map<Combination, Integer> getScores() {
        return Collections.unmodifiableMap(scoreMap);
    }

}
