package org.jblackjack.model;

import java.util.*;


public class BlackJackModel {

    private ArrayList<Card> deck;
    private ArrayList<Card> dealerHand;

    private ArrayList<Player> players;
    private boolean gameOver;

    public BlackJackModel() {

        deck = new ArrayList<>();

        players = new ArrayList<>();
        dealerHand = new ArrayList<>();
        gameOver = false;

        initializeDeck();

    }

    // Initializes a standard deck of 52 cards
    public void initializeDeck() {
        String[] suits = {"H", "D", "C", "S"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        for (String rank : ranks) {
            for (String suit : suits) {
                deck.add(new Card(suit, rank));
            }
        }
        Collections.shuffle(deck);

    }

    public boolean setAmount(Player player){

        return player.getAmount() < player.getCredits();

    }

    public void newGame(){

        dealerHand.clear();
        gameOver = false;
        Collections.shuffle(deck);

        // Deal two cards to each player and dealer
        for (Player player : players) {
            player.setFolded(false);
            player.setBusted(false);
            player.setStanding(false);
            player.getHand().clear();
            player.getHand().add(deck.remove(deck.size() - 1));
            player.getHand().add(deck.remove(deck.size() - 1));


        }

        dealerHand.add(deck.remove(deck.size() - 1));
        dealerHand.add(deck.remove(deck.size() - 1));
    }

    public void initializeGame(int numPlayers, int initialCredits, List<String> names) {
        // Clear players list before adding new players
        players.clear();
        dealerHand.clear();
        gameOver = false;
        Collections.shuffle(deck);
        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player(initialCredits, this,names.get(i)));

        }


        // Deal two cards to each player and dealer
        for (Player player : players) {
            player.getHand().add(deck.remove(deck.size() - 1));
            player.getHand().add(deck.remove(deck.size() - 1));
            //System.out.println("do 2 carte");

        }

        dealerHand.add(deck.remove(deck.size() - 1));
        dealerHand.add(deck.remove(deck.size() - 1));
    }

    // Starts a new game
    //

    // Player takes a hit
    public void playerHits(Player player) {
        if (getHandValue(player.getHand()) <= 21) {
            player.getHand().add(deck.remove(deck.size() - 1));
        } else if (getHandValue(player.getHand()) == 21) {  // Check the hand of the player object
            dealerHit();
        }
    }

    // Player stands
    public void dealerHit() {
        if (!gameOver) {
            while (getHandValue(dealerHand) < 17) {
                dealerHand.add(deck.remove(deck.size() - 1));

            }

            gameOver = true;
        }
    }



    // Calculates the value of a hand
    public int getHandValue(ArrayList<Card> hand) {
        int value = 0;
        int aceCount = 0;

        for (Card card : hand) {
            if (card.getValue().equals("A")) {
                aceCount++;
                value += 11;
            } else if (card.getValue().matches("[JQK]")) {
                value += 10;
            } else {
                value += Integer.parseInt(card.getValue());
            }
        }

        while (value > 21 && aceCount > 0) {
            value -= 10; // Convert an Ace from 11 to 1
            aceCount--;
        }

        return value;
    }

    // Getters and setters

    public ArrayList<Player> getPlayers() {

        return players;
    }
    public ArrayList<Card> getDealerHand() {
        return dealerHand;
    }

    public boolean isGameOver() {
        return gameOver;
    }


    public ArrayList<Integer> getAllPlayerSums() {
        ArrayList<Integer> allPlayerSums = new ArrayList<>();
        for (Player player : players) {
            allPlayerSums.add(getHandValue(player.getHand()));
        }
        return allPlayerSums;
    }


    public boolean playerHasBlackJack(Player player) {
        return player.getHand().size() == 2 && getHandValue(player.getHand()) == 21;
    }

    // Checks if the player is busted (hand value over 21)
    public boolean playerIsBusted(Player player) {
        if (getHandValue(player.getHand()) > 21){
            player.setBusted(true);
            player.stand();
            return true;
        }
        return false;
    }


    // Checks if the dealer is busted (hand value over 21)
    public boolean dealerIsBusted() {
        return getHandValue(dealerHand) > 21;
    }

    // Checks if the game is a tie (both player and dealer have the same hand value)
    public boolean isTie(Player player) {
        return getHandValue(player.getHand()) == getHandValue(dealerHand);
    }

    public boolean allPlayersFolded(){
        for (Player player : players) {
            if (!player.isFolded()) {
                return false;
            }
        }
        gameOver=true;
        return true;
    }



    // Checks if the player wins (player has a higher hand value than the dealer, and neither is busted)
    public boolean playerWins(Player player) {
        int playerValue = getHandValue(player.getHand());
        int dealerValue = getHandValue(dealerHand);
        return !playerIsBusted(player) && !dealerIsBusted() && playerValue > dealerValue;
    }




    public void clearAmount() {
        for (Player player : players) {
            player.clearAmount();
        }
    }

    public void isPlayerBroke() {
        players.removeIf(player -> player.getCredits() == 0);
    }

}
