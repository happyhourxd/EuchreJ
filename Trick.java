import java.util.ArrayList;
import java.io.Serializable;

public class Trick implements Serializable{

    public ArrayList<Player> players = new ArrayList<>();
    private static final long serialVersionUID = 20; // the serialVersionUID, should be updated each time player is
    public Deck deck;
    public int[] wins = {0,0};
    public int[] score = {0,0};
    public boolean weridTrump = false;
    public boolean doneWifTrump = false;
    public Card trump = null;
    public Player dealer;
    public Player currentPlayer;
    public int currPlayerInt;
    public int turn;
    public ArrayList<Card> table;
    public String phase;
    public String leadingSuit;
    public int[] cardsLeft = {5,5,5,5};

    public Trick(ArrayList<Player> players) {
        this.players = new ArrayList<>();
        for (Player p : players) {
            this.players.add(new Player(p));
        }
        this.deck = new Deck();
        this.table = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            this.table.add(new Card(0,null));
        }

        for (Player p : players) {
            p.clearHand();
        }
    }

    public void setCurrentPlayer(Player player) {
        for (int i = 0; i < players.size(); i++ )
            if (this.players.get(i).id == player.id)
                this.currentPlayer = this.players.get(i);
                return;
    }

    public void setCurrentPlayerByID(int id) {
        for (Player p : this.players)
            if (p.id == id) {
                this.currentPlayer = p;
                return;
            }
    }

    public void clearTable() {
        this.table = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            this.table.add(new Card(0,null));
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void scp(Player player) {
        this.currentPlayer = player;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getPhase() {
        return this.phase;
    }

    public Trick deal() {
        if (this.deck.getSize() < 24) {
            this.deck = new Deck();
        }
        this.deck.shuffle();
        while(deck.getSize() > 6) {
            players.get(0).giveCard(this.deck.drawCard());
            players.get(1).giveCard(this.deck.drawCard());
            players.get(2).giveCard(this.deck.drawCard());
            players.get(3).giveCard(this.deck.drawCard());
        }
        return this;
    }

    public Card getTrump() {
        return this.trump;
    }

    public void setTrump(Card trump) {
        this.trump = trump;
    }

    public void trump() {
        this.doneWifTrump = true;
        this.trump = new Card(deck.getTop());
    }

    public void dealerTrade(Card card) {
        for (Player p : players) {
            if (p.id == dealer.id) {
                p.cards.remove(card);
                p.giveCard(new Card(this.trump));
            }
        }
    }

    public Trick play(Card card) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).cards.contains(card)) {
                players.get(i).play(card);
                table.set(i,card);
            }
        }
        
        return this;
    }

    public int findHighestTeam() {
        Card highCard = findHihgestCard();
        int pos = 0;
        for (int i = 0; i < 4; i++) {
            if(table.get(i) == highCard) {
                pos = i;
            }
        }
        return (pos%2);
    }

    public void calcWins() {
        try {
            if (findHighestTeam() == 1) {
                wins[0]++;
            } else {
                wins[1]++;
            }
        } catch (Exception error) {

        }
        
    }

    public void calcScore() {
        if (wins[0] == 5) {
            score[0] += 4;
        
        } else if (wins[0] == 3) {
            score[0] += 2;
        } else if  (wins[1] == 5) {
            score[1] += 4;   
        } else {
            score[1] += 2;
        }
    }

    public void resetWins(){
        wins[0] = 0;
        wins[1] = 0;
    }

    public Card findHihgestCard() {
        int amtnull = 0;
        for (Card c : this.table) {
            if (c.suit == null)
                amtnull++;
        }
        if (amtnull == 3) {
            for (Card c : this.table) {
                if (c.suit != null) {
                    return c;
                }
            }
        }
        int pos = 0;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).id == dealer.id)
                pos = i;
        }
        pos = (pos+1)%4;

        Card highest = table.get(0);
        Card first = table.get(pos);
        
        
        for (Card c : table) {
            if ((highest.suit != null) && c.suit != null) {
                if (highest.suit.equals(trump.suit)) {
                    if(c.suit.equals(trump.suit) || isLeftBower(trump, c)) {
                        if (highest.number != 11) {
                            if(c.number > highest.number) {
                                highest = c;
                            }
                        }
                    } 
                } else if (c.suit.equals(trump.suit) || isLeftBower(trump, c)) {
                    highest = c;
                } else {
                    if (highest.suit.equals(first.suit) && (c.suit.equals(first.suit))) {
                        if (highest.number < c.number) {
                            highest = c;
                        }
                    } else {
                        if (c.suit.equals(first.suit)) {
                            if (c.number > first.number) {
                                highest = c;
                            }
                        }
                    }
                }
            } else if (c.suit != null) {
                highest = c;
            }
        }
        return highest;
    }

    public boolean isLeftBower(Card card1, Card card2) {
        if (card2.number == 11) {
            if (card1.suit.equals("spades") && card2.suit.equals("clubs"))
            return true;
        if (card1.suit.equals("clubs") && card2.suit.equals("spades"))
            return true;
        if (card1.suit.equals("hearts") && card2.suit.equals("diamonds"))
            return true;
        if (card1.suit.equals("diamonds") && card2.suit.equals("hears"))
            return true;
        }
        return false;
    }

    public void setDealer(int id) {
        for (Player p : players) {
            if (p.id == id)
                dealer = p;
        }
    }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public Player getDealer() {
        return dealer;
    }

    public Card getTopCard() {
        return deck.getTop();
    }
}
