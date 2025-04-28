import java.util.ArrayList;
import java.util.Collections;
import java.io.Serializable;

public class Deck implements Serializable{
    private static final long serialVersionUID = 00;
    private ArrayList<Card> deckCards;

    public Deck(){
        deckCards = new ArrayList<Card>();
        int[] cardNum = {9, 10, 11, 12, 13, 14}; // 9 = 9, 10 = 10, J = 11, Q = 12, K = 13, A = 14
        String[] cardSuit = {"hearts", "spades", "clubs", "diamonds"}; // card suit

        for (String suit : cardSuit) {
            for (int num : cardNum) {
                deckCards.add(new Card(num, suit)); // Assuming Card has a constructor Card(int number, String suit)
            }
        }
    }

    public String toString() {
        String returnValue = "";
        for (Card c : deckCards) 
            returnValue += c + ", ";
        return returnValue;
    }
    

    public void shuffle() {
        Collections.shuffle(deckCards); // shuffle the deck
    }
    
    public void cutDeck(){//cut deck 
        ArrayList<Card> firstHalfCards = new ArrayList<>(deckCards.subList(0, 11));
        ArrayList<Card> lastHalfCards = new ArrayList<>(deckCards.subList(11, deckCards.size()));
        deckCards.clear();
        deckCards.addAll(lastHalfCards);
        deckCards.addAll(firstHalfCards);
    }

    public Card drawCard(){
        Card topCard = deckCards.remove(0);
        deckCards.trimToSize();
        return topCard;
    }
    
    public Card getTop() {
        return deckCards.get(0);
    }

    public int getSize(){
        return deckCards.size();
    }
}
