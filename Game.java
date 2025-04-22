import java.util.ArrayList;

public class Game {
    private ArrayList<Player> playerArray; // List of players in the game
    private String trump; // The trump suit for the current round
    private Deck deck; // The deck of cards
    private int[] teamScores; // Scores for the two teams
    private int winningScore; // The score required to win the game
    private Player dealer; // The current dealer
    private ArrayList<Player> dealingOrder; // The order in which players are dealt cards
    private ArrayList<Player> playOrder; // The order in which players play during a trick

    public Game(ArrayList<Player> playerArray, int winningScore) {
        this.playerArray = playerArray;
        this.deck = new Deck();
        this.teamScores = new int[] {0, 0};
        this.winningScore = winningScore;
    }

    // Main game loop
    public int startGame(Server server) {
        while (teamScores[0] < winningScore && teamScores[1] < winningScore) {
            deckSetup(server); // Prepare the deck
            dealOrder(server); // Determine the dealing order
            dealCards(server); // Deal cards to players
            if (bidding(server) == -1) { // Handle bidding for the trump suit
                continue; // Restart the round if no trump suit is selected
            }
            setCardValue(trump); // Set card values based on the trump suit
            playRound(server); // Play a round of tricks
        }
        return 0; // Game ends when a team reaches the winning score
    }

    // Prepare the deck for the round
    private void deckSetup(Server server) {
        if (deck.getSize() != 24) {
            deck = new Deck(); // Create a new deck if the current deck is not complete
        }
        deck.shuffle(); // Shuffle the deck
    }

    // Determine the order in which players are dealt cards
    private void dealOrder(Server server) {
        for (Player player : playerArray) {
            if (player.getDealer()) {
                dealer = player; // Set the current dealer
                server.sendUpdateToClients(player.getName() + " is the dealer.");
                break;
            }
        }
        dealingOrder = new ArrayList<>();
        int playerIndex = playerArray.indexOf(dealer) + 1;
        for (int i = 0; i < 3; i++) {
            if (playerIndex > 3) {
                playerIndex = 0; // Wrap around to the first player
            }
            dealingOrder.add(playerArray.get(playerIndex));
            playerIndex++;
        }
    }

    // Update the play order based on the winner of the previous trick
    private void playingOrder(Player winner) {
        playOrder = new ArrayList<>();
        int playerIndex = playerArray.indexOf(winner);
        for (int i = 0; i < 3; i++) {
            if (playerIndex > 3) {
                playerIndex = 0; // Wrap around to the first player
            }
            playOrder.add(playerArray.get(playerIndex));
            playerIndex++;
        }
    }

    // Play a round consisting of multiple tricks
    private void playRound(Server server) {
        for (int trickCount = 0; trickCount < 5; trickCount++) {
            if (trickCount == 0) {
                playOrder = dealingOrder; // Set the initial play order to the dealing order
            }
            playTrick(server); // Play a single trick
        }
        dealingOrder.get(0).setDealer(true); // Update the dealer for the next round
        dealer.setDealer(false);
    }

    // Play a single trick
    private void playTrick(Server server) {
        ArrayList<Card> trickCards = new ArrayList<>(); // Cards played in the current trick
        String leadSuit = null; // The suit of the first card played
        Card winningCard = null; // The winning card of the trick
        Player winner = null; // The player who wins the trick

        // Each player plays a card
        for (Player player : playOrder) {
            Card playedCard = player.getCards().get(0); // Placeholder: Replace with actual card selection logic
            trickCards.add(playedCard);
            player.play(playedCard);

            if (leadSuit == null) {
                leadSuit = playedCard.getSuit(); // Set the lead suit
            }
            if (winningCard == null || 
                (playedCard.getSuit().equals(trump) && !winningCard.getSuit().equals(trump)) || //Played card trump and winning card not trump
                (playedCard.getSuit().equals(leadSuit) && !winningCard.getSuit().equals(trump)) || //Played Card Lead Suit and winning card not trump
                (playedCard.getSuit().equals(winningCard.getSuit()) && playedCard.getValue() > winningCard.getValue())|| //Played Card of same suit as winning suit and is greater
                (playedCard.getValue()==32&&playedCard.getValue()>winningCard.getValue())) { //Left Bower Played
                // Update the winning card and the winner
                winningCard = playedCard;
                winner = player; // Update the winner of the trick
            }

            // Additional logic to handle edge cases
            if (winningCard.getSuit().equals(trump) && playedCard.getSuit().equals(trump)) {
                // If both cards are trump cards, compare their values
                if (playedCard.getValue() > winningCard.getValue()) {
                    winningCard = playedCard;
                    winner = player;
                }
            } else if (winningCard.getSuit().equals(leadSuit) && playedCard.getSuit().equals(leadSuit)) {
                // If both cards are lead suit cards, compare their values
                if (playedCard.getValue() > winningCard.getValue()) {
                    winningCard = playedCard;
                    winner = player;
                }
            }
        }
        server.sendUpdateToClients(winner.getName()+" won the trick");
        //TODO add point scoring
        // Update the play order based on the winner
        playingOrder(winner);
    }

    //TODO Validate if a card is valid to play
    /*
    private boolean validCard(Card playerCard, String leadSuit, String trump) {

        return true;
    }
    */

    // Set the potential trump suit by drawing the top card from the deck
    private void setPotentialTrump(Server server) {
        trump = deck.drawCard().getSuit(); // Draw the top card and get its suit
        server.sendUpdateToClients("The potential trump suit is: " + trump);
    }

    // Handle bidding for the trump suit
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
                    wantsToSet = player;
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
        return "oop"; // Default return value if no valid suit is chosen
    }

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
        for (Player player : playerArray) {
            server.sendPlayerUpdate(player, player.getCards().toString());
        }
    }

    // Set card values based on the trump suit
    public void setCardValue(String trumpSuit) {
        for (Player player : playerArray) {
            for (Card card : player.getCards()) {
                if (card.getSuit() == trumpSuit) {
                    if (card.getValue() == 11) { // If the card is a Jack (value 11)
                        card.setValue(card.getValue() * 3); // Triple the value of the Jack
                    } else { // For other cards in the trump suit
                        card.setValue(card.getValue() * 2); // Double the value
                    }
                    //Left Bower Value
                } else if (card.getValue() == 11 && card.getSuit() == "diamonds" && trumpSuit == "hearts") {
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