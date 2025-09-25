package model;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final ArrayList<Dice> diceList = new ArrayList<Dice>();

    public Board() {
        for (int i = 1; i <= 5; i++) {
            diceList.add(new Dice());
        }
    }

    public void addSixthDie() {
        if (diceList.size() < 6) {
            diceList.add(new Dice());
        }
    }

    public void removeSixthDie() {
        if (diceList.size() == 6) {
            diceList.remove(5); // supprime le 6ème dé (index 5)
        }
    }
    
    

    @Override
    public String toString() {
        var builder = new StringBuilder();
        for (Dice d : diceList) {
            builder.append(d.toString());
        }
        builder.append("\n").append("-----------------\n");
        return builder.toString();
    }

    public void reroll(int[] positions) {
        for (int pos : positions) {
            if (pos < 1 || pos > diceList.size()) {
                throw new IllegalArgumentException("Invalid dice position: " + pos);
            }
            diceList.set(pos - 1, new Dice());
        }
    }

    public List<Dice> getDice() {
        return new ArrayList<>(diceList);
    }

    public void rerollAll() {
        for (int i = 1; i <= diceList.size(); i++) {
            reroll(new int[]{i});
        }
    }

    public int sumOfDice() {
        return diceList.stream().mapToInt(Dice::value).sum();
    }
    
    
}
