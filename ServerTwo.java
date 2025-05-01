import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javafx.beans.binding.ObjectExpression;

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
        this.trick.setDealer(this.trick.players.get(0).id);
        this.trick = trick.deal();

        System.out.println("Dealer id: " + trick.players.get(0).cards);

        for (Player p : this.trick.players) {
            System.out.println(p.cards);
            trick.setCurrentPlayer(p);
            sendTrick(p);
            reciveTrick(p);
        }

        System.out.println("Dealer id: " + trick.getPlayer(0).cards);

        //done with dealing onto selecting trump
        this.trick.setPhase("selectTrump");
        selectTrump(1);
        this.trick.setPhase("");
        if(trick.doneWifTrump == false) {
            newTrick();
            return;
        }

        System.out.println("Dealer id: " + trick.players.get(0).cards);

        System.out.println("Done with trump!");

        //display trump to everyone
        this.trick.setPhase("displayTrump");
        for (Player p : players) { //send trick to all players to update trump
            sendTrick(p);
        }


        this.trick.setPhase("dealersChoice");
        this.trick.setCurrentPlayer(trick.players.get(0));
        System.out.println(this.trick.getCurrentPlayer().getCards());
        sendTrick(trick.getDealer()); //send the trick to the dealer to see what card they want to echange
        reciveTrick(trick.getDealer());
        this.trick.setPhase("");

        for(Player p : players) {
            if (p.dealer != true) {
                reciveTrick(p);
            }
        }

        playHand(1);

    } 

    public void playHand(int i) throws IOException, ClassNotFoundException {
        if (i == 4) { //base case
            sendTrick(trick.getDealer()); //dealer always goes last
            this.trick = reciveTrick(trick.getDealer()); //recive the trick from the dealer
            trick.table.clear();
            return;
        }

        tempPlayer = players.get(findPos(trick.getDealer()) + i);
        trick.setCurrentPlayer(tempPlayer);
        
        System.out.println("sending trick to " + tempPlayer);
        sendTrick(tempPlayer);
        this.trick = reciveTrick(tempPlayer);
        sendTrickE(tempPlayer);
        playHand(i+1);
    }

    public void selectTrump(int i) throws IOException, ClassNotFoundException { //Done
        if (i == 4) { //checked for regualr trump (base-ish case)
            sendTrick(trick.getDealer());
            this.trick = reciveTrick(trick.getDealer());
            return;
        } else if (i == 8) { //base base case

        } else if (trick.doneWifTrump == true)
            return;
        else {
        tempPlayer = players.get(findPos(trick.getDealer()) + i);
        trick.setCurrentPlayer(tempPlayer);
        sendTrick(tempPlayer);
        this.trick = reciveTrick(tempPlayer);
        selectTrump(i + 1);
        }
    }

    public void sendTrick(Player p) throws IOException { //sends trick to specific player
        out.get(findPos(p)).writeObject(trick);
        //out.get(findPos(p)).flush();
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

    public Trick reciveTrick(Player p) throws IOException, ClassNotFoundException{ //recives then updates trick
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
