import java.util.ArrayList;
import java.util.Scanner;


public class Game {
    private String trump; // The trump suit for the current round
    private Deck deck; // The deck of cards
    Player playerArray[] = {new Player(1, "Player1"),// Placeholder Array of players
                            new Player(2, "Player2"),
                            new Player(3, "Player3"),
                            new Player(4, "Player4")};

    public Game() {
        deck = new Deck(); // Initialize the deck
    }

    // Initialize a new round
    public void initializeRound() {
        deck.initializeDeck(); // Create a new deck
        deck.shuffle(); // Shuffle the deck
        dealCards(); // Deal cards to players
        setTrump(); // Determine the trump suit
        setCardValue(trump); // Set card values based on the trump suit
    }
    //TRUMP SUIT INITIALIZATION; Should probably be own file
    // Set the potential trump suit by drawing the top card from the deck
    private void setPotentialTrump() {
        trump = deck.drawCard().getSuit(); // Draw the top card and get its suit
    }
    
    // Determine the trump suit for the round
    public void setTrump() {
        setPotentialTrump(); // Set the initial potential trump suit
        System.out.println(trump);
        int accepted = 0; // Flag to track if a player accepts the trump suit

        // First round: Ask each player if they accept the trump suit
        for (int i = 0; i < 4; i++) { // Loop through all 4 players
            accepted = acceptTrump(playerArray[i]); // Ask the player if they accept the trump suit
            if (accepted == 1) { // If a player accepts the trump suit
                break; // Exit the loop
            }
        }

        // Second round: Ask each player to select trump suit
        
        if (accepted == 0) {
            int wantsToSet=0;
            for (int i=0;i<4;i++){
                askSetTrump(playerArray[i]);
                if (wantsToSet==1){
                    wantsToSet=i;
                    break;
                }
            }
            if (wantsToSet!=0){
                trump=askSuit(playerArray[wantsToSet]);
            }
            else{
                initializeRound();
            }
        }
    }

    //PLACEHOLDER METHODS
    // Ask a player if they accept the current trump suit
    public int acceptTrump(Player player) {
        Scanner scanner = new Scanner(System.in);
        String response = "";

        System.out.println(player.getName() + ", do you accept the trump suit (" + trump + ")? (yes/no):");
        while (true) {
            response = scanner.nextLine().toLowerCase(); // Read user input and convert to lowercase
            if (response.equals("yes")) {
                System.out.println(player.getName() + " accepted the trump suit: " + trump);
                scanner.close();
                return 1; // Player accepts the trump suit
            } else if (response.equals("no")) {
                System.out.println(player.getName() + " declined the trump suit.");
                scanner.close();
                return 0; // Player declines the trump suit
            } else {
                System.out.println("Invalid response. Please enter 'yes' or 'no'.");
            }
        }
    }
    // Ask a player if they accept the current trump suit
    public int askSetTrump(Player player) {
        Scanner scanner = new Scanner(System.in);
        String response = "";

        System.out.println(player.getName() + "set trump suit? (yes/no):");
        while (true) {
            response = scanner.nextLine().toLowerCase(); // Read user input and convert to lowercase
            if (response.equals("yes")) {
                scanner.close();
                return 1; // Player accepts the trump suit
            } else if (response.equals("no")) {
                System.out.println(player.getName() + " declined the trump suit.");
                scanner.close();
                return 0; // Player declines the trump suit
            } else {
                System.out.println("Invalid response. Please enter 'yes' or 'no'.");
            }
        }
    }
    // Ask a player to choose a trump suit if no one accepts the initial trump suit
    public String askSuit(Player player) {
        Scanner scanner = new Scanner(System.in);
        String suit = "";

        System.out.println( "Choose a suit (hearts, spades, clubs, diamonds):");
        while (true) {
            suit = scanner.nextLine().toLowerCase(); // Read user input and convert to lowercase
            if (suit.equals("hearts") || suit.equals("spades") || suit.equals("clubs") || suit.equals("diamonds")) {
                scanner.close();
                break; // Valid suit entered
            } else {
                System.out.println("Invalid suit. Please enter one of: hearts, spades, clubs, diamonds.");
            }
        }

        System.out.println(player.getName() + " chose the suit: " + suit);
        return suit; // Return the chosen suit
    }

    //DEAL CARD LOGIC
    // Deal cards to players in two rounds
    public void dealCards() {
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
            playerArray[i].setCards(playerCards.get(i)); // Set the player's hand
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
    /*
     * Ready to actually add play logic
     */
}
