import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

public class GUIController {

    Image assignImage(Card card) {
        return new Image("/images/" + card.number + card.suit + ".png");
    }

    @FXML
    public void initialize() {
        // loop that adds each card in each player's hand to their hbox
    }

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
    private ImageView p0play;

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
