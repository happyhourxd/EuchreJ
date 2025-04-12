import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> deckCards;

    public void initializeDeck() {
        deckCards = new ArrayList<Card>();
        int[] cardNum = {9, 10, 11, 12, 13, 14}; // 9 = 9, 10 = 10, J = 11, Q = 12, K = 13, A = 14
        String[] cardSuit = {"hearts", "spades", "clubs", "diamonds"}; // card suit

        for (String suit : cardSuit) {
            for (int num : cardNum) {
                deckCards.add(new Card(num, suit)); // Assuming Card has a constructor Card(int number, String suit)
            }
        }
        shuffle();
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
    /* idk... deal way euchre works probably much better way to do it..
    public void dealDeck(){
        int players = 4;
        int cardsPer = 5;
            for(int i=0; i<players; i++){
                if(playerCards[i].size()==0){
                    if(i%2==0){
                        for(int j=0; j<3; j++){
                        playerCards[i].add(deckCards.get(0));
                        deckCards.remove(0);
                        deckCards.trimToSize;
                        }
                    }
                    else{
                        for(int j=0; j<2; j++){
                        playerCards[i].add(deckCards.get(0));
                        deckCards.remove(0);
                        }
                    }
                }
                else{
                    if(i%2==0){
                        for(int j=0; j<2; j++){
                        playerCards[i].add(deckCards.get(0));
                        deckCards.remove(0);
                        deckCards.trimToSize;
                        }
                    }
                    else{
                        for(int j=0; j<3; j++){
                        playerCards[i].add(deckCards.get(0));
                        deckCards.remove(0);
                        }
                    }
                }
            }
        }
    */
}
