import java.util.concurrent.CountDownLatch;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI extends Application {

    GUIController controller;
    int port;
    String ip;
    Boolean done = false;

    @Override
    public void start(Stage stage) throws Exception {
        //nates code:
        String[] value = connectionGUI();

        System.out.println("eek");


        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        this.controller = loader.<GUIController>getController();
        stage.setTitle("EuchreJ");
        stage.setScene(scene);
        stage.show();
    
        Thread connection = new Thread() {
            public void run() {
                controller.start(Integer.parseInt(value[0]), value[1]);
            }
        };
        connection.start();
    }

    public String[] connectionGUI() {
        CountDownLatch latch = new CountDownLatch(1); // latch starts at 1
        //vars
        
        // (JFRAME)
        JFrame frame = new JFrame("Connect to Euchre Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Closes window on clicking exit
        frame.setSize(350, 180); // size
        frame.setLocationRelativeTo(null); // Center window

        JLabel ipLabel = new JLabel("Server IP:"); // IP address
        JTextField ipField = new JTextField("127.0.0.1", 15); // "Default IP" means hosting

        JLabel portLabel = new JLabel("Port:"); // port
        JTextField portField = new JTextField("5000", 5); // Default port 5000

        JButton connectButton = new JButton("Connect"); // connect button 
        
        

        // panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // stack vertically, add elements, put them on the frame, and make visable

        panel.add(ipLabel);
        panel.add(ipField);
        panel.add(portLabel);
        panel.add(portField);
        panel.add(connectButton);

        frame.add(panel);
        frame.setVisible(true);
        

        // Defining "connect"
        connectButton.addActionListener(e -> {
            String ip = ipField.getText().trim(); // Read input from the text fields, and trim
            String portText = portField.getText().trim(); 

            // no IP = default (host)
            if (ip.isEmpty()) ip = "127.0.0.1";

            // error handling. If invalid or empty, use default port "5000".
            try {
                port = portText.isEmpty() ? 5000 : Integer.parseInt(portText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid port. Using default (5000).");
                port = 5000;
            }

            System.out.println("eek2");
            // Close the window after function
            done = true;
            frame.dispose();
            latch.countDown();
        });

        try {
            latch.await(); // Block JavaFX thread safely
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("eek3");
        String[] newString = {Integer.toString(port),ip};
        return newString;
    }

    

    public static void main(String[] args) {
        launch(args);
    }

}
