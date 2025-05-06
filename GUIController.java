import java.io.IOException;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;

public class GUIController {

    Trick trick;
    public Client client;
    public int wins[] = {0,0};
    Boolean weirdTrump = false;
    ArrayList<Button> cardButtons = new ArrayList<>();
    int cardsLeft = 5;
    int p1CardsLeft = 5;
    int p2CardsLeft = 5;
    int p3CardsLeft = 5;
    ImageView[] p1cards;
    ImageView[] p2cards;
    ImageView[] p3cards;
    ArrayList<Button> disabled = new ArrayList<>();


    Image assignImage(Card card) {
        return new Image("/images/" + card.number + card.suit + ".png");
    }

    public void initButtons() {
        System.out.println("buttons initialized!");
        b6.setDisable(true);
        b5.setDisable(true);

        b5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                trick.trump();
                b5.setDisable(true);
                try {
                    client.sendTrick();
                    
                } catch (Exception error) {
                    
                }
            }
        });

        b6.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                b5.setDisable(true);
                b6.setVisible(false);
                disableCards();
                try {
                    client.sendTrick();
                } catch (Exception error) {
                    System.out.println(error);
                }
            }
        });
    }

    public void dealerCards(ArrayList<Button> carButtons) {
        enableCards(carButtons, true);
        for(int i = 0; i < carButtons.size(); i++) {
            Button b = carButtons.get(i);
            final int index = i;
            b.setOnAction(new EventHandler<ActionEvent>() { 
                @Override
                public void handle(ActionEvent e) {
                    
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

    public void regularCards(ArrayList<Button> cardButtons) {
        for (int i = 0; i < cardButtons.size(); i++) {
            Button b = cardButtons.get(i);
            int index = i;
            b.setOnAction(new EventHandler<ActionEvent> () {
                @Override
                public void handle(ActionEvent e) {
                    p0play.setImage(assignImage(trick.getCurrentPlayer().cards.get(index)));
                    trick.play(trick.getCurrentPlayer().cards.get(index));
                    b.setVisible(false);
                    cardsLeft--;
                    updateCards();
                    client.setTrick(trick);
                    try {
                        client.sendTrick();
                    } catch (Exception error) {
                        System.out.println(error);
                    }
                }
            });
        }
    }

    public void weirdTrump(ArrayList<Button> cardButtons) {
        weirdTrump = true;
        this.trick.weirdTrump = true;
        enableCards(cardButtons, true);
        b6.setVisible(true);
        b6.setDisable(false);
        for (int i = 0; i < cardButtons.size(); i++) {
            Button b = cardButtons.get(i);
            int index = i;
            b.setOnAction(new EventHandler<ActionEvent> () {
                @Override
                public void handle(ActionEvent e) {
                    disableCards();
                    trick.setTrump(trick.currentPlayer.cards.get(index));
                    trick.doneWifTrump = true;
                    client.setTrick(trick);
                    try {
                        client.sendTrick();
                    } catch (Exception error) {
                        System.out.println(error);
                    }
                }
            });
        }
    }

    public void updateCards() {
        int pos = 0;
        for (int i = 0; i < trick.players.size(); i++) {
            if (trick.players.get(i).id == client.me.id) {
                pos = i;
            }
        }
        this.trick.cardsLeft[pos]--;
    }

    public void updateTable() {
        int pos = 0;
        for (int i = 0; i < trick.players.size(); i++) {
            if (trick.players.get(i).id == client.me.id) {
                pos = i;
            }
        }
        try {
            p1play.setImage(assignImage(trick.table.get((pos+1)%4)));
        } catch (Exception error) {
            
        }try {
            p2play.setImage(assignImage(trick.table.get((pos+2)%4)));
        } catch (Exception error) {
        }try {
            p3play.setImage(assignImage(trick.table.get((pos+3)%4)));
        } catch (Exception error) {
        }try {
            p0play.setImage(assignImage(trick.table.get(pos)));
        } catch (Exception error) {

        }
        p1CardsLeft = this.trick.cardsLeft[(pos+1)%4];
        p2CardsLeft = this.trick.cardsLeft[(pos+2)%4];
        p3CardsLeft = this.trick.cardsLeft[(pos+3)%4];
        for (int i = 4; i > (p1CardsLeft - 1); i--) {
            this.p1cards[i].setVisible(false);
        }
        for (int i = 4; i > (p2CardsLeft - 1); i--) {
            this.p2cards[i].setVisible(false);
        }
        for (int i = 4; i > (p3CardsLeft - 1); i--) {
            this.p3cards[i].setVisible(false);
        }
    }

    public void clearTable(ArrayList<Button> cardButtons) {
        p0play.setImage(new Image("/images/back.png"));
        p1play.setImage(new Image("/images/back.png"));
        p2play.setImage(new Image("/images/back.png"));
        p3play.setImage(new Image("/images/back.png"));
    }

    public void disableCards(ArrayList<Button> cardButtons) {
        for (int i = 0; i < this.cardButtons.size(); i++) {
            Button b = this.cardButtons.get(i);
            b.setDisable(true);
        }
    }

    public void disableCards() {
        for (int i = 0; i < this.cardButtons.size(); i++) {
            Button b = this.cardButtons.get(i);
            b.setDisable(true);
        }
    }


    public void enableCards(ArrayList<Button> cardButtons, boolean always) {
        int pos = 0;
        for (int i = 0; i < trick.players.size(); i++) {
            if (trick.players.get(i).id == client.me.id) {
                pos = i;
            }
        }
        int amt = 5;
        if (always) { 
            for (int i = 0; i < cardButtons.size(); i++) {
                Button b = cardButtons.get(i);
                b.setDisable(false);
            }
        } else {
            for (int i = 0; i < cardButtons.size(); i++) {
                Button b = cardButtons.get(i);
                if (canPlay(i, pos)) {
                    b.setDisable(false);
                } else {
                    disabled.add(b);
                    amt--;
                }
            }
            if (amt == 0) {
                for (int i = 0; i< cardButtons.size(); i++) {
                    Button b = cardButtons.get(i);
                    b.setDisable(false);
                }
        }
        }
        
    }

    public boolean isEmpty() {
        int amt = 0;
        for (Card c : this.trick.table) {
            if (c.suit == null)
                amt++;
        }
        if (amt == 4)
            return true;
        return false;
    }

    public boolean canPlay(int i, int me) {
        if (isEmpty())
            return true;
        Card card = this.trick.getCurrentPlayer().cards.get(i);
        if(card.suit == this.trick.trump.suit)
            return true;
        else if (this.trick.leadingSuit == card.suit)
            return true;
        else if (this.trick.table.get((me+2)%4).suit != null) 
        /*
         * Depending on the version of euchre this rule is contriversal
         * -- house rules --
         * 
         * if you are currently winning trick you do NOT have to follow suit
         * 
         * -- some offical rules --
         * 
         * some rules say YOU HAVE to follow suit, this isn't as fun
         */
            if (this.trick.table.get((me+2)%4) == this.trick.findHihgestCard())
                return true;
        return false;
    }

    public void reset(ArrayList<Button> cards) {
        Image image = new Image("/images/back.png");
        clearTable(cards);
        potTrump.setImage(image);
        b6.setVisible(true);
        this.cardsLeft = 5;
        for (int i = 0; i < cards.size(); i++) {
            Button b = cards.get(i);
            b.setVisible(true);
        }
        for (int i = 0; i < 5; i++) {
            p1cards[i].setVisible(true);
            p2cards[i].setVisible(true);
            p3cards[i].setVisible(true);
        }
        p0c0.setImage(image);
        p0c1.setImage(image);
        p0c2.setImage(image);
        p0c3.setImage(image);
        p0c4.setImage(image);
    }

    public void refreshHand() {
        p0c0.setImage(assignImage(trick.currentPlayer.cards.get(0)));
        p0c1.setImage(assignImage(trick.currentPlayer.cards.get(1)));
        p0c2.setImage(assignImage(trick.currentPlayer.cards.get(2)));
        p0c3.setImage(assignImage(trick.currentPlayer.cards.get(3)));
        p0c4.setImage(assignImage(trick.currentPlayer.cards.get(4)));
    }

    public void setTrump(Card card) { //CANT CHANGE TEXT NEED TO USE IMAGES
        potTrump.setImage(assignImage(card));
    }

    public void updateTrump() {
        potTrump.setImage(assignImage(trick.getTrump()));
    }

    public void updateScore() {
        t0s0.setVisible(false);
        t0s2.setVisible(false);
        t0s4.setVisible(false);
        t0s6.setVisible(false);
        t0s8.setVisible(false);
        t0s10.setVisible(false);
        t1s0.setVisible(false);
        t1s2.setVisible(false);
        t1s4.setVisible(false);
        t1s6.setVisible(false);
        t1s8.setVisible(false);
        t1s10.setVisible(false);
        int me = 0;
        for (int i = 0; i < this.trick.players.size(); i++) {
            if (client.me.id == this.trick.players.get(i).id) {
                me = i;
            }
        }
        if ((me%2) == 0 ) {
            if (this.trick.score[0] == 0)
                t0s0.setVisible(true);
            else if (this.trick.score[0] == 2)
                t0s2.setVisible(true);
            else if (this.trick.score[0] == 4)
                t0s4.setVisible(true);
            else if (this.trick.score[0] == 6)
                t0s6.setVisible(true);
            else if (this.trick.score[0] == 8)
                t0s8.setVisible(true);
            else
                t0s10.setVisible(true);
            if (this.trick.score[1] == 0)
                t1s0.setVisible(true);
            else if (this.trick.score[1] == 2)
                t1s2.setVisible(true);
            else if (this.trick.score[1] == 4)
                t1s4.setVisible(true);
            else if (this.trick.score[1] == 6)
                t1s6.setVisible(true);
            else if (this.trick.score[1] == 8)
                t1s8.setVisible(true);
            else
                t1s10.setVisible(true);
        } else {
            if (this.trick.score[1] == 0)
                t0s0.setVisible(true);
            else if (this.trick.score[1] == 2)
                t0s2.setVisible(true);
            else if (this.trick.score[1] == 4)
                t0s4.setVisible(true);
            else if (this.trick.score[1] == 6)
                t0s6.setVisible(true);
            else if (this.trick.score[1] == 8)
                t0s8.setVisible(true);
            else
                t0s10.setVisible(true);
            if (this.trick.score[0] == 0)
                t1s0.setVisible(true);
            else if (this.trick.score[0] == 2)
                t1s2.setVisible(true);
            else if (this.trick.score[0] == 4)
                t1s4.setVisible(true);
            else if (this.trick.score[0] == 6)
                t1s6.setVisible(true);
            else if (this.trick.score[0] == 8)
                t1s8.setVisible(true);
            else
                t1s10.setVisible(true);
        }
        
    }


    @FXML
    public void initialize() {
        // loop that adds each card in each player's hand to their hbox
    }

    public void start(int port, String addr) {
        this.p1cards = new ImageView[] {p1c0,p1c1,p1c2,p1c3,p1c4};
        this.p2cards = new ImageView[] {p2c0,p2c1,p2c2,p2c3,p2c4};
        this.p3cards = new ImageView[] {p3c0,p3c1,p3c2,p3c3,p3c4};
        //t0s0.setVisible(false);
        t0s2.setVisible(false);
        t0s4.setVisible(false);
        t0s6.setVisible(false);
        t0s8.setVisible(false);
        t0s10.setVisible(false);
        //t1s0.setVisible(false);
        t1s2.setVisible(false);
        t1s4.setVisible(false);
        t1s6.setVisible(false);
        t1s8.setVisible(false);
        t1s10.setVisible(false);
        this.cardButtons = new ArrayList<>(Arrays.asList(b0,b1,b2,b3,b4));
        try {
            this.client = new Client(addr, port);
            client.join();
            b5.setDisable(true);
            
        } catch (Exception e) {
            System.out.println(e);
        }
        initButtons();
        
        try {
            while (wins[0] < 10 || wins[1] < 10) {
                gamePlay(cardButtons);
                reset(cardButtons);
            }
            
        } catch (Exception error) {
            System.out.println(error);
        }
    }

    public void gamePlay(ArrayList<Button> cardButtons) throws IOException, ClassNotFoundException {
        /*
         * for the love of god please dont change any of the sendTrick or receive tricks, its a delecate balance
         * this is the main gameplay loop for the client side
         */
        this.trick = client.receiveTrick(); // trick to update face cards
        updateScore();
        refreshHand();
        client.sendTrick();
        disableCards(cardButtons);

        this.trick = client.receiveTrick(); //select trump method

        if(this.trick.getPhase().equals("selectTrump")) {
            b6.setDisable(false);
            b5.setDisable(false);
            setTrump(this.trick.getTopCard());
            trick = client.receiveTrick(); //update trump trump
        
            if(this.trick.getPhase().equals("weirdTrump")) {
                weirdTrump(cardButtons);
                trick = client.receiveTrick(); //post trump trick (update trump)
            }
        }
        
        weirdTrump = this.trick.weirdTrump;

        if (((this.trick.dealer.getId() == client.me.getId())) && !weirdTrump) {
            setTrump(this.trick.getTopCard());
            enableCards(cardButtons, true);
            System.out.println("im dealer");
            dealerCards(cardButtons);
        }
        
        this.trick = client.receiveTrick();
        
        b6.setDisable(true);
        updateTrump();
        
        for (int i = 0; i < 5; i++) {
            loop(cardButtons);
            clearTable(cardButtons);
        }
    }

    public void loop(ArrayList<Button> cardButtons) throws IOException, ClassNotFoundException{
        
        disabled = new ArrayList<>();
        updateScore();
        this.trick = null;
        this.client.trick = null;

        int turns = 0;
        while (turns < 4) {
            disableCards(cardButtons);

            this.trick = client.receiveTrick();
            updateScore();
            wins = this.trick.score;
            updateTable();
            if(ifImPlayer()) {
                enableCards(cardButtons, false);
                regularCards(cardButtons);
                client.receiveTrick();
            } else 
                client.sendTrick();
            turns++;
        }
        System.out.println("LOOP OVER");
    }

    public boolean ifImPlayer() {
        return this.trick.currentPlayer.id == this.client.me.id;
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
    private ImageView p1c1;

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
    public ImageView potTrump;

    @FXML
    private Label t0s0;

    @FXML
    private Label t0s10;

    @FXML
    private Label t0s2;

    @FXML
    private Label t0s4;

    @FXML
    private Label t0s6;

    @FXML
    private Label t0s8;

    @FXML
    private Label t1s0;

    @FXML
    private Label t1s2;

    @FXML
    private Label t1s4;

    @FXML
    private Label t1s6;

    @FXML
    private Label t1s8;

    @FXML
    private Label t1s10;

    @FXML
    public Label trump;

    @FXML
    void c0pressed(MouseEvent event) {
        
    }

    @FXML
    void c1pressed(MouseEvent event) {
        
    }

    @FXML
    void c2pressed(MouseEvent event) {
    
    }

    @FXML
    void c3pressed(MouseEvent event) {
        
    }

    @FXML
    void c4pressed(MouseEvent event) {
        
    }

    @FXML
    void nuhUhPressed(MouseEvent event) {

    }

    @FXML
    void potTrumpPressed(MouseEvent event) {

    }

    @FXML
    void score1(MouseEvent event) {

    }

    @FXML
    void score2(MouseEvent event) {

    }

    @FXML
    void p1c0pressed(MouseEvent event) {

    }

    @FXML
    void p1c1pressed(MouseEvent event) {

    }

    @FXML
    void p1c2pressed(MouseEvent event) {

    }

    @FXML
    void p1c3pressed(MouseEvent event) {

    }

    @FXML
    void p1c4pressed(MouseEvent event) {

    }

    @FXML
    void p2c0pressed(MouseEvent event) {

    }

    @FXML
    void p2c1pressed(MouseEvent event) {

    }

    @FXML
    void p2c2pressed(MouseEvent event) {

    }

    @FXML
    void p2c3pressed(MouseEvent event) {

    }

    @FXML
    void p2c4pressed(MouseEvent event) {

    }

    @FXML
    void p3c0pressed(MouseEvent event) {

    }

    @FXML
    void p3c1pressed(MouseEvent event) {

    }

    @FXML
    void p3c2pressed(MouseEvent event) {

    }

    @FXML
    void p3c3pressed(MouseEvent event) {

    }

    @FXML
    void p3c4pressed(MouseEvent event) {

    }
}
