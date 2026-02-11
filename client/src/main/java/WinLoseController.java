import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class WinLoseController implements ResultInterface {
    @FXML private Label resultTitleLabel;
    @FXML private Label amountLabel;
    @FXML private Label messageLabel;
    @FXML private Button playAgainButton;
    @FXML private Button exitButton;
    
    private Client client;
    private GamePlayController gamePlayController;
    private FXMLLoader loader;
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    public void setReturnController(GamePlayController controller) {
        this.gamePlayController = controller;
    }
    
    // Legacy setter used by some code paths
    public void setGameResult(boolean won, int amount, String message) {
        if (won) {
            resultTitleLabel.setText("YOU WON!");
            resultTitleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #49b34cff;");
            amountLabel.setText("+$" + amount);
            amountLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #49b34cff;");
        } else {
            resultTitleLabel.setText("YOU LOST");
            resultTitleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #f44336;");
            amountLabel.setText("-$" + Math.abs(amount));
            amountLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #f44336;");
        }
        
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
    }

    // New interface-based setter
    @Override
    public void setGameResult(PokerInfo info) {
        boolean won = info.getTotalWinnings() >= 0;
        int amount = info.getTotalWinnings();
        String message = info.getMessage();
        setGameResult(won, amount, message);
    }
    
    @FXML
    public void handlePlayAgain() {
        // Close the result dialog and return to the existing GamePlayController
        try {
            // Close this window (dialog)
            Stage stage = (Stage) playAgainButton.getScene().getWindow();
            stage.close();

            // Ask the original GamePlayController to reset for a new hand (preserves gameInfoTextArea)
            if (gamePlayController != null) {
                gamePlayController.returnFromResult();
            }
        } catch (Exception e) {
            // Ignore window close errors
        }
    }
    
    @FXML
    public void handleExit() {
        if (client != null) {
            client.disconnect();
        }
        Platform.exit();
    }

    // ResultInterface implementations for dialog control
    @Override
    public void showModal() {
        try {
            Stage stage = (Stage) playAgainButton.getScene().getWindow();
            stage.showAndWait();
        } catch (Exception e) {
            // Ignore
        }
    }

    @Override
    public void close() {
        try {
            Stage stage = (Stage) playAgainButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            // Ignore
        }
    }

    @Override
    public void onPlayAgain() {
        handlePlayAgain();
    }

    @Override
    public void onExit() {
        handleExit();
    }

    @Override
    public boolean loadFXML(String resourcePath) {
        try {
            loader = new FXMLLoader(getClass().getResource(resourcePath));
            loader.load();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public FXMLLoader getFXMLLoader() {
        return loader;
    }
}
