public class Card {
    
    int number; // the number of the card 9 = 9, 10 = 10, J = 11, Q = 12, K = 13, A = 14
    String suit; // hearts, spaces, clubs, diamands
    int value;
    public Card(int number, String suit){ //just a card constructor
        this.number = number;
        this.suit = suit;
        this.value = number;
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
    public int getValue() {
        return value;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public void setSuit(String suit) {
        this.suit = suit;
    }
    public void setValue(int value) {
        this.value = value;
    }
}
