package org.jblackjack.model;

public class Card {
    private String suit;
    private String value;

    public Card(String suit, String value) {
        this.suit = suit;
        this.value = value;
    }
    public String getImagePath(){
        return "/" + this + ".png";
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value + "-" + suit;
    }
}