public class card {
    
    int number; // the number of the card 9 = 9, 10 = 10, J = 11, Q = 12, K = 13, A = 14
    String suit; // hearts, spaces, clubs, diamands

    public card(int number, String suit){ //just a card constructor
        this.number = number;
        this.suit = suit;
    }
}
