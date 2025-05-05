// run this file to start the server (required)
public class startServer {

    static int port = 5000; // change to change port

    public static void main(String[] args) {
        try {
            Server server = new Server(port);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
