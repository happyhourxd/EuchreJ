import java.net.*;
import java.io.*;
public class Client {
    
    String addr;
    int port;
    Player me;
    private Socket s;
    ObjectInputStream in;
    ObjectOutputStream out;
    
    public Client(String addr, int port) {
        this.addr = addr;
        this.port = port;
        this.me = new Player((int) System.currentTimeMillis(), "name");

    }

    public void join() throws IOException, ClassNotFoundException {
        System.out.println("Joining server...");
        this.s = new Socket(addr, port);
        System.out.println("Joined!");

        InputStream inputStream = this.s.getInputStream();
        this.in = new ObjectInputStream(inputStream);
        
        OutputStream outputStream = this.s.getOutputStream();
        this.out = new ObjectOutputStream(outputStream);

        Player p = (Player) in.readObject();
        System.out.println(p);
    }

    public void reciveTrick() throws IOException, ClassNotFoundException{
        this.me = (Player) this.in.readObject();
        System.out.println(this.me);
        System.out.println((String) this.in.readObject());
    }

    public void sendData() {
        try {
            //ObjectOutputStream out = new ObjectOutputStream((this.s.getOutputStream()));
            InputStream inputStream = s.getInputStream();
            ObjectInputStream in = new ObjectInputStream(inputStream);
            String message = (String) in.readObject();
            System.out.println(message);
            //out.write(1);
        } catch (Exception e) {

        }
    }
}
