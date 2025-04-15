import java.util.*;
public class Main {
    
    //todo
    //determine if client or server by using arguments
    //gameplay elements

    static boolean isServer = false;
    static int port = 5000;
    static String addr = "127.0.0.1";

    public static void main(String[] args) {
        if (args.length > 0) {
            isServer = true;
        }
        if (!isServer) {
            Client client = new Client(addr, port);
            client.join();
            while (true) {

            }
        } else {
            isServer = true;
            Server server = new Server(port);
            while (isServer) {
                
            }
        }
    }
}
