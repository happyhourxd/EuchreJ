import java.util.ArrayList;
import java.net.*;
import java.io.*;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class server {
    
    int port;
    int connections = 0;
    ArrayList<Socket> playerSockets; //a list of the player sockets
    ArrayList<player> players; //a list of the players
    player tempPlayer;
    private Socket s = null;
    private ServerSocket ss = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    public server(int port) {
        this.port = port;

        try {
            ss = new ServerSocket(this.port); //make a new server socket

            System.out.println("Starting server... \n Awaiting 4 clients");

            while(connections > 5) { // wait for 4 players
                playerSockets.add(ss.accept());
                connections++;
                System.out.println("A Connection was made");
            } // all 4 players have connected

            for(Socket p : playerSockets) {//loop to get players and add them to the list
                //out = new DataOutputStream( new BufferedOutputStream(p.getOutputStream()));
                in = new ObjectInputStream(p.getInputStream());
                player tempPlayer = (player) in.readObject(); //make a temp player from the user's connected
                players.add(tempPlayer);
            }

            } catch (SocketException se) {
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException cn) {
                cn.printStackTrace();
            }
        }

    public ArrayList<player> getPlayers() {
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
