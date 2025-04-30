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
    boolean trash = false;

    Image assignImage(Card card) {
        return new Image("/images/" + card.number + card.suit + ".png");
    }

    public void initButtons() {
        ArrayList<Button> cards = new ArrayList<>();
        cards.add(b0);
        cards.add(b1);
        cards.add(b2);
        cards.add(b3);
        cards.add(b4);

        b5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                trick.normalTrump = true;
                client.setTrick(trick);
                b5.setDisable(true);
                try {
                    client.sendTrick();
                    trick = client.reciveTrick();
                } catch (Exception error) {
                    System.out.println(error);
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

        b0.setOnAction(new EventHandler<ActionEvent>() { 
            @Override
            public void handle(ActionEvent e) {

            }
        });

        
    }


    @FXML
    public void initialize() {
        // loop that adds each card in each player's hand to their hbox

        
    }

    public void start() {
        try {
            this.client = new Client("localhost", 5000);
            client.join();
            b5.setDisable(true);
            
        } catch (Exception e) {
            System.out.println(e);
        }
        initButtons();
        try {
            gamePlay();
        } catch (Exception error) {
            System.out.println(error);
        }
    }

    public void gamePlay() throws IOException, ClassNotFoundException {
        this.trick = client.reciveTrick();
        if(this.trick.getPhase().equals("selectTrump")) {
            p0play.setImage(assignImage(this.trick.getTopCard()));
        }
        if (ifImPlayer() && (this.trick.getTrump() == null)) {
            b5.setDisable(false);
        }
        
        //if dealer select cards
        if (ifImDealer()) {
            trash = true;
        }

        this.trick = client.reciveTrick();


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
    private Label trump;

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
