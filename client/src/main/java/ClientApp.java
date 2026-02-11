import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/welcome.fxml"));
        
        Scene scene = new Scene(root, 1000, 570);
        primaryStage.setTitle(" Three Card Poker - Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        primaryStage.setOnCloseRequest(event -> {
            // Cleanup on close
            System.exit(0);
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
