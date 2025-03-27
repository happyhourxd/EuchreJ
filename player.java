import java.util.ArrayList;

public class player {

    int id;
    String name;
    int score;
    int team;
    ArrayList<card> cards;

    public player(int id, String name, int team) {
        this.id = id;
        this.name = name;
        this.team = team;
    }

    public void setCards(ArrayList<card> cards) {
        this.cards = cards;
    }

    public card play(card playedCard) {
        for (int i = 0; i < cards.size(); i++) {
            card currCard = cards.get(i);
            if (currCard.type == playedCard.type && currCard.number == playedCard.number) {
                cards.remove(i);
                return currCard;
            }
        }
        return new card(0,null);
    }
}