import java.util.ArrayList;

public class Game {
    private ArrayList<Player> playerArray;
    private String trump; // The trump suit for the current round
    private Deck deck; // The deck of cards
    private int[] teamScores;
    private int winningScore;
    private Player dealer;
    private ArrayList<Player> dealingOrder;

    public Game(ArrayList<Player> playerArray, int winningScore) {
        this.playerArray = playerArray;
        this.deck = new Deck();
        this.teamScores = new int[] {0, 0};
        this.winningScore = winningScore;
    }

    public int startGame(Server server) {
        while (teamScores[0] < winningScore && teamScores[1] < winningScore) {
            deckSetup(server);
            dealOrder(server);
            dealCards(server);
            if(bidding(server)==-1){
                continue;
            } 
            setCardValue(trump);
            playRound(server);
        }
        return 0;
    }

    private void deckSetup(Server server) {
        if (deck.getSize() != 24) {
            deck = new Deck();
        }
        deck.shuffle();
    }
    private void dealOrder(Server server){
        for (Player player : playerArray) {
            if (player.getDealer()) {
                dealer = player;
                server.sendUpdateToClients(player.getName() + " is the dealer.");
                break;
            }
        }
        dealingOrder = new ArrayList<Player>();
        int playerIndex=playerArray.indexOf(dealer)+1;
        for(int i=0; i<3;i++){
            if(playerIndex>3){
                playerIndex=0;
            }
            dealingOrder.add(playerArray.get(playerIndex));
            playerIndex++;
        }
    }


    private void playRound(Server server) {
        for (int trickCount = 0; trickCount < 5; trickCount++) {
            playTrick(server);
        }
        dealingOrder.get(0).setDealer(true);
        dealer.setDealer(false);
    }

    private void playTrick(Server server) {
        
    }

    // Set the potential trump suit by drawing the top card from the deck
    private void setPotentialTrump(Server server) {
        trump = deck.drawCard().getSuit(); // Draw the top card and get its suit
        server.sendUpdateToClients("The potential trump suit is: " + trump);
    }

    // Determine the trump suit for the round
    public int bidding(Server server) {
        setPotentialTrump(server); // Set the initial potential trump suit
        int accepted = 0; // Flag to track if a player accepts the trump suit

        // First round: Ask each player if they accept the trump suit
        for (Player player : dealingOrder) {
            accepted = acceptTrump(player, server);
            if (accepted == 1) {
                return 0;
            }
        }
        // Second round: Ask each player to select a trump suit
        if (accepted == 0) {
            Player wantsToSet = null;
            for (Player player : dealingOrder) {
                if (askSetTrump(player, server) == 1) {
                    wantsToSet=player;
                    break;
                }
            }
            if (wantsToSet != null) {
                trump = askSuit(wantsToSet, server);
                return 0;
            }
        }
        return -1; // Default return value if no conditions are met
    }

    // Ask a player if they accept the current trump suit
    public int acceptTrump(Player player, Server server) {
        String promptMessage = player.getName() + ", do you accept the trump suit (" + trump + ")? (yes/no):";
        String response = server.promptPlayerForInput(player, promptMessage);

        if (response != null) {
            response = response.toLowerCase();
            if (response.equals("yes")) {
                server.sendUpdateToClients(player.getName() + " accepted the trump suit: " + trump);
                return 1; // Player accepts the trump suit
            } else if (response.equals("no")) {
                server.sendUpdateToClients(player.getName() + " declined the trump suit.");
                return 0; // Player declines the trump suit
            }
        }
        server.sendUpdateToClients("Invalid response or communication error.");
        return 0; // Default to declining if an error occurs
    }

    // Ask a player if they want to set the trump suit
    public int askSetTrump(Player player, Server server) {
        String promptMessage = player.getName() + ", do you want to set the trump suit? (yes/no):";
        String response = server.promptPlayerForInput(player, promptMessage);

        if (response != null) {
            response = response.toLowerCase();
            if (response.equals("yes")) {
                return 1; // Player wants to set the trump suit
            } else if (response.equals("no")) {
                server.sendUpdateToClients(player.getName() + " declined to set the trump suit.");
                return 0; // Player does not want to set the trump suit
            }
        }
        server.sendUpdateToClients("Invalid response or communication error.");
        return 0; // Default to declining if an error occurs
    }

