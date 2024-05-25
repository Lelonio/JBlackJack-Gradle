package org.jblackjack.view;


import org.jblackjack.model.Card;
import org.jblackjack.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CardPanel extends JPanel {

    private final int[] animatePlayerCardWidth;
    private final ArrayList<Integer> playerSums;
    private final ArrayList<Card> dealerHand;

    private int animateDealerX = 20;

    private final BlackJackView view;
    private final int dealerSum;

    private boolean emptyTable;
    private final Card hiddenCard;
    private final ArrayList<Player> players;
    private final Timer hiddenCardTimer;
    private final Timer dealerCardTimer;

    private JLabel betStatus;

    private Timer timer;
    private Timer playerCardTimer;
    private boolean revealHiddenCard;
    private int animatePlayerIndex;
    private boolean isAnimatingCard;
    private final int cardWidth = 71;
    private final int cardHeight = 110;
    private final Color[] betBtnColor;
    private final Color standBtnColor;
    private final Color hitBtnColor;
    private ArrayList<Point> playerCardPositions;
    private ArrayList<Point> targetCardPositions;
    private int animateHiddenCardWidth;

    public CardPanel(ArrayList<Card> dealerHand, ArrayList<Player> players, ArrayList<Integer> playerSums, int dealerSum, BlackJackView view) {
        this.dealerHand = dealerHand;
        this.players = players;
        this.playerSums = playerSums;
        this.dealerSum = dealerSum;
        this.hiddenCard = dealerHand.get(0);
        this.revealHiddenCard = false;
        this.hiddenCardTimer = new Timer(3, null);
        this.dealerCardTimer = new Timer(3, null);
        this.playerCardTimer = new Timer(3, null);
        this.view = view;
        this.timer=null;
        int numPlayers = players.size();
        this.animatePlayerCardWidth = new int[numPlayers];
        this.betBtnColor = new Color[numPlayers];
        this.standBtnColor = new Color(201, 167, 79);
        this.hitBtnColor = new Color(201, 167, 79);

        for (int i = 0; i < numPlayers; i++) {
            this.animatePlayerCardWidth[i] = 71;
            this.betBtnColor[i] = new Color(201, 167, 79);
        }

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });

        this.playerCardPositions = new ArrayList<>(numPlayers);
        this.targetCardPositions = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            this.playerCardPositions.add(new Point());
            this.targetCardPositions.add(new Point());
        }
        this.isAnimatingCard = false;
        this.animatePlayerIndex = -1;
    }

    private void handleMouseClick(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        for (int i = 0; i < players.size(); i++) {
            int buttonY = 464 + 110 + 20;
            int buttonX = 303 + 293 * i;
            if (!view.getController().getStart()) {
                handleBetButtons(x, y, i, buttonX, buttonY);
            } else if (!view.getController().getGameOver() && !players.get(i).isStanding() && view.getController().getStart()) {
                handleGameButtons(x, y, i, buttonX, buttonY);
            }
        }
    }

    private void handleBetButtons(int x, int y, int playerIndex, int buttonX, int buttonY) {
        if (x >= buttonX + 10 && x <= buttonX + 40 && y >= buttonY - 13 && y <= buttonY + 17) {
            changeBet(playerIndex, false);
        }

        if (x >= buttonX + 90 && x <= buttonX + 120 && y >= buttonY - 13 && y <= buttonY + 17) {
            changeBet(playerIndex, true);
        }
    }

    private void handleGameButtons(int x, int y, int playerIndex, int buttonX, int buttonY) {
        if (x >= buttonX && x <= buttonX + 70 && y >= buttonY && y <= buttonY + 40) {
            view.getController().playerStand(playerIndex);
            repaint();
        }
        if (x >= buttonX + 110 && x <= buttonX + 170 && y >= buttonY - 13 && y <= buttonY + 17) {
            if (!isAnimatingCard) {
                view.getController().playerHit(playerIndex);
                repaint();
            }
        }
    }

    private void changeBet(int playerIndex, boolean increase) {
        view.getController().betting(playerIndex, increase);
        betBtnColor[playerIndex] = new Color(255, 100, 0);
        repaint();
        Timer timer = new Timer(200, e -> {
            betBtnColor[playerIndex] = new Color(201, 167, 79);
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void animateHiddenCard() {
        animateHiddenCardWidth = 71;
        hiddenCardTimer.addActionListener(e -> {
            animateHiddenCardWidth -= 1;
            if (animateHiddenCardWidth <= 0) {
                ((Timer) e.getSource()).stop();
                animateHiddenCardWidth = 71;
            }
            repaint();
        });
        hiddenCardTimer.start();
    }

    public void revealHiddenCard() {
        this.revealHiddenCard = true;
    }

    public void startPlayerCardAnimation(int playerIndex, Point startPoint, Point endPoint) {
        if (isAnimatingCard) return;
        animatePlayerIndex = playerIndex;
        isAnimatingCard = true;
        playerCardPositions.set(playerIndex, startPoint);
        targetCardPositions.set(playerIndex, endPoint);

        playerCardTimer = new Timer(5, e -> {
            Point currentPos = playerCardPositions.get(playerIndex);
            Point targetPos = targetCardPositions.get(playerIndex);
            if (currentPos.x < targetPos.x) currentPos.x += 2;
            if (currentPos.y < targetPos.y) currentPos.y += 2;
            playerCardPositions.set(playerIndex, currentPos);
            if (Math.abs(currentPos.x - targetPos.x) < 2 && Math.abs(currentPos.y - targetPos.y) < 2) {
                currentPos.setLocation(targetPos);
                ((Timer) e.getSource()).stop();
                isAnimatingCard = false;
            }
            repaint();
        });
        playerCardTimer.start();
    }

    private Image loadImage(String path) {
        return new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();
    }

    Image bg = new ImageIcon(getClass().getResource("/table.png")).getImage();
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, null);
        g.setFont(new Font("Arial", Font.TRUETYPE_FONT, 20));
        g.setColor(Color.WHITE);
        drawDealerStatus(g);
        drawDealerCards(g);
        drawCardStack(g);
        drawHiddenCard(g);
        drawPlayerInfo(g);
    }

    private void drawDealerStatus(Graphics g) {
        if (revealHiddenCard) {
            if (dealerSum == 21) g.drawString("Blackjack!", 475, 180);
            else if (dealerSum > 21) g.drawString("Busted! " + dealerSum, 475, 180);
            else g.drawString("" + dealerSum, 505, 180);
        }
    }

    private void drawCardStack(Graphics g) {
        Image cardBackImg = loadImage("/BACK.png");
        int stackX = 280;
        int stackY = 23;
        for (int i = 0; i < 10; i++) {
            g.drawImage(cardBackImg, stackX + i, stackY + i, 71, 110, null);
        }
    }

    private void drawHiddenCard(Graphics g) {
        Image hiddenCardImg;
        if (revealHiddenCard && !hiddenCardTimer.isRunning()) {
            hiddenCardImg = loadImage(hiddenCard.getImagePath());
        } else {
            hiddenCardImg = loadImage("/BACK.png");
        }
        if (hiddenCardTimer.isRunning()) {
            g.drawImage(hiddenCardImg, 420, 23, animateHiddenCardWidth, 110, null);
        } else {
            g.drawImage(hiddenCardImg, 420, 23, 71, 110, null);
        }
    }


    private void drawPlayerInfo(Graphics g) {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            int y = 464;
            int xBtn = 303 + 293 * i;
            int yBtn = y + 110 + 20;
            drawPlayerBets(g, player, i, yBtn, xBtn);
            drawPlayerCards(g, player, i, y);
            drawPlayerStatus(g, player, i, y);
        }
    }

    private void drawPlayerBets(Graphics g, Player player, int playerIndex, int yBtn, int xBtn) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        int panelHeight = this.getHeight();
        g.drawString(player.getName() + " bet: " + player.getAmount(), 10, panelHeight - 10 - playerIndex * 30);

        if (!view.getController().getStart()) {
            drawBetButtons(g, playerIndex, yBtn, xBtn);
        } else if (!player.isStanding() && !player.isBusted() && view.getController().getStart()) {
            drawActionButtons(g, xBtn, yBtn);
        }
    }

    private void drawBetButtons(Graphics g, int playerIndex, int yBtn, int xBtn) {
        g.setColor(betBtnColor[playerIndex]);
        g.fillOval(xBtn + 10, yBtn - 13, 30, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.TRUETYPE_FONT, 22));
        g.drawString("-", xBtn + 20, yBtn + 10);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.TRUETYPE_FONT, 20));
        g.drawString("Bet", xBtn + 50, yBtn + 10);

        g.setColor(betBtnColor[playerIndex]);
        g.fillOval(xBtn + 90, yBtn - 13, 30, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.TRUETYPE_FONT, 20));
        g.drawString("+", xBtn + 98, yBtn + 10);
    }

    private void drawActionButtons(Graphics g, int xBtn, int yBtn) {
        g.setColor(standBtnColor);
        g.fillOval(xBtn + 5, yBtn - 13, 60, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.TRUETYPE_FONT, 20));
        g.drawString("Stand", xBtn + 10, yBtn + 10);

        int hitButtonX = xBtn + 30 + 80;
        g.setColor(hitBtnColor);
        g.fillOval(hitButtonX - 7, yBtn - 13, 60, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.TRUETYPE_FONT, 20));
        g.drawString("Hit", hitButtonX + 10, yBtn + 10);
    }

    private void drawPlayerCards(Graphics g, Player player, int playerIndex, int y) {
        ArrayList<Card> playerHand = player.getHand();
        for (int j = 0; j < playerHand.size(); j++) {
            Card card = playerHand.get(j);
            Image cardImg = loadImage(card.getImagePath());

            int x = 343 + 20 * j + 283 * playerIndex;
            if (isAnimatingCard && animatePlayerIndex == playerIndex && j == playerHand.size() - 1) {
                drawAnimatingCard(g, player, playerIndex, cardImg, x, y);
            } else {
                drawCard(g, player, playerIndex, cardImg, x, y, j);
            }
        }
    }

    private void drawDealerCards(Graphics g){
        for (int j = 1; j < dealerHand.size(); j++) {
            Card card = dealerHand.get(j);
            Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
            if (j == dealerHand.size() - 1 && dealerCardTimer.isRunning()) {
                g.drawImage(cardImg, animateDealerX - (cardWidth + 5), 23, cardWidth, cardHeight, null);
            } else {
                g.drawImage(cardImg, cardWidth + 420 + 20 * j, 23, cardWidth, cardHeight, null);
            }
        }
    }

    private void drawAnimatingCard(Graphics g, Player player, int playerIndex, Image cardImg, int x, int y) {
        Point currentPos = playerCardPositions.get(playerIndex);
        Graphics2D g2d = (Graphics2D) g.create();
        if (player.isBusted()) {
            cardImg = loadImage("/BACK.png");
        }
        rotateCard(g2d, playerIndex, x, y);
        g2d.drawImage(cardImg, currentPos.x - 5, currentPos.y - 8, 71, 110, null);
        g2d.dispose();
    }

    private void drawCard(Graphics g, Player player, int playerIndex, Image cardImg, int x, int y, int cardIndex) {
        if (player.isBusted() || player.isFolded()) {
            cardImg = loadImage("/BACK.png");
        }
        Graphics2D g2d = (Graphics2D) g.create();
        rotateCard(g2d, playerIndex, x, y);
        g2d.drawImage(cardImg, x - 10, y - 8, (cardIndex == player.getHand().size() - 1) ? animatePlayerCardWidth[playerIndex] : 71, 110, null);
        g2d.dispose();
    }

    private void rotateCard(Graphics2D g2d, int playerIndex, int x, int y) {
        if (playerIndex == 1) {
            g2d.rotate(Math.toRadians(-7.5), x + animatePlayerCardWidth[playerIndex] / 2, y + 110 / 2);
        } else {
            g2d.rotate(Math.toRadians(7.5), x + animatePlayerCardWidth[playerIndex] / 2, y + 110 / 2);
        }
    }

    private void drawPlayerStatus(Graphics g, Player player, int playerIndex, int y) {
        String winText = player.getName() + " wins: " + player.getWins();
        String lossText = player.getName() + " losses: " + player.getLosses();
        String creditText = player.getName() + " credits: " + player.getCredits();
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.ITALIC, 20));
        int textX = (playerIndex == 0) ? 10 : this.getWidth() - g.getFontMetrics().stringWidth(winText) - player.getName().length() - 50;

        g.drawString(winText, textX, 40);
        g.drawString(lossText, textX, 70);
        g.drawString(creditText, textX, 100);

        if (playerIndex == 1) {
            drawPlayerFoldOrBust(g, player, playerIndex, y);
        } else if (playerIndex == 0) {
            drawPlayerFoldOrBust(g, player, playerIndex, y - 70);
        }
    }

    private void drawPlayerFoldOrBust(Graphics g, Player player, int playerIndex, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        if (player.isFolded() && !view.getController().getGameOver()) {
            rotateCard(g2d, playerIndex, 335 + 275 * playerIndex, y);
            drawPlayerStatusText(g2d, player, "Folded! ", 370 + 260 * playerIndex, y, y + 48 -70*playerIndex);
        } else if (player.isBusted()) {
            rotateCard(g2d, playerIndex, 335 + 275 * playerIndex, y);
            drawPlayerStatusText(g2d, player, "Busted! " + playerSums.get(playerIndex), 370 + 250 * playerIndex, y, y + 48 -70*playerIndex);
        } else if (!player.isFolded()) {
            rotateCard(g2d, playerIndex, 335 + 275 * playerIndex, y);
            drawPlayerStatusText(g2d, player, (playerSums.get(playerIndex) == 21) ? "BlackJack!" : "" + playerSums.get(playerIndex), 370 + 265 * playerIndex, y, y + 48 -70*playerIndex);
        }
        g2d.dispose();
    }

    private void drawPlayerStatusText(Graphics2D g2d, Player player, String status, int x, int y, int yText) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString(player.getName(), x-15 - 20 * players.indexOf(player), y+20- 70 * players.indexOf(player));
        g2d.setFont(new Font("Arial", Font.TRUETYPE_FONT, 20));
        g2d.drawString(status, x + 5, yText);
    }

    public void setEmptyBackground() {
        this.bg = new ImageIcon(getClass().getResource("/tableEmpty.png")).getImage();

    }

    public void setBackground() {
        this.bg = new ImageIcon(getClass().getResource("/table.png")).getImage();

    }

    public void setStatus(String status) {
        setEmptyBackground();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // Add a top padding of 10 pixels
        gbc.insets = new Insets(0, 0, 150, 0);

        JLabel statusLabel = new JLabel(status, SwingConstants.CENTER);
        statusLabel.setFont(new Font("Times New Roman", Font.PLAIN, 30));
        statusLabel.setForeground(Color.WHITE);

        add(statusLabel, gbc);
    }


    public void setCountdown(Timer timer) {
        this.timer = timer;
        if (this.timer != null) {
            this.timer.stop();
        }

        setEmptyBackground();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 1;
        view.getFrame().remove(view.gameOver);
        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setVisible(true);
        statusLabel.setFont(new Font("Times New Roman", Font.BOLD, 40));
        statusLabel.setForeground(Color.WHITE);
        gbc.insets = new Insets(0, 0, 150, 0);

        add(statusLabel, gbc);
        repaint();
        view.numPlayersButton.setEnabled(false);
        view.newGameButton.setEnabled(false);
        statusLabel.setText("Place your bet!");

        ActionListener taskPerformer = new ActionListener() {
            int i = 10;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (i > 0) {
                    view.getController().playTic();
                    statusLabel.setText("" + i);
                    i--;
                } else {
                    ((Timer) e.getSource()).stop();
                    setBackground();
                    view.getController().setStart(true);
                    remove(statusLabel);
                    for (Player player : players) {
                        if (player.getAmount() == 0) {
                            player.setFolded(true);
                            player.stand();
                        }
                    }
                    view.getController().checkFolded();
                    view.numPlayersButton.setEnabled(true);
                    view.newGameButton.setEnabled(true);
                    setBetStatus(" ");
                    repaint();
                }
            }
        };

        this.timer = new Timer(1000, taskPerformer);
        this.timer.start();
    }

    public void setBetStatus(String s) {
        if (betStatus == null) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.SOUTH;
            gbc.weightx = 1;
            gbc.weighty = -30;
            betStatus = new JLabel(s, SwingConstants.CENTER);
            betStatus.setFont(new Font("Times New Roman", Font.BOLD, 20));
            betStatus.setForeground(Color.WHITE);
            add(betStatus, gbc);
        } else {
            betStatus.setText(s);
        }
    }
}
