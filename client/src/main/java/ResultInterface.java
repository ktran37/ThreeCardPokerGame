import javafx.fxml.FXMLLoader;

public interface ResultInterface {
    // Configure dialog with result data
    void setGameResult(PokerInfo info);

    // Show the dialog modally (returns after dialog closes)
    void showModal();

    // Close the dialog if open
    void close();

    // Called when user clicks Play Again 
    void onPlayAgain();

    // Called when user clicks Exit
    void onExit();

    // Load FXML resource for this result presenter 
    boolean loadFXML(String resourcePath);

    // Return the underlying FXMLLoader if applicable 
    FXMLLoader getFXMLLoader();
}
