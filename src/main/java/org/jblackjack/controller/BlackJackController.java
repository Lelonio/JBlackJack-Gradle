package org.jblackjack.controller;

import org.jblackjack.model.BlackJackModel;
import org.jblackjack.view.BlackJackView;
import org.jblackjack.model.Player;
import org.jblackjack.view.SetupDialog;

import javax.swing.Timer;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BlackJackController {

    private Timer timer;
    AudioManager audioManager = new AudioManager();
    private final BlackJackModel model;
    private final BlackJackView view;
    private Player currentPlayer;

    private boolean isAnimating;

    SetupDialog dialog;

    public boolean start;

    public BlackJackController(BlackJackView view, BlackJackModel model) {
        this.view = view;
        this.view.setController(this);
        this.model = model;
        this.start = false;
        this.isAnimating=false;
        dialog = new SetupDialog(view.getFrame());
        dialog.setVisible(true);
    }

    public void playerHit(int currentPlayerIndex) {
        if (isAnimating) return;
        this.currentPlayer = model.getPlayers().get(currentPlayerIndex);
        if (!this.model.isGameOver()) {
            this.model.playerHits(model.getPlayers().get(currentPlayerIndex));
            this.updateView();
            this.audioManager.play("dealt.wav");

            // Animation for dealing the card
            Point startPoint = new Point(280, 23); // Starting from the deck position
            Point endPoint = new Point(343 + 20 * (currentPlayer.getHand().size() - 1) + 283 * currentPlayerIndex, 464); // Target card position
            view.getCardPanel().startPlayerCardAnimation(currentPlayerIndex, startPoint, endPoint);
            view.getCardPanel().repaint();
            isAnimating = true;

            // Adding a delay to ensure the animation is visible
            Timer animationTimer = new Timer(1700, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    isAnimating = false;
                    if (model.playerIsBusted(currentPlayer)) {
                        audioManager.play("lose.wav");
                        view.getFrame().revalidate();
                        view.getFrame().repaint();
                        processNextPlayer(currentPlayerIndex);
                    } else if (model.getHandValue(currentPlayer.getHand()) == 21) {
                        view.getCardPanel().repaint();
                        playerStand(model.getPlayers().indexOf(currentPlayer));
                    }
                }
            });
            animationTimer.setRepeats(false);
            animationTimer.start();
        }
    }

    private void processNextPlayer(int currentPlayerIndex) {
        try {
            int playerCount = this.model.getPlayers().size();
            if (playerCount == 1) {
                // Handle the case when there is only one player
                this.model.dealerHit();

                this.checkGameStatus();
            } else if (currentPlayerIndex < playerCount - 1) {
                // Move to the next player
                this.playerStand(model.getPlayers().indexOf(this.currentPlayer));
                this.currentPlayer = this.model.getPlayers().get(currentPlayerIndex + 1);
                this.view.getFrame().revalidate();
                this.view.getFrame().repaint();
            } else if (currentPlayerIndex == playerCount - 1) {
                // Handle the last player
                this.playerStand(model.getPlayers().indexOf(this.currentPlayer));
                this.currentPlayer = this.model.getPlayers().get(currentPlayerIndex - 1);
                this.view.getFrame().revalidate();
                this.view.getFrame().repaint();
            } else {
                // Default case, dealer plays
                this.model.dealerHit();

                this.checkGameStatus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void playerStand(int index) {
        Player currentPlayer = model.getPlayers().get(index);
        currentPlayer.stand();
        Player nextPlayer = null;
        for (int i = 0; i < model.getPlayers().size(); i++) {
            if (!model.getPlayers().get(i).isStanding()) {
                nextPlayer = model.getPlayers().get(i);
                break;
            }
        }

        if (nextPlayer != null) {
            this.currentPlayer = nextPlayer;
        } else {
            // Otherwise, it's the dealer's turn
            model.dealerHit();
            updateView();
            view.getCardPanel().animateHiddenCard();
            view.getCardPanel().revealHiddenCard();
            audioManager.play("dealt.wav");



            Timer outcomeTimer = new Timer(1200, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    checkGameStatus();
                }
            });
            outcomeTimer.setRepeats(false);
            outcomeTimer.start();
        }

        view.repaint();
    }

    public void newGame() {
        model.initializeDeck();
        model.newGame();
        model.clearAmount();
        model.isPlayerBroke();

        start = false;
        if (model.getPlayers().size() == 0) {
            view.gameOver();
            audioManager.play("lose.wav");
        } else {
            this.currentPlayer = model.getPlayers().get(0);
            audioManager.play("shuffle.wav");
            updateView();
            view.getFrame().remove(view.gameOver);
            view.revalidate();
            view.repaint();
            view.startCountdown(new Timer(1000, null));
        }
    }

    public void startGame() {
        model.initializeDeck();
        model.initializeGame(dialog.getNumPlayers(), dialog.getBetAmount(), dialog.getPlayerNames());
        this.currentPlayer = model.getPlayers().get(0);
        start = false;
        audioManager.play("shuffle.wav");
        updateView();
        view.getFrame().remove(view.gameOver);
        view.revalidate();
        view.repaint();
        view.startCountdown(null);
    }

    private void updateView() {
        view.updateHands(model.getDealerHand(), model.getPlayers(), model.getAllPlayerSums(), model.getHandValue(model.getDealerHand()));
    }

    public void betting(int currentPlayerIndex, boolean b) {
        if (!model.setAmount(model.getPlayers().get(currentPlayerIndex)) && b) {
            view.getCardPanel().setBetStatus(model.getPlayers().get(currentPlayerIndex).getName() + " has insufficient credits!");
            audioManager.play("nocredits.wav");
            int delay = 2000;
            ActionListener taskPerformer = evt -> {
                view.getCardPanel().setBetStatus(" ");
                view.getCardPanel().repaint();
            };

            if (timer != null) {
                timer.stop();
            }
            timer = new Timer(delay, taskPerformer);
            timer.start();
        } else if (b) {
            audioManager.play("credits.wav");
            model.getPlayers().get(currentPlayerIndex).setAmount();
        } else if (!b) {
            audioManager.play("credits.wav");
            model.getPlayers().get(currentPlayerIndex).decreaseAmount();
        }
    }

    private void checkGameStatus() {
        if (model.isGameOver()) {
            StringBuilder statusBuilder = new StringBuilder();
            statusBuilder.append("<html>");
            boolean winSoundPlayed = false;
            boolean loseSoundPlayed = false;
            boolean tieSoundPlayed = false;
            for (Player player : model.getPlayers()) {
                String message;
                if (!player.isFolded()) {
                    if (model.playerHasBlackJack(player)) {
                        message = "<b> " + player.getName() + "</b> has <i>Blackjack</i>! It wins! 🎉";
                        player.incrementWins();
                        player.winBet();
                        if (!winSoundPlayed) {
                            audioManager.play("win.wav");
                            winSoundPlayed = true;
                        }
                    } else if (model.playerIsBusted(player)) {
                        message = "<b> " + player.getName() + "</b> is busted! 😞";
                        player.incrementLosses();
                        player.loseBet();
                        if (!loseSoundPlayed) {
                            audioManager.play("lose.wav");
                            loseSoundPlayed = true;
                        }
                    } else if (model.dealerIsBusted()) {
                        message = "Dealer is busted! <b> " + player.getName() + "</b> wins! 🎉";
                        player.incrementWins();
                        player.winBet();
                        if (!winSoundPlayed) {
                            audioManager.play("win.wav");
                            winSoundPlayed = true;
                        }
                    } else if (model.isTie(player)) {
                        message = "It's a tie for <b> " + player.getName() + "</b>! 😐";
                        if (!tieSoundPlayed) {
                            audioManager.play("tie.wav");
                            tieSoundPlayed = true;
                        }
                    } else if (model.playerWins(player)) {
                        message = "<b> " + player.getName() + "</b> wins! 🎉";
                        player.incrementWins();
                        player.winBet();
                        if (!winSoundPlayed) {
                            audioManager.play("win.wav");
                            winSoundPlayed = true;
                        }
                    } else {
                        message = "<b> " + player.getName() + "</b> loses! 😞";
                        player.setBusted(true);
                        player.incrementLosses();
                        player.loseBet();
                        if (!loseSoundPlayed) {
                            audioManager.play("lose.wav");
                            loseSoundPlayed = true;
                        }
                    }
                    statusBuilder.append(message).append("<br>");
                }
            }
            if (statusBuilder.toString().equals("<html>")) {
                statusBuilder.append("All players folded! <br>");
            }
            statusBuilder.append("</html>");
            view.getCardPanel().setStatus(statusBuilder.toString());
            view.getCardPanel().setEmptyBackground();
            view.getFrame().revalidate();
            view.getFrame().repaint();
        }
    }

    public void changePlayers() {
        dialog = new SetupDialog(view.getFrame());
        dialog.setVisible(true);
        startGame();
    }

    public boolean getGameOver() {
        return model.isGameOver();
    }

    public void setStart(boolean b) {
        this.start = b;
    }

    public boolean getStart() {
        return start;
    }

    public void checkFolded() {
        model.allPlayersFolded();
        checkGameStatus();
    }

    public void playTic() {

        audioManager.play("tic.wav");
    }
}
