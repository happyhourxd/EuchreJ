import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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


    public ServerTwo(int port) {
        this.port = port;
        playerSockets = new ArrayList<>();

        try {
            ss = new ServerSocket(this.port); //make a new server socket

            System.out.println("Starting server... \n Awaiting 4 clients");

            while(connections < 4) { // wait for 4 players
                playerSockets.add(ss.accept());
                connections++;
                System.out.println("A Connection was made");
            } // all 4 players have connected
            System.out.println("All 4 clients joined!");
        } catch (SocketException se) {
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // try {
            // for(Socket p : playerSockets) {//loop to get players and add them to the list
            //     out = new ObjectOutputStream(new BufferedOutputStream(p.getOutputStream()));
            //     in = new ObjectInputStream(p.getInputStream());
            //     Player tempPlayer = (Player) in.readObject(); //make a temp player from the user's connected
            //     tempPlayer.setOutputStream(out);
            //     tempPlayer.setInputStream(in);
            //     players.add(tempPlayer);
            // }
        // } catch (IOException e) {
        //     e.printStackTrace();
        // } catch (ClassNotFoundException cn) {
        //     cn.printStackTrace();
        // }
        // //done init!
        // newTrick();
    }

    public ServerTwo(ArrayList<Player> playerList) throws IOException, ClassNotFoundException { //ALSO FOR TESTING
        this.playerSockets = new ArrayList<>();
        this.out = new ArrayList<>();
        this.in = new ArrayList<>();
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
        this.players = playerList;
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

        //done with trump onto playing hands
        this.trick.setPhase("PlayHand");
        playHand(1);
    } 

    public void playHand(int i) throws IOException, ClassNotFoundException {
        if ((trick.getPhase().equals("selectTrump") || trick.getPhase().equals("selectTrumpAbnormal")) && (trick.getTrump() != null)) {
            return;
        }
        if (i == 4) {
            sendTrick(trick.getDealer());
            this.trick = receiveTrick(trick.getDealer());
            return;
        }
        Player currentPlayer = players.get(findPos(trick.getDealer()) + i);

        sendTrick(currentPlayer);
        this.trick = receiveTrick(currentPlayer);
        playHand(i+1);
    }

    public void selectTrump(Boolean normal) throws IOException, ClassNotFoundException {
        if (normal)
        trick.setPhase("selectTrump");
        else
        trick.setPhase("selectTrumpAbnormal");
        playHand(1);
    }

    public void sendTrick(Player p) throws IOException {
        out.get(findPos(p)).writeObject(this.trick);
    }

    public void sendTrick() throws IOException { //todo
        Player p = new Player(0, "name");
        for (ObjectOutputStream o : out) {
            o.writeObject(p);
            o.reset();
        }
    }

    public Trick receiveTrick(Player p) throws IOException, ClassNotFoundException{
        this.trick = (Trick) in.get(findPos(p)).readObject();
        sendTrick();
        return this.trick;
    }

    public int findPos(Player player) {
        for(int i = 0; i < players.size(); i++) {
            if (player.getId() == players.get(i).getId())
                return i;
        }
        return -1;
    }

    public Player findNextPlayer(Player player) {
        if (findPos(player) == 3)
            return players.get(0);
        else
            return players.get(findPos(player) + 1);
    }

    public Socket findSocket(Player player) {
        return playerSockets.get(findPos(player));
    }
}
