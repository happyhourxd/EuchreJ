import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable{

    private static final long serialVersionUID = 1L; // the serialVersionUID, should be updated each time player is updated, base 16 for funzies
    int id; // will be the id, most likely the exact time in utc of creation
    String name; // the players name, a user input
    int score; // the current score of the team
    int team; // the team they belong to ether 1 or 2
    boolean dealer; // if player is dealer or not
    ArrayList<Card> cards;  // the list of the cards in the players hand

    public Player(int id, String name) { // player constructor
        this.id = id;
        this.name = name;
    }

    public void setTeam(int team) { //set the team
        this.team = team;
    }

    public void setCards(ArrayList<Card> cards) { // used by the server to set the players hand happens each round
        cards.clear(); //clears the hand before the round
        this.cards = cards;
    }

    public Card play(Card playedCard) { // finds and removes the card from the players list when playing a card
        for (int i = 0; i < cards.size(); i++) {
            Card currCard = cards.get(i);
            if (currCard.suit == playedCard.suit && currCard.number == playedCard.number) {
                cards.remove(i);
                return currCard;
            }
        }
        System.out.println("oop");
        return new Card(0,null); //returns null card if isnt found shouldnt happen
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "Id = " + getId() + "Name = " + getName();
    }
}