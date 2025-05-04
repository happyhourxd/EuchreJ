public class startServer {

    static int port = 5000;

    public static void main(String[] args) {
        try {
            Server server = new Server(5000);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
