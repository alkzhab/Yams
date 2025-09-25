package model;

import java.util.Random;

public class Dice {

    private int value;

    public Dice(int value) {
        if (value > 6 || value < 1) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    public Dice() {
        this(new Random().nextInt(6) + 1);
    }

    public Dice reroll() {
        return new Dice();
    }

    public void forceValue(int value) {
        if (value > 6 || value < 1) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return "-------\n" + "|  " + value + "  |\n" + "-------\n";
    }
}
