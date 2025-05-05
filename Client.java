import java.net.*;
import java.io.*;
public class Client {
    
    String addr;
    int port;
    Player me;
    Trick trick;
    private Socket s;
    ObjectInputStream in;
    ObjectOutputStream out;
    
    public Client(String addr, int port) {
        this.addr = addr;
        this.port = port;
        this.me = new Player((int) System.nanoTime(), "name");

    }

    public void join() throws IOException, ClassNotFoundException {
        System.out.println("Joining server...");
        this.s = new Socket(addr, port);
        System.out.println("Joined!");

        InputStream inputStream = this.s.getInputStream();
        OutputStream outputStream = this.s.getOutputStream();

        this.in = new ObjectInputStream(inputStream);
        this.out = new ObjectOutputStream(outputStream);

        out.writeObject(this.me);
        out.flush();

        Player p = (Player) in.readObject();
    }

    public void setTrick(Trick trick) {
        this.trick = trick;
    }

    public Trick reciveTrick() throws IOException, ClassNotFoundException{
        this.trick = (Trick) this.in.readObject();
        return this.trick;
    }

    public void sendTrick() throws IOException, ClassNotFoundException{
        this.out.reset();
        this.out.writeObject(this.trick);
        this.out.flush();
    }
}
