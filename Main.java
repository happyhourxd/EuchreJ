import java.util.ArrayList;

public class Main {
    
    //todo
    //determine if client or server by using arguments
    //gameplay elements

    static boolean isServer = false;
    static int port = 5000;
    static String addr = "127.0.0.1";

    public static void main(String[] args) {

        ArrayList<Player> tempPlst = new ArrayList<>();
        tempPlst.add(new Player(1, "jane")); //default dealer
        tempPlst.add(new Player(2, "jon"));
        tempPlst.add(new Player(3, "jack"));
        tempPlst.add(new Player(4, "phil"));
        
        //ServerTwo server = new ServerTwo(tempPlst);

        try {
        isServer = true;
        if (args.length > 0) {
            
        }
        if (!isServer) {
            
                Client client = new Client(addr, port);
                client.join();
                client.reciveTrick();
        } else {
            //Server server = new Server(port);
            ServerTwo server = new ServerTwo(5000 );
        }
        } catch (Exception e) {
            System.out.println(e);
        } 
    }
}
