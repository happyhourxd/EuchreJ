import java.util.ArrayList;

public class player {

    int id; // will be the id, most likely the exact time in utc of creation
    String name; // the players name, a user input
    int score; // the current score of the team
    int team; // the team they belong to ether 1 or 2
    ArrayList<card> cards;  // the list of the cards in the players hand

    public player(int id, String name, int team) { // player constructor
        this.id = id;
        this.name = name;
        this.team = team;
    }

    public void setCards(ArrayList<card> cards) { // used by the server to set the players hand happens each round
        cards.clear(); //clears the hand before the round
        this.cards = cards;
    }

    public card play(card playedCard) { // finds and removes the card from the players list when playing a card
        for (int i = 0; i < cards.size(); i++) {
            card currCard = cards.get(i);
            if (currCard.suit == playedCard.suit && currCard.number == playedCard.number) {
                cards.remove(i);
                return currCard;
            }
        }
        System.out.println("oop");
        return new card(0,null); //returns null card if isnt found shouldnt happen
    }
}