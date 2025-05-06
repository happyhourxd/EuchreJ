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

    public Server(int port) throws IOException, ClassNotFoundException {
        this.playerSockets = new ArrayList<>();
        this.out = new ArrayList<>();
        this.in = new ArrayList<>();
        this.players = new ArrayList<>();
        ss = new ServerSocket(port);

        System.out.println("Waiting for players...");

        while(connections < 4) {
            this.s = ss.accept();
            playerSockets.add(s);

            OutputStream outputSteam = this.s.getOutputStream();
            InputStream inputStream = this.s.getInputStream();

            this.out.add(new ObjectOutputStream(outputSteam));
            this.in.add(new ObjectInputStream(inputStream));

            tempPlayer = (Player) this.in.get(connections).readObject();

            tempPlayer.setTeam(connections%2);
            if(connections == 0) {
                tempPlayer.setDealer(true);
            }

            this.out.get(connections).writeObject(tempPlayer);
            this.out.get(connections).flush();

            this.players.add(tempPlayer);

            System.out.println("New connection made!");
            connections++;
        }

        System.out.println("All connections made!");
        Player dealer = newTrick(this.players.get(3));
        System.out.println("calculating score...");
        this.trick.calcScore();
        while (score[0] < 10 || score[1] < 10) {
            score = this.trick.score;
            newTrick(dealer);
            this.trick.calcScore();
            this.trick.cardsLeft[0] = 5;
            this.trick.cardsLeft[1] = 5;
            this.trick.cardsLeft[2] = 5;
            this.trick.cardsLeft[3] = 5;
        }
    }

    public Player newTrick(Player dealer) throws IOException, ClassNotFoundException{
        this.trick = new Trick(players);
        this.trick.score[0] += score[0];
        this.trick.score[1] += score[1];
        for (int i = 0; i < players.size(); i++) {
            if (this.trick.players.get(i).id == dealer.id) {
                this.trick.setDealer(this.trick.getPlayer((i+1)%4).id);
            }
        }
        this.trick = trick.deal();

        System.out.println("Dealer id: " + trick.dealer.cards);

        for (Player p : this.trick.players) { //show cards
            trick.setCurrentPlayer(p);
            sendTrick(p);
            receiveTrick(p);
        }


        //done with dealing onto selecting trump
        this.trick.setPhase("selectTrump");

        selectTrump(1);
        this.trick.currentPlayer = null;
        //display trump to everyone
        this.trick.setPhase("displayTrump");
        for (Player p : players) { //send trick to all players to update trump
            sendTrick(p);
        }

        if (!weirdTrump) {
            this.trick.setPhase("dealersChoice");
            this.trick.setCurrentPlayer(this.trick.getDealer());
            
            receiveTrick(trick.getDealer());
            this.trick.setPhase("");
            
        }

        this.trick.currentPlayer = null;

        for(Player p : players) {    //11
            sendTrick(p);
        }

        for (int j = 0; j < 5; j++) {
            playHand(1);
            this.trick.calcWins();
            this.trick.clearTable();
            score = this.trick.score;
        }
        
        return this.trick.dealer;
    }        

    public void playHand(int i) throws IOException, ClassNotFoundException {
        if (i == 4) { //base case
            trick.setCurrentPlayer(this.trick.dealer);
            sendTrick(trick.getDealer()); //dealer always goes last
            this.trick = receiveTrick(trick.getDealer()); //receive the trick from the dealer
            sendTrick(trick.getDealer());
            for (Player p : players ) {
                if (p.getId() != trick.getDealer().id) {
                    sendTrick(p);
                    receiveTrick(p);
                }
            }
            return;
        }

        tempPlayer = this.trick.players.get((findPos(trick.getDealer()) + i)%4);
        trick.turn = ((findPos(trick.getDealer()) +i)%4);
        this.trick.setCurrentPlayerByID(tempPlayer.id);
        sendTrick(this.trick.currentPlayer);
        this.trick = receiveTrick(tempPlayer);
        sendTrick(tempPlayer);
        

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

    public void selectTrump(int i) throws IOException, ClassNotFoundException { //Done
        this.weirdTrump = this.trick.weirdTrump;
        if (trick.doneWifTrump == true) {
            return;
        } else if (i == 4) { //checked for regualr trump (base-ish case)
            sendTrick(trick.getDealer());
            this.trick = receiveTrick(trick.getDealer());
            this.trick.setPhase("weirdTrump");
            if (this.trick.trump != null) {
                return;
            } else {
                selectTrump(i+1);
            }
        } else {
        tempPlayer = players.get((findPos(trick.getDealer()) + i)%4);
        trick.setCurrentPlayer(tempPlayer);
        sendTrick(tempPlayer);
        this.trick = receiveTrick(tempPlayer);
        selectTrump(i + 1);
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
