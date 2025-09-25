package model;

public class Card {
    private String name;
    private String description;
    private boolean used;

    public Card(String name, String description) {
        this.name = name;
        this.description = description;
        this.used = false;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUsed() {
        return used;
    }

    public void use() {
        used = true;
    }

    @Override
    public String toString() {
        return name + (used ? " (utilisée)" : "");
    }
}
