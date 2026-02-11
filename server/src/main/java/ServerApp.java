import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parameters params = getParameters();
        if (!params.getRaw().isEmpty()) {
            // If a port was passed as first argument, skip intro screen
            try {
                int port = Integer.parseInt(params.getRaw().get(0));
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/serverMain.fxml"));
                Parent root = loader.load();
                ServerController controller = loader.getController();
                controller.initializeServer(port);
                Scene scene = new Scene(root, 900, 600);
                primaryStage.setTitle("Three Card Poker Server - Port " + port);
                primaryStage.setScene(scene);
                primaryStage.show();
                return;
            } catch (NumberFormatException | javafx.fxml.LoadException e) {
                // Fall back to intro if parsing/initialization fails
            }
        }
        Parent root = FXMLLoader.load(getClass().getResource("/serverIntro.fxml"));
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Three Card Poker Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
