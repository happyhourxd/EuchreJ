import java.net.*;
import java.io.*;


public class server {
    
    int port;
    private Socket s = null;
    private ServerSocket ss = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    public server(int port) {
        this.port = port;

        try {
            ss = new ServerSocket(this.port);
            
        }
    }

}
