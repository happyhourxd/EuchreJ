public class Main {
    
    //todo
    //determine if client or server then act on that
    //gameplay elements

    static boolean isServer = false;
    static int port = 5000;
    static String addr = "127.0.0.1";

    public static void main(String[] args) {
        if (args[0] == "s") {
            isServer = true;
            Server server = new Server(port);
        }
        else {
            Client client = new Client(addr, port);
        }
    }
}
