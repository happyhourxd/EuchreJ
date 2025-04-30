import java.util.ArrayList;
import java.io.Serializable;

public class Trick implements Serializable{

    public ArrayList<Player> players = new ArrayList<>();
    private static final long serialVersionUID = 01; // the serialVersionUID, should be updated each time player is
    public Deck deck;
    public int[] score = {0,0};
    public boolean normalTrump;
    public Card trump = null;
    public Player dealer;
    public Player currentPlayer;
    public int turn = 0;
    public ArrayList<Card> table;
    public String phase;

    public Trick(ArrayList<Player> players) {
        this.players = players;
        this.dealer = players.get(0);
        this.deck = new Deck();
        this.table = new ArrayList<>();

        for (Player p : players) {
            p.clearHand();
        }
    }

    public Trick(ArrayList<Player> players, Card trump, int[] score, Player lastDelear) {
        
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getPhase() {
        return this.phase;
    }

    public Trick deal() {
        this.phase = "deal";
        if (this.deck.getSize() < 24) {
            this.deck = new Deck();
            this.deck.shuffle();
        }
        deck.shuffle();
        while(deck.getSize() > 6) {
            players.get(0).giveCard(deck.drawCard());
            players.get(1).giveCard(deck.drawCard());
            players.get(2).giveCard(deck.drawCard());
            players.get(2).giveCard(deck.drawCard());
        }
        return this;
    }

    public Card getTrump() {
        return this.trump;
    }

    public void setTrump(Card trump) {
        this.trump = trump;
    }

    public void normalTrump(Card card) {
        this.trump = deck.getTop();
        for (Player p : players) {
            if (p.id == dealer.id) {
                p.play(card);
                p.giveCard(this.trump);
            }
        }
    }

    public Trick play(Card card) {
        for (Player p : players) {
            if (p.cards.contains(card)) {
                p.play(card);
            }
        }
        table.add(card);
        return this;
    }

    public int score() {
        return 0;
    }

    public void setDealer(int id) {
        for (Player p : players) {
            if (p.id == id)
                dealer = p;
        }
    }

    public Player getPlayer(int id) {
        for (Player p : players) 
            if (p.id == id)
                return p;
        return null;
    }

    public Player getDealer() {
        return dealer;
    }

    public Card getTopCard() {
        return deck.getTop();
    }
}
