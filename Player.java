import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {

    private static final long serialVersionUID = 21; // the serialVersionUID, should be updated each time player is
                                                     // updated, base 16 for funzies
    int id; // will be the id, most likely the exact time in utc of creation
    String name; // the player's name, a user input
    int score; // the current score of the team
    int team; // the team they belong to, either 0 or 1
    boolean dealer; // if player is dealer or not
    public ArrayList<Card> cards; // the list of the cards in the player's hand
    transient ObjectOutputStream out; // the player's output stream 
    transient ObjectInputStream in;

    public Player(int id, String name) { // player constructor
        this.id = id;
        this.name = name;
        this.dealer = false;
        this.cards = new ArrayList<>();
    }

    public Player(Player player) {
        this.id = player.id;
        this.name = player.name;
        this.dealer = player.dealer;
        this.cards = new ArrayList<>(player.cards);
        this.team = player.team;
    }


    public void setTeam(int team) { // Set the team
        this.team = team;//0 or 1 to correspond with game score array
    }

    public void giveCard(Card card) {
        this.cards.add(new Card(card));
    }

    public void setCards(ArrayList<Card> cards) { // Used by the server to set the player's hand, happens each round
        cards.clear(); // Clears the hand before the round
        this.cards = cards;
    }

    public Card play(Card playedCard) { // Finds and removes the card from the player's list when playing a card
        for (int i = 0; i < cards.size(); i++) {
            Card currCard = cards.get(i);
            if (currCard.suit.equals(playedCard.suit) && currCard.number == playedCard.number) {
                cards.remove(i);
                return currCard;
            }
        }
        return new Card(0, null); // Returns a null card if not found, shouldn't happen
    }

    public int getId() {
        return id;
    }

    public void clearHand() {
        setCards(new ArrayList<>());
    }

    public String getName() {
        return name;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public String toString() {
        return "Id = " + getId() + ", Name = " + getName();
    }

    public int getTeam() {
        return team;
    }

    public boolean getDealer() {
        return dealer;
    }
    public void setDealer(boolean dealer){
        this.dealer=dealer;
    }
    public void setOutputStream(ObjectOutputStream out) { // Set the player's output stream
        this.out = out;
    }

    public ObjectOutputStream getOutputStream() { // Get the player's output stream
        return out;
    }
    public void setInputStream(ObjectInputStream in){
        this.in = in;
    }
    public ObjectInputStream getInputStream() { // Get the player's output stream
        return in;
    }
}