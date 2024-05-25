package org.jblackjack;

import org.jblackjack.controller.BlackJackController;
import org.jblackjack.model.BlackJackModel;
import org.jblackjack.view.BlackJackView;

public class Main {
    public static void main(String[] args){

        BlackJackModel model = new BlackJackModel();
        BlackJackView view = new BlackJackView();
        BlackJackController controller = new BlackJackController(view,model);

        controller.startGame();
    }}