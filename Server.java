import java.util.ArrayList;
import java.net.*;
import java.io.*;

public class Server {
    
    int port;
    int connections = 0;
    ArrayList<Socket> playerSockets; //a list of the player sockets
    ArrayList<Player> players; //a list of the players
    Player tempPlayer;
    private Socket s;
    private ServerSocket ss;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Server(int port) {
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
                //out = new DataOutputStream( new BufferedOutputStream(p.getOutputStream()));
                in = new ObjectInputStream(p.getInputStream());
                Player tempPlayer = (Player) in.readObject(); //make a temp player from the user's connected
                players.add(tempPlayer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cn) {
            cn.printStackTrace();
        }
            
        }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void exit() {
        for(Socket p : playerSockets) {
            try {
            p.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // public void applyMethod(player player) {
    //     for (player i : players) {
    //         if (i.id == player.id) {

    //         }
    //     }
    // }

}
