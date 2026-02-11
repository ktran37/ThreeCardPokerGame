import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ServerIntroController {
    @FXML private TextField portField;
    @FXML private Button startButton;
    @FXML private Label statusLabel;
    
    @FXML
    public void initialize() {
        portField.setText("5555");
    }
    
    @FXML
    public void handleStart() {
        String portText = portField.getText().trim();
        
        if (portText.isEmpty()) {
            statusLabel.setText("Please enter a port number");
            return;
        }
        
        try {
            int port = Integer.parseInt(portText);
            
            if (port < 1024 || port > 65535) {
                statusLabel.setText("Port must be between 1024 and 65535");
                return;
            }
            
            // Switch to main server scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/serverMain.fxml"));
            Parent root = loader.load();
            
            ServerController controller = loader.getController();
            controller.initializeServer(port);
            
            Stage stage = (Stage) startButton.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            stage.setScene(scene);
            stage.setTitle("Three Card Poker Server - Running on Port " + port);
            
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid port number");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }
}
