import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI extends Application {

    GUIController controller;

    @Override
    public void start(Stage stage) throws Exception {
        //nates code:



        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        this.controller = loader.<GUIController>getController();
        stage.setTitle("EuchreJ");
        stage.setScene(scene);
        stage.show();
    
        Thread connection = new Thread() {
            public void run() {
                controller.start(5000, "localhost");
            }
        };
        connection.start();
    }

    

    public static void main(String[] args) {
        launch(args);
    }

}
