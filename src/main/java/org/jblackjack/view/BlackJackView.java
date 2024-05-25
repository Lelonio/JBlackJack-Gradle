package org.jblackjack.view;

import org.jblackjack.controller.BlackJackController;
import org.jblackjack.model.Card;
import org.jblackjack.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

public class BlackJackView extends JPanel {


    private CardPanel cardPanel;


    JFrame frame = new JFrame("JBlackJack");




    int boardWidth = 1024;
    int boardHeight = 736;

    public JLabel gameOver;

    public JButton newGameButton;

    public JButton numPlayersButton;


    JPanel buttonPanel = new JPanel();

    private BlackJackController controller; // client GUI controller



    public BlackJackView(){

        try {
            // Print statements for debugging
            System.out.println("Trying to load icon...");
            URL iconURL = getClass().getResource("/icon.png");
            if (iconURL != null) {
                Image icon = new ImageIcon(iconURL).getImage();
                frame.setIconImage(icon);
                System.out.println("Icon loaded successfully.");
            } else {
                System.err.println("Icon not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame.getContentPane().setBackground(new Color(0,119,56));
        gameOver = new JLabel("GAME OVER!"+"\n"+"No credits left!", SwingConstants.CENTER); // Center align text
        gameOver.setVisible(false);

        frame.setVisible(true);
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);




        numPlayersButton = new JButton("Reset Game");
        newGameButton = new JButton("New Game");


        int gap = 50; // Set the gap size as per your requirement
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, gap, 0));
        buttonPanel.add(newGameButton);
        buttonPanel.add(numPlayersButton);
        buttonPanel.setBackground(new Color(0,119,56));// or any left-to-right layout
        frame.add(buttonPanel, BorderLayout.SOUTH); // Add after playerHandPanel

        setVisible(true);

    }

    public void startCountdown(Timer timer){


        getCardPanel().setCountdown(timer);
    }

    public void updateHands(ArrayList<Card> dealerHand, ArrayList<Player> players, ArrayList<Integer> playerSums, int dealerSum) {
        if (cardPanel != null) {
            frame.remove(cardPanel); // Remove the old CardPanel
        }
        cardPanel = new CardPanel(dealerHand, players, playerSums,dealerSum,this); // Create a new CardPanel

        frame.add(cardPanel, BorderLayout.CENTER);


        frame.revalidate(); // To make sure the JFrame updates to show the new cards
        frame.repaint(); // Redraw the JFrame

    }

    public void gameOver(){
        frame.remove(cardPanel);
        newGameButton.setEnabled(false);
        gameOver.setVisible(true);
        gameOver.setFont(new Font("Times New Roman", Font.BOLD, 50));
        gameOver.setForeground(Color.white);
        frame.add(gameOver,BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();

    }


    public void setController(BlackJackController controller) {
        this.controller=controller;

        this.newGameButton.addActionListener(e -> controller.newGame());
        this.numPlayersButton.addActionListener(e -> controller.changePlayers());
    }

    public BlackJackController getController(){
        return this.controller;
    }


    public CardPanel getCardPanel() {
        return cardPanel;
    }

    public JFrame getFrame() {
        return frame;
    }

    public JPanel getButtonPanel(){
        return buttonPanel;
    }


}

