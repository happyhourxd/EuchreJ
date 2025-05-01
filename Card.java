import java.io.Serializable;

public class Card implements Serializable {
    
    private static final long serialVersionUID = 03;
    final int number; // the number of the card 9 = 9, 10 = 10, J = 11, Q = 12, K = 13, A = 14
    final String suit; // hearts, spaces, clubs, diamands


    public Card(int number, String suit) { //just a card constructor
        this.number = number;
        this.suit = suit;
    }

    public Card(Card card) {
        this.number = card.number;
        this.suit = card.suit;
    }

    public String toString() {
        return number + suit;
    }

    public int getNumber() {
        return number;
    }

    public String getSuit() {
        return suit;
    }
}
