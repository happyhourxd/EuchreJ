import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerTwo {

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
    public Player dealer;

    public ServerTwo(int port) throws IOException, ClassNotFoundException {
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
        newTrick();
    }

    public ServerTwo(ArrayList<Player> playerList) throws IOException, ClassNotFoundException { //ALSO FOR TESTING
        this.playerSockets = new ArrayList<>();
        this.out = new ArrayList<>();
        this.in = new ArrayList<>();
        this.players = playerList;
        ss = new ServerSocket(5000);
        
        System.out.print("Waiting for clients...");
        
        while(connections < 4) {
            this.s = ss.accept();
            playerSockets.add(s);

            OutputStream outputStream = this.s.getOutputStream();
            InputStream inputStream = this.s.getInputStream();
            
            this.out.add(new ObjectOutputStream(outputStream));
            this.in.add(new ObjectInputStream(inputStream));

            System.out.println("New Connection made!");
            connections++;
        }
        System.out.println("all connections made!");
        newTrick();
    }

    public void newTrick() throws IOException, ClassNotFoundException{
        this.trick = new Trick(players);
        trick.deal();

        System.out.println("Dealer id: " + trick.getDealer().getId());

        //done with dealing onto selecting trump

        selectTrump(true);
        
        if(trick.getTrump() == null) {
            selectTrump(false);
        }
        if(trick.getTrump() == null) {
            newTrick();
            return;
        }
        sendTrick(trick.getDealer());
        reciveTrick(trick.getDealer());
        trick.setPhase("showTrump");
        sendTrick(); //show selected trump suit

        //done with trump onto playing hands
        this.trick.setPhase("PlayHand");
        playHand(1);
    } 

    public void playHand(int i) throws IOException, ClassNotFoundException {
        System.out.println(trick.getTrump());
        if ((trick.getPhase().equals("selectTrump") || trick.getPhase().equals("selectTrumpAbnormal")) && (trick.getTrump() != null)) { //return clause for no trump
            return;
        }

        if (i == 4) { //base case
            sendTrick(trick.getDealer()); //dealer always goes last
            this.trick = reciveTrick(trick.getDealer()); //recive the trick from the dealer
            return;
        }

        tempPlayer = players.get(findPos(trick.getDealer()) + i);
        trick.setCurrentPlayer(tempPlayer);
        System.out.println("sending trick to " + tempPlayer);
        sendTrick(tempPlayer);
        this.trick = reciveTrick(tempPlayer);
        playHand(i+1);
    }

    public void selectTrump(Boolean normal) throws IOException, ClassNotFoundException {
        if (normal)
        trick.setPhase("selectTrump");
        else
        trick.setPhase("selectTrumpAbnormal");
        System.out.println("Selecting trump...");
        playHand(1);
    }

    public void sendTrick(Player p) throws IOException { //sends trick to specific player
        out.get(findPos(p)).writeObject(this.trick);
        //out.get(findPos(p)).flush();
    }

    public void sendTrick() throws IOException { //sends trick to all players
        for (ObjectOutputStream o : out) {
            o.writeObject(this.trick);
            o.flush();
        }
    }

    public Trick reciveTrick(Player p) throws IOException, ClassNotFoundException{ //recives then updates trick
        this.trick = (Trick) in.get(findPos(p)).readObject();
        sendTrick();
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
