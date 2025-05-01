import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class GUIController {

    Trick trick;
    public Client client;

    Image assignImage(Card card) {
        return new Image("/images/" + card.number + card.suit + ".png");
    }

    public void initButtons() {
        System.out.println("buttons initialized!");

        b5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                trick.trump();
                b5.setDisable(true);
                try {
                    client.sendTrick();
                    
                } catch (Exception error) {
                    System.out.println("ASSSHASD");
                }
            }
        });

        b6.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    client.sendTrick();
                    trick = client.reciveTrick();
                    b6.setDisable(true);
                } catch (Exception error) {
                    System.out.println(error);
                }
            }
        });
    }

    public void dealerCards(ArrayList<Button> carButtons) {
        for(int i = 0; i < carButtons.size(); i++) {
            Button b = carButtons.get(i);
            final int index = i;
            b.setOnAction(new EventHandler<ActionEvent>() { 
                @Override
                public void handle(ActionEvent e) {
                    System.out.println(trick.dealer.cards);
                    trick.dealerTrade(trick.dealer.cards.get(index));
                    client.setTrick(trick);
                    try {
                        client.sendTrick();
                        trick.setCurrentPlayer(trick.dealer);
                        refreshHand();
                    } catch (Exception error) {
                        System.out.println(error);
                    }
                }
            });
        }
    }

    public void regularCards(ArrayList<Button> carButtons) {
        for (int i = 0; i < carButtons.size(); i++) {
            Button b = carButtons.get(i);
            int index = i;
            b.setOnAction(new EventHandler<ActionEvent> () {
                @Override
                public void handle(ActionEvent e) {
                    if (ifImPlayer()) {
                        trick.play(trick.getCurrentPlayer().cards.get(index));
                        p0play.setImage(assignImage(trick.getCurrentPlayer().cards.get(index)));
                        b.setVisible(true);
                        client.setTrick(trick);
                        try {
                            client.sendTrick();
                        } catch (Exception error) {
                            System.out.println(error);
                        }
                    }
                }
            });
        }
    }

    public void updateTable() {
        try {
            p1play.setImage(assignImage(trick.table.get(0)));
            p2play.setImage(assignImage(trick.table.get(1)));
            p3play.setImage(assignImage(trick.table.get(2)));
            p0play.setImage(assignImage(trick.table.get(3)));
        } catch (Exception error) {

        }
    }

    public void refreshHand() {
        p0c0.setImage(assignImage(trick.currentPlayer.cards.get(0)));
        p0c1.setImage(assignImage(trick.currentPlayer.cards.get(1)));
        p0c2.setImage(assignImage(trick.currentPlayer.cards.get(2)));
        p0c3.setImage(assignImage(trick.currentPlayer.cards.get(3)));
        p0c4.setImage(assignImage(trick.currentPlayer.cards.get(4)));
    }

    public void setTrump(String text) { //CANT CHANGE TEXT NEED TO USE IMAGES im thinking hiding htem?
        trump.setVisible(false);
    }

    public void updateTrump() {
        p0play.setImage(assignImage(trick.getTrump()));
    }


    @FXML
    public void initialize() {
        // loop that adds each card in each player's hand to their hbox

        
    }

    public void start() {
        ArrayList<Button> carButtons = new ArrayList<>();
        carButtons.add(b0);
        carButtons.add(b1);
        carButtons.add(b2);
        carButtons.add(b3);
        carButtons.add(b4);
        try {
            this.client = new Client("localhost", 5000);
            client.join();
            b5.setDisable(true);
            
        } catch (Exception e) {
            System.out.println(e);
        }
        initButtons();
        try {
            gamePlay(carButtons);
        } catch (Exception error) {
            System.out.println(error);
        }
    }

    public void gamePlay(ArrayList<Button> cardButtons) throws IOException, ClassNotFoundException {
        this.trick = client.reciveTrick(); // trick to update face cards
        refreshHand();
        client.sendTrick();
        
        this.trick = client.reciveTrick();
        if(this.trick.getPhase().equals("selectTrump")) {
            b5.setDisable(false);
            p0play.setImage(assignImage(this.trick.getTopCard()));
            trick = client.reciveTrick(); //update trump 
        } else { //trick is recived from up portion
            System.out.println(trump.getText());
            setTrump(trick.getTrump().suit);
        }

        System.out.println("Im about to recive the update trump trick");

       this.trick = client.reciveTrick();
       updateTrump();
        if (this.trick.dealer.getId() == client.me.getId()) {
            System.out.println(trick.currentPlayer.getCards());
            dealerCards(cardButtons);
        }
        client.sendTrick();

        int turns = 0;
        while (turns < 5) {
            if(ifImPlayer()) {
                turns++;
            }
        }
    }

    public boolean ifImPlayer() {
        return this.trick.getCurrentPlayer().getId() == this.client.me.getId();
    }

    public boolean ifImDealer() {
        return this.trick.getDealer().getId() == this.client.me.getId();
    }
    
    @FXML
    private Button b0;

    @FXML
    private Button b1;

    @FXML
    private Button b2;

    @FXML
    private Button b3;

    @FXML
    private Button b4;

    @FXML
    private Button b5;

    @FXML
    private Button b6;

    @FXML
    private ImageView p0c0;

    @FXML
    private ImageView p0c1;

    @FXML
    private ImageView p0c2;

    @FXML
    private ImageView p0c3;

    @FXML
    private ImageView p0c4;

    @FXML
    private ImageView p0play; //current played card

    @FXML
    private ImageView p1c0;

    @FXML
    private ImageView p1c2;

    @FXML
    private ImageView p1c3;

    @FXML
    private ImageView p1c4;

    @FXML
    private ImageView p1play;

    @FXML
    private ImageView p2c0;

    @FXML
    private ImageView p2c1;

    @FXML
    private ImageView p2c2;

    @FXML
    private ImageView p2c3;

    @FXML
    private ImageView p2c4;

    @FXML
    private ImageView p2play;

    @FXML
    private ImageView p3c0;

    @FXML
    private ImageView p3c1;

    @FXML
    private ImageView p3c2;

    @FXML
    private ImageView p3c3;

    @FXML
    private ImageView p3c4;

    @FXML
    private ImageView p3play;

    @FXML
    private Label team0score;

    @FXML
    private Label team1score;

    @FXML
    public Label trump;

    @FXML
    void c0pressed(MouseEvent event) {
        System.out.println("card 0");
        p0c0.setVisible(false);
        Card exampleCard = new Card(9, "spades");
        p0play.setImage(assignImage(exampleCard));
    }

    @FXML
    void c1pressed(MouseEvent event) {
        System.out.println("card 1");
        p0c1.setVisible(false);
    }

    @FXML
    void c2pressed(MouseEvent event) {
        System.out.println("card 2");
        p0c2.setVisible(false);
    }

    @FXML
    void c3pressed(MouseEvent event) {
        System.out.println("card 3");
        p0c3.setVisible(false);
    }

    @FXML
    void c4pressed(MouseEvent event) {
        System.out.println("card 4");
        p0c4.setVisible(false);
    }

}
