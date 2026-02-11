import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ServerController {
    @FXML private Button stopButton;
    @FXML private ListView<String> gameListView;
    @FXML private Label clientCountLabel;
    
    private Server server;
    private ObservableList<String> gameLog;
    private DateTimeFormatter timeFormatter;
    
    @FXML
    public void initialize() {
        gameLog = FXCollections.observableArrayList();
        gameListView.setItems(gameLog);
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    }
    
    public void initializeServer(int port) {
        server = new Server(port, this);
        server.start();
    }
    
    @FXML
    public void handleStop() {
        if (server != null) {
            server.stop();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Server Stopped");
            alert.setHeaderText(null);
            alert.setContentText("Server has been stopped. Close this window to exit.");
            alert.showAndWait();
            
            Stage stage = (Stage) stopButton.getScene().getWindow();
            stage.close();
        }
    }
    
    public void addMessage(String message) {
        Platform.runLater(() -> {
            String timestamp = LocalTime.now().format(timeFormatter);
            gameLog.add("[" + timestamp + "] " + message);
            
            // Auto-scroll to bottom
            gameListView.scrollTo(gameLog.size() - 1);
        });
    }
    
    public void updateClientCount(int count) {
        Platform.runLater(() -> {
            clientCountLabel.setText("Active Clients: " + count);
        });
    }
}
