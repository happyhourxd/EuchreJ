import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private Socket s;
    private ServerSocket ss;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    public Trick trick;


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
        try {
            for(Socket p : playerSockets) {//loop to get players and add them to the list
                out = new ObjectOutputStream(new BufferedOutputStream(p.getOutputStream()));
                in = new ObjectInputStream(p.getInputStream());
                Player tempPlayer = (Player) in.readObject(); //make a temp player from the user's connected
                tempPlayer.setOutputStream(out);
                tempPlayer.setInputStream(in);
                players.add(tempPlayer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cn) {
            cn.printStackTrace();
        }
        //done init!
        newTrick();
    }

    public void newTrick() {
        this.trick = new Trick(players);
        trick.deal();
        sendTrick("deal");
        trick = reciveTrick();

        //done with dealing onto selecting trump

        selectTrump(true);
        trick = reciveTrick();
        if(trick.getTrump() == null) {
            selectTrump(false);
        }
        trick = reciveTrick();
        if(trick.getTrump() == null) {
            newTrick();
            return;
        }

        //done with trump onto playing hands

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getId() == trick.getDealer().getId()) {
                if(i == 3)
                    sendTrick(0, "playHand");
                else
                    sendTrick(i, "playHand");
            }
        }

        this.trick = reciveTrick();

    } 

    public void selectTrump(Boolean normal) {
        String phase;
        if (normal)
        phase = "selectTrump";
        else
        phase = "selectTrumpAdnormal";
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getId() ==  trick.getDealer().getId()) {
                if (i == 3) {
                    sendTrick(0, phase);
                } else {
                    sendTrick(i+1, phase);
                }
            }
        }
    }

    public void sendTrick(int id, String phase) {

    }

    public void sendTrick(String phase) { //todo

    }

    public Trick reciveTrick() { //todo
        return this.trick;
    }
}
