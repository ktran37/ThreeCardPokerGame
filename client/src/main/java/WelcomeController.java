import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class WelcomeController {
    @FXML private TextField ipField;
    @FXML private TextField portField;
    @FXML private Button connectButton;
    @FXML private Label messageLabel;
    
    private Client client;
    private int attemptCount = 0;
    
    @FXML
    public void initialize() {
        ipField.setText("localhost");
        portField.setText("5555");
    }
    
    @FXML
    public void handleConnect() {
        String ip = ipField.getText().trim();
        String portText = portField.getText().trim();
        
        if (ip.isEmpty() || portText.isEmpty()) {
            messageLabel.setText("Please enter both IP and port");
            return;
        }
        
        try {
            int port = Integer.parseInt(portText);
            
            if (port < 1024 || port > 65535) {
                messageLabel.setText("Port must be between 1024 and 65535");
                return;
            }
            
            messageLabel.setText("Connecting...");
            connectButton.setDisable(true);
            
            client = new Client(ip, port);
            
            // Try connecting with retry logic
            connectWithRetry(3);
            
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid port number");
            connectButton.setDisable(false);
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
            connectButton.setDisable(false);
        }
    }
    
    private void connectWithRetry(int maxAttempts) {
        new Thread(() -> {
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                final int currentAttempt = attempt;
                javafx.application.Platform.runLater(() -> {
                    messageLabel.setText("Connecting... (attempt " + currentAttempt + "/" + maxAttempts + ")");
                });
                
                // Small delay to ensure message displays
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    break;
                }
                
                if (client.connect()) {
                    attemptCount = 0; // reset after success
                    // Connection successful, switch to game play scene
                    javafx.application.Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameplay.fxml"));
                            Parent root = loader.load();
                            
                            GamePlayController controller = loader.getController();
                            controller.setClient(client);
                            
                            Stage stage = (Stage) connectButton.getScene().getWindow();
                            Scene scene = new Scene(root, 1000, 700);
                            stage.setScene(scene);
                            stage.setTitle("Three Card Poker - Playing");
                        } catch (java.io.IOException e) {
                            messageLabel.setText("Error loading game: " + e.getMessage());
                            connectButton.setDisable(false);
                        }
                    });
                    return;
                }
                
                // Failed - wait before retry 
                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(1000 * attempt); // 1s, 2s, 3s
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
            
            // All attempts failed
            attemptCount++;
            javafx.application.Platform.runLater(() -> {
                messageLabel.setText("Could not connect after " + maxAttempts + " attempts. Check server is running or port number.");
                connectButton.setDisable(false);
            });
        }).start();
    }
}
