import java.util.ArrayList;
import java.net.*;
import java.io.*;

public class Server {
    
    int port;
    int connections = 0;
    ArrayList<Socket> playerSockets; //a list of the player sockets
    ArrayList<ObjectOutputStream> outputStreams; //a list of output streams for each client
    ArrayList<Player> players;
    private Game game; //a list of the players
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
                out = new ObjectOutputStream(new BufferedOutputStream(p.getOutputStream()));
                outputStreams.add(out);
                in = new ObjectInputStream(p.getInputStream());
                Player tempPlayer = (Player) in.readObject(); //make a temp player from the user's connected
                tempPlayer.setOutputStream(out);
                tempPlayer.setInputStream(in);
                players.add(tempPlayer);
            }
            //idk if right place but ye!
            game = new Game(players, 10);
            gameLoop();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cn) {
            cn.printStackTrace();
        }
            
    }
    private void gameLoop(){
        game.startGame(this);
    }
    public void sendUpdateToClients(Object update) {
        for (ObjectOutputStream out : outputStreams) {
            try {
                out.writeObject(update); // Write the update to the client's output stream
                out.flush(); // Ensure the data is sent immediately
            } catch (IOException e) {
                System.err.println("Error sending update to client: " + e.getMessage());
            }
        }
    }
    public String promptPlayerForInput(Player player, String promptMessage) {
        try {
            // Send the prompt message to the player
            ObjectOutputStream out = player.getOutputStream();
            out.writeObject(promptMessage);
            out.flush();
    
            // Wait for the player's response
            ObjectInputStream in = new ObjectInputStream(player.getInputStream());
            String response = (String) in.readObject(); // Read the player's response
            return response; // Return the player's response
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error prompting player for input: " + e.getMessage());
            return null; // Return null if an error occurs
        }
    }
    public void sendPlayerUpdate(Player player, String message) {
        try {
            ObjectOutputStream out = player.getOutputStream();
            out.writeObject(message);
        }
        catch(IOException e){
            System.err.println("Error sending message:"+e.getMessage());
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