    // Ask a player to choose a trump suit if no one accepts the initial trump suit
    public String askSuit(Player player, Server server) {
        String promptMessage = player.getName() + ", choose a suit (hearts, spades, clubs, diamonds):";
        String suit = server.promptPlayerForInput(player, promptMessage);

        if (suit != null) {
            suit = suit.toLowerCase();
            if (suit.equals("hearts") || suit.equals("spades") || suit.equals("clubs") || suit.equals("diamonds")) {
                server.sendUpdateToClients(player.getName() + " chose the suit: " + suit);
                return suit; // Return the chosen suit
            }
        }
        return "oop";
    }
    //DEAL CARD LOGIC
    // Deal cards to players in two rounds
    public void dealCards(Server server) {
        Card drawnCard;
        int players = 4; // Number of players
        ArrayList<ArrayList<Card>> playerCards = new ArrayList<>(); // List to hold each player's hand

        // Initialize an empty hand for each player
        for (int i = 0; i < players; i++) {
            playerCards.add(new ArrayList<>());
        }

        // First round of dealing
        for (int i = 0; i < players; i++) {
            if (i % 2 == 0) { // For even-indexed players
                for (int j = 0; j < 3; j++) { // Deal 3 cards
                    drawnCard = deck.drawCard();
                    playerCards.get(i).add(drawnCard);
                }
            } else { // For odd-indexed players
                for (int j = 0; j < 2; j++) { // Deal 2 cards
                    drawnCard = deck.drawCard();
                    playerCards.get(i).add(drawnCard);
                }
            }
        }

        // Second round of dealing
        for (int i = 0; i < players; i++) {
            if (i % 2 == 0) { // For even-indexed players
                for (int j = 0; j < 2; j++) { // Deal 2 cards
                    drawnCard = deck.drawCard();
                    playerCards.get(i).add(drawnCard);
                }
            } else { // For odd-indexed players
                for (int j = 0; j < 3; j++) { // Deal 3 cards
                    drawnCard = deck.drawCard();
                    playerCards.get(i).add(drawnCard);
                }
            }
        }

        // Assign the dealt cards to each player
        for (int i = 0; i < players; i++) {
            dealingOrder.get(i).setCards(playerCards.get(i)); // Set the player's hand
        }
        for (Player player : playerArray){
            server.sendPlayerUpdate(player, player.getCards().toString());
        }
    }

    //Set Card Values given trump card
    public void setCardValue(String trumpSuit) {
        // Iterate through each player
        for (Player player : playerArray) {
            // Iterate through each card in the player's hand
            for (Card card : player.getCards()) {
                // Check if the card's suit matches the trump suit
                if (card.getSuit() == trumpSuit) {
                    if (card.getValue() == 11) { // If the card is a Jack (value 11)
                        card.setValue(card.getValue() * 3); // Triple the value of the Jack
                    } else { // For other cards in the trump suit
                        card.setValue(card.getValue() * 2); // Double the value
                    }
                }
                // Check if the card is the Left Bower (Jack of the same color as the trump suit)
                else if (card.getValue() == 11 && card.getSuit() == "diamonds" && trumpSuit == "hearts") {
                    card.setValue((card.getValue() * 3) - 1); // Adjust value for Left Bower
                } else if (card.getValue() == 11 && card.getSuit() == "hearts" && trumpSuit == "diamonds") {
                    card.setValue((card.getValue() * 3) - 1); // Adjust value for Left Bower
                } else if (card.getValue() == 11 && card.getSuit() == "spades" && trumpSuit == "clubs") {
                    card.setValue((card.getValue() * 3) - 1); // Adjust value for Left Bower
                } else if (card.getValue() == 11 && card.getSuit() == "clubs" && trumpSuit == "spades") {
                    card.setValue((card.getValue() * 3) - 1); // Adjust value for Left Bower
                }
            }
        }
    }
}