package org.jblackjack.model;

import java.util.ArrayList;

public class Player {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private ArrayList<Card> hand;
    private boolean broke;
    private int amount;
    private int credits;

    private int wins;



    private int losses;
    private boolean isBusted;

    private boolean isStanding;

    public boolean isFolded() {
        return isFolded;
    }

    public void setFolded(boolean folded) {
        isFolded = folded;
    }

    private boolean isFolded;
    private BlackJackModel model;

    public Player(int credits, BlackJackModel model, String name) {
        this.name=name.toUpperCase();
        this.hand = new ArrayList<>();
        this.credits = credits;
        this.isBusted = false;
        this.model = model;
        this.isStanding=false;
        this.wins=0;
        this.losses=0;
        this.amount=0;
        this.broke=false;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public int getCredits() {
        return credits;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void incrementWins(){
        this.wins ++;
    }

    public void incrementLosses(){
        this.losses ++;

    }

    public int getWins(){
        return wins;
    }

    public boolean isBusted() {
        return isBusted;
    }

    public void setBusted(boolean busted) {
        isBusted = busted;
    }

    public boolean isStanding() {
        return isStanding;
    }

    public void stand() {
        this.isStanding = true;
    }

    public void winBet() {
        // Subtract the bet amount from the player's credits
        this.credits += this.amount*2;
    }

    public void loseBet() {
        // Subtract the bet amount from the player's credits
        this.credits -= this.amount;
    }

    public void setBroke(){
        this.broke=true;
    }

    public boolean isBroke(){
        return this.broke;
    }





    public int getHandValue() {
        return model.getHandValue(this.hand);
    }


    public void setStanding(boolean b) {
        this.isStanding=b;
    }

    public void setAmount() {
        this.amount+=10;
    }

    public void decreaseAmount(){this.amount-=10;}

    public void clearAmount(){
        this.amount=0;
    }

    public int getAmount(){
        return this.amount;
    }
}
