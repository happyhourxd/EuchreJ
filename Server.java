import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    int port;
    int connections = 0;
    ArrayList<Socket> playerSockets; //a list of the player sockets
    ArrayList<Player> players;
    Player tempPlayer;
    public Socket s;
    private ServerSocket ss;
    private ArrayList<ObjectInputStream> in;
    private ArrayList<ObjectOutputStream> out;
    public Trick trick;
    public int wins[] = {0,0};
    public int score[] = {0,0};
    public Player dealer;
    public boolean weirdTrump = false;

    public Server(int port) throws IOException, ClassNotFoundException { // makes new server jsut needs port to listen to
        this.playerSockets = new ArrayList<>(); //arraylist of the player sockets
        this.out = new ArrayList<>(); //array list of the objectoutput streams
        this.in = new ArrayList<>(); //arraylist of the objectinput streams
        this.players = new ArrayList<>(); //arraylist of the players
        ss = new ServerSocket(port); //server socket

        System.out.println("Waiting for players...");

        while(connections < 4) { //waits for 4 players to join
            this.s = ss.accept(); //accept the incoming socket
            playerSockets.add(s); //add socket to list

            OutputStream outputSteam = this.s.getOutputStream();//make output stream
            InputStream inputStream = this.s.getInputStream(); //make input steam

            this.out.add(new ObjectOutputStream(outputSteam)); //add each to list
            this.in.add(new ObjectInputStream(inputStream));

            tempPlayer = (Player) this.in.get(connections).readObject(); //read the incoming player (used for id and maybe name?)

            tempPlayer.setTeam(connections%2); //set team for every other player
            if(connections == 0) { //first joining player is dealer
                tempPlayer.setDealer(true);
            }

            this.out.get(connections).writeObject(tempPlayer); //give the players their new player object
            this.out.get(connections).flush();

            this.players.add(tempPlayer);

            System.out.println("New connection made!"); //notify that a connection was made
            connections++;
        }

        System.out.println("All connections made!"); //notify all connections are made
        Player dealer = newTrick(this.players.get(3)); //make a new trick and set its return value as next dealer (dealer is set to last player so the first player who joined is next dealer)
        System.out.println("calculating score...");
        this.trick.calcScore(); //calc the score
        while (score[0] < 10 || score[1] < 10) { //while ether score is less than 10 keep going
            score = this.trick.score; //keep score
            newTrick(dealer); //new trick
            this.trick.calcScore(); //write score
            this.trick.cardsLeft[0] = 5; //reset cards left just in case
            this.trick.cardsLeft[1] = 5;
            this.trick.cardsLeft[2] = 5;
            this.trick.cardsLeft[3] = 5;
        }
    }

    public Player newTrick(Player dealer) throws IOException, ClassNotFoundException{
        this.trick = new Trick(players);
        this.trick.score[0] += score[0]; //write old score
        this.trick.score[1] += score[1];

        for (int i = 0; i < players.size(); i++) { //set dealer to next player
            if (this.trick.players.get(i).id == dealer.id) {
                this.trick.setDealer(this.trick.getPlayer((i+1)%4).id);
            }
        }
        this.trick = trick.deal(); //deal cards

        System.out.println("Dealer id: " + trick.dealer.cards); //notify server who is dealer

        for (Player p : this.trick.players) { //show cards
            trick.setCurrentPlayer(p);
            sendTrick(p);
            receiveTrick(p);
        }


        //done with dealing onto selecting trump
        this.trick.setPhase("selectTrump");

        selectTrump(1); 
        this.trick.currentPlayer = null; //reset current player
        //display trump to everyone
        this.trick.setPhase("displayTrump");
        for (Player p : players) { //send trick to all players to update trump
            sendTrick(p);
        }

        if (!weirdTrump) { //if werid trump didnt happen the dealer needs to update their cards
            this.trick.setPhase("dealersChoice");
            this.trick.setCurrentPlayer(this.trick.getDealer());
            receiveTrick(trick.getDealer()); //client logic determines that the dealer will send their trick
            this.trick.setPhase("");//reset phase
        }

        this.trick.currentPlayer = null;

        for(Player p : players) {//update players if dealer updated their hand
            sendTrick(p);
        }

        for (int j = 0; j < 5; j++) {//run play hand!
            playHand(1);
            this.trick.calcWins(); //calculate the wins
            this.trick.clearTable(); //clear the 'table' <- where played cards are held
            score = this.trick.score; //update the score
        }
        return this.trick.dealer; //returnd dealer so next dealer can be selected
    }        

    public void playHand(int i) throws IOException, ClassNotFoundException { //actually plays the hand
        if (i == 4) { //base case
            trick.setCurrentPlayer(this.trick.dealer); 
            sendTrick(trick.getDealer()); //dealer always goes last
            this.trick = receiveTrick(trick.getDealer()); //receive the trick from the dealer
            sendTrick(trick.getDealer());
            for (Player p : players ) { //update players after dealer played their hand
                if (p.getId() != trick.getDealer().id) {
                    sendTrick(p);
                    receiveTrick(p);
                }
            }
            return; //break recursive loop
        }

        tempPlayer = this.trick.players.get((findPos(trick.getDealer()) + i)%4); 
        trick.turn = ((findPos(trick.getDealer()) +i)%4); //set turn value to current player
        this.trick.setCurrentPlayerByID(tempPlayer.id);
        sendTrick(this.trick.currentPlayer); //send trick to current player
        this.trick = receiveTrick(tempPlayer); //recive trick
        sendTrick(tempPlayer); //send it back ( acts as time.sleep for them deciding which card to play)
        

        if (i == 1) {
            for (Card c : this.trick.table)
                if (c.suit != null)
                this.trick.leadingSuit = c.suit;
        }

        for (Player p : players) {
            if (p.getId() != tempPlayer.id) {
                sendTrick(p);
                receiveTrick(p);
            }
        }    
        
        playHand(i+1);
    }

    public void selectTrump(int i) throws IOException, ClassNotFoundException {
        this.weirdTrump = this.trick.weirdTrump; //if trump went around a second time use weird trump
        if (trick.doneWifTrump == true) { //if trump has been selected exit the loop
            return;
        } else if (i == 4) { //checked for regualr trump (base-ish case)
            sendTrick(trick.getDealer());
            this.trick = receiveTrick(trick.getDealer());
            this.trick.setPhase("weirdTrump"); //after the dealer is done if it goes around again its weird trump
            if (this.trick.trump != null) { //if trump is selected after dealer break loop
                return;
            } else {
                selectTrump(i+1);
            }
        } else {
        tempPlayer = players.get((findPos(trick.getDealer()) + i)%4); //temp player is whoever is selecting trump
        trick.setCurrentPlayer(tempPlayer); 
        sendTrick(tempPlayer);  //send it
        this.trick = receiveTrick(tempPlayer); //recive updated trick
        selectTrump(i + 1); //go again
        }
    }

    public void sendTrick(Player p) throws IOException { //sends trick to specific player
        out.get(findPos(p)).reset();
        out.get(findPos(p)).writeObject(this.trick);
        out.get(findPos(p)).flush();
    }

    public void sendTrickE(Player p) throws IOException { //sends trick to all players
        int i = 0;
        for (ObjectOutputStream o : out) {
            if (findPos(p) != i) {
                o.writeObject(this.trick);
            o.flush();
            }
        }
    }

    public Trick receiveTrick(Player p) throws IOException, ClassNotFoundException{ //receives then updates trick
        this.trick = (Trick) in.get(findPos(p)).readObject();
        return this.trick;
    }

    public int findPos(Player player) { //find the position of the player in players array
        for(int i = 0; i < players.size(); i++) {
            if (player.getId() == players.get(i).getId())
                return i;
        }
        return -1;
    }

    public Player findNextPlayer(Player player) { //returns the next player after inputted player in player array
        if (findPos(player) == 3)
            return players.get(0);
        else
            return players.get(findPos(player) + 1);
    }

    public Socket findSocket(Player player) { //finds the socket of inputed player
        return playerSockets.get(findPos(player));
    }
}
