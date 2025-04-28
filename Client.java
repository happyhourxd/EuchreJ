import java.net.*;
import java.io.*;
public class Client {
    
    String addr;
    int port;
    Player me;
    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;
    
    public Client(String addr, int port) {
        this.addr = addr;
        this.port = port;
        this.me = new Player((int) System.currentTimeMillis(), "name");

    }

    public void join() {
        System.out.println("Joining server...");
        try {
            s = new Socket(addr, port);
            System.out.println("Joined!");
        }
        catch (UnknownHostException u) {
            System.out.println(u);
            return;
        }
        catch (IOException i) {
            System.out.println(i);
            return;
        }
        while(true) {
            
        }

    }
}
