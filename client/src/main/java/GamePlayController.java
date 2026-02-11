import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.ArrayList;

public class GamePlayController {
    @FXML private ImageView playerCard1, playerCard2, playerCard3;
    @FXML private ImageView dealerCard1, dealerCard2, dealerCard3;
    @FXML private TextField anteBetField, pairPlusBetField;
    @FXML private Label playBetLabel, totalWinningsLabel;
    @FXML private Label playerWinningsLabel;
    @FXML private ListView<String> gameInfoListView;
    @FXML private Button dealButton, playButton, foldButton;
    
    // Result panel components (inline display)
    @FXML private javafx.scene.layout.VBox resultPanel;
    @FXML private Label resultTitleLabel;
    @FXML private Label resultAmountLabel;
    @FXML private TextArea resultMessageArea;
    @FXML private Button playAgainButton;
    
    private Client client;
    private int totalWinnings = 0;
    private int currentAnte = 0;
    private int currentPairPlus = 0;
    private int currentThemeIndex = 0;
    
    // Store current hands
    private ArrayList<Card> currentPlayerHand;
    private ArrayList<Card> currentDealerHand;
    
    @FXML
    public void initialize() {
        addGameMessage("Welcome to 3 Card Poker! Place your bets and click DEAL.");
        
        // Set initial dealer card backs after UI is ready and force a layout pass
        Platform.runLater(() -> {
            Image cardBack = getCardBackImage();
            if (cardBack != null) {
                dealerCard1.setImage(cardBack);
                dealerCard2.setImage(cardBack);
                dealerCard3.setImage(cardBack);

                // Ensure the ImageViews are visible and force CSS/layout to avoid any rendering timing issues
                dealerCard1.setVisible(true);
                dealerCard2.setVisible(true);
                dealerCard3.setVisible(true);

                dealerCard1.applyCss(); dealerCard2.applyCss(); dealerCard3.applyCss();

                if (dealerCard1.getParent() != null) {
                    dealerCard1.getParent().applyCss();
                    dealerCard1.getParent().requestLayout();
                }

                // Also set player's card backs initially and force layout for them too
                playerCard1.setImage(cardBack);
                playerCard2.setImage(cardBack);
                playerCard3.setImage(cardBack);
                playerCard1.setVisible(true);
                playerCard2.setVisible(true);
                playerCard3.setVisible(true);
                playerCard1.applyCss(); playerCard2.applyCss(); playerCard3.applyCss();
                if (playerCard1.getParent() != null) {
                    playerCard1.getParent().applyCss();
                    playerCard1.getParent().requestLayout();
                }

                if (dealerCard1.getScene() != null && dealerCard1.getScene().getRoot() != null) {
                    dealerCard1.getScene().getRoot().applyCss();
                    dealerCard1.getScene().getRoot().requestLayout();
                }
            } else {
                addGameMessage("ERROR: Failed to load card back image");
            }
        });
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    @FXML
    public void handleDeal() {
        // Validate bets
        String anteText = anteBetField.getText().trim();
        String pairPlusText = pairPlusBetField.getText().trim();
        
        if (anteText.isEmpty()) {
            addGameMessage("ERROR: You must place an ante bet!");
            return;
        }
        
        try {
            int ante = Integer.parseInt(anteText);
            int pairPlus = pairPlusText.isEmpty() ? 0 : Integer.parseInt(pairPlusText);
            
            if (ante < 5 || ante > 25) {
                addGameMessage("ERROR: Ante bet must be between $5 and $25");
                return;
            }
            
            if (pairPlus != 0 && (pairPlus < 5 || pairPlus > 25)) {
                addGameMessage("ERROR: Pair Plus bet must be $0 or between $5 and $25");
                return;
            }
            
            currentAnte = ante;
            currentPairPlus = pairPlus;
            
            // Disable deal button, enable play/fold
            dealButton.setDisable(true);
            anteBetField.setDisable(true);
            pairPlusBetField.setDisable(true);
            addGameMessage("Sending bets...");
            
            // Send bet to server
            PokerInfo info = new PokerInfo();
            info.setAnteBet(ante);
            info.setPairPlusBet(pairPlus);
            info.setGameAction("BET");
            
            // Perform network operations in a separate thread
            new Thread(() -> {
                try {
                    client.sendInfo(info);
                    
                    Platform.runLater(() -> {
                        addGameMessage("Bets placed: Ante=$" + ante + ", Pair Plus=$" + pairPlus);
                        addGameMessage("Dealing cards...");
                    });
                    
                    PokerInfo response = client.receiveInfo();
                    Platform.runLater(() -> {
                        // Store the hands for later use
                        currentPlayerHand = response.getPlayerHand();
                        currentDealerHand = response.getDealerHand();
                        
                        displayCards(response);
                        playButton.setDisable(false);
                        foldButton.setDisable(false);
                        playBetLabel.setText("$" + currentAnte);
                        addGameMessage(response.getMessage());
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        addGameMessage("ERROR: " + e.getMessage());
                        resetForNewHand();
                    });
                }
            }).start();
            
        } catch (NumberFormatException e) {
            addGameMessage("ERROR: Invalid bet amount");
        }
    }
    
    @FXML
    public void handlePlay() {
        addGameMessage("You chose to PLAY. Making play bet of $" + currentAnte);

        playButton.setDisable(true);
        foldButton.setDisable(true);

        // Send play decision to server
        PokerInfo info = new PokerInfo();
        info.setPlayBet(currentAnte);
        info.setGameAction("PLAY");
        info.setPlayerHand(currentPlayerHand);
        info.setDealerHand(currentDealerHand);
        info.setAnteBet(currentAnte);
        info.setPairPlusBet(currentPairPlus);

        new Thread(() -> {
            try {
                client.sendInfo(info);

                // Wait a moment for suspense
                Thread.sleep(1000);

                PokerInfo response = client.receiveInfo();

                Platform.runLater(() -> {
                    // Reveal dealer cards
                    revealDealerCards(response);

                    // Show results after a delay to let user see dealer cards
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000); // 2 second delay to view dealer cards
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Platform.runLater(() -> {
                            // Show results
                            int gameWinnings = response.getTotalWinnings();
                            totalWinnings += gameWinnings;
                            updateWinnings(totalWinnings);

                            addGameMessage("===== GAME RESULT =====");
                            addGameMessage(response.getMessage());
                            addGameMessage("This hand: " + (gameWinnings >= 0 ? "+" : "") + "$" + gameWinnings);

                            // Prefer showing a separate Win/Lose scene if available, otherwise show inline
                            if (!showResultScene(gameWinnings >= 0, gameWinnings, response.getMessage())) {
                                showResultInline(gameWinnings >= 0, gameWinnings, response.getMessage());
                            }
                        });
                    }).start();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    addGameMessage("ERROR: " + e.getMessage());
                });
            }
        }).start();
    }
    
    @FXML
    public void handleFold() {
        addGameMessage("You chose to FOLD.");
        
        playButton.setDisable(true);
        foldButton.setDisable(true);
        
        // Send fold decision to server
        PokerInfo info = new PokerInfo();
        info.setAnteBet(currentAnte);
        info.setPairPlusBet(currentPairPlus);
        info.setGameAction("FOLD");
        info.setPlayerHand(currentPlayerHand);
        info.setDealerHand(currentDealerHand);
        
        new Thread(() -> {
            try {
                client.sendInfo(info);
                PokerInfo response = client.receiveInfo();
                
                Platform.runLater(() -> {
                    // Reveal dealer cards even when folding
                    revealDealerCards(response);
                    
                    // Show results after a delay to let user see dealer cards
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000); // 2 second delay to view dealer cards
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        
                        Platform.runLater(() -> {
                            int gameLoss = response.getTotalWinnings();
                            totalWinnings += gameLoss;
                            updateWinnings(totalWinnings);
                            
                            addGameMessage("===== GAME RESULT =====");
                            addGameMessage(response.getMessage());
                            addGameMessage("Lost: $" + Math.abs(gameLoss));
                            
                            // Prefer showing a separate Win/Lose scene if available, otherwise show inline
                            if (!showResultScene(false, gameLoss, response.getMessage())) {
                                showResultInline(false, gameLoss, response.getMessage());
                            }
                        });
                    }).start();
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    addGameMessage("ERROR: " + e.getMessage());
                });
            }
        }).start();
    }
    
    @FXML
    public void handleFreshStart() {
        totalWinnings = 0;
        updateWinnings(totalWinnings);
        resetForNewHand();
        addGameMessage("Fresh start! Total winnings reset to $0.");
        
        // Send NEW_GAME to server
        PokerInfo info = new PokerInfo();
        info.setGameAction("NEW_GAME");
        
        new Thread(() -> {
            try {
                client.sendInfo(info);
                // Consume response to maintain sync
                PokerInfo response = client.receiveInfo();
                Platform.runLater(() -> {
                    if (response.getMessage() != null && !response.getMessage().isEmpty()) {
                        addGameMessage(response.getMessage());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> addGameMessage("Server sync error: " + e.getMessage()));
            }
        }).start();
    }
    
    @FXML
    public void handleChangeTheme() {
        currentThemeIndex = (currentThemeIndex + 1) % 4; // 0, 1, 2, 3
        
        clearThemes();
        
        switch (currentThemeIndex) {
            case 0:
                addGameMessage("Switched to Default Theme!");
                break;
            case 1:
                playerCard1.getScene().getRoot().getStyleClass().add("theme1-root");
                addGameMessage("Switched to Theme 1!");
                break;
            case 2:
                playerCard1.getScene().getRoot().getStyleClass().add("theme2-root");
                addGameMessage("Switched to Theme 2!");
                break;
            case 3:
                playerCard1.getScene().getRoot().getStyleClass().add("theme3-root");
                addGameMessage("Switched to Theme 3!");
                break;
        }
    }

    private void clearThemes() {
        playerCard1.getScene().getRoot().getStyleClass().removeAll("theme1-root", "theme2-root", "theme3-root", "alt-root");
    }
    
    @FXML
    public void handleExit() {
        client.disconnect();
        Platform.exit();
    }
    
    private void displayCards(PokerInfo info) {
        ArrayList<Card> playerHand = info.getPlayerHand();
        
        if (playerHand != null && playerHand.size() == 3) {
            playerCard1.setImage(getCardImage(playerHand.get(0)));
            playerCard2.setImage(getCardImage(playerHand.get(1)));
            playerCard3.setImage(getCardImage(playerHand.get(2)));
        }
    }
    
    private void revealDealerCards(PokerInfo info) {
        ArrayList<Card> dealerHand = info.getDealerHand();
        
        if (dealerHand != null && dealerHand.size() == 3) {
            dealerCard1.setImage(getCardImage(dealerHand.get(0)));
            dealerCard2.setImage(getCardImage(dealerHand.get(1)));
            dealerCard3.setImage(getCardImage(dealerHand.get(2)));
        }
    }
    
    private Image getCardImage(Card card) {
        String cardName;
        
        // Map card value to name
        switch (card.getValue()) {
            case 14: cardName = "ace"; break;
            case 13: cardName = "king"; break;
            case 12: cardName = "queen"; break;
            case 11: cardName = "jack"; break;
            default: cardName = String.valueOf(card.getValue());
        }
        
        // Map suit to name
        String suitName;
        switch (card.getSuit()) {
            case 'H': suitName = "hearts"; break;
            case 'D': suitName = "diamonds"; break;
            case 'C': suitName = "clubs"; break;
            case 'S': suitName = "spades"; break;
            default: suitName = "clubs";
        }
        
        String imagePath = "/front-cards-image/" + cardName + "_of_" + suitName + ".png";
        try {
            java.io.InputStream stream = getClass().getResourceAsStream(imagePath);
            if (stream == null) {
                addGameMessage("ERROR: Image not found: " + imagePath);
                return null;
            }
            Image img = new Image(stream);
            return img;
        } catch (Exception e) {
            addGameMessage("ERROR: Could not load card image: " + imagePath + " - " + e.getMessage());
            return null;
        }
    }
    
    private Image getCardBackImage() {
        try {
            // Try loading backcard.png from image folder
            java.io.InputStream stream = getClass().getResourceAsStream("/image/backcard.png");
            
            if (stream != null) {
                Image img = new Image(stream);
                return img;
            }
            
            addGameMessage("ERROR: Card back image not found: /image/backcard.png");
            return null;
        } catch (Exception e) {
            addGameMessage("ERROR: Could not load card back: " + e.getMessage());
            return null;
        }
    }
    
    private void addGameMessage(String message) {
        gameInfoListView.getItems().add(message);
        gameInfoListView.scrollTo(gameInfoListView.getItems().size() - 1);
    }
    
    private void updateWinnings(int amount) {
        totalWinningsLabel.setText("Total Winnings: $" + amount);
        if (playerWinningsLabel != null) {
            playerWinningsLabel.setText("Your Winnings: $" + amount);
        }
    }
    
    private void resetForNewHand() {
        dealButton.setDisable(false);
        playButton.setDisable(true);
        foldButton.setDisable(true);
        anteBetField.setDisable(false);
        pairPlusBetField.setDisable(false);
        anteBetField.clear();
        pairPlusBetField.clear();
        playBetLabel.setText("(= Ante)");
        
        // Show player and dealer card backs on the JavaFX thread and force layout
        Platform.runLater(() -> {
            Image cardBack = getCardBackImage();

            // Player backs (reset visible cards)
            playerCard1.setImage(cardBack);
            playerCard2.setImage(cardBack);
            playerCard3.setImage(cardBack);

            // Dealer backs
            dealerCard1.setImage(cardBack);
            dealerCard2.setImage(cardBack);
            dealerCard3.setImage(cardBack);

            // Make sure they are visible and refresh CSS/layout
            playerCard1.setVisible(true); playerCard2.setVisible(true); playerCard3.setVisible(true);
            dealerCard1.setVisible(true); dealerCard2.setVisible(true); dealerCard3.setVisible(true);

            playerCard1.applyCss(); playerCard2.applyCss(); playerCard3.applyCss();
            dealerCard1.applyCss(); dealerCard2.applyCss(); dealerCard3.applyCss();

            if (playerCard1.getParent() != null) {
                playerCard1.getParent().applyCss();
                playerCard1.getParent().requestLayout();
            }
            if (dealerCard1.getParent() != null) {
                dealerCard1.getParent().applyCss();
                dealerCard1.getParent().requestLayout();
            }
        });
    }
    
    private void showResultInline(boolean won, int amount, String message) {
        // Disable game controls while showing results
        dealButton.setDisable(true);
        playButton.setDisable(true);
        foldButton.setDisable(true);
        anteBetField.setDisable(true);
        pairPlusBetField.setDisable(true);
        
        // Set result title and styling
        if (won) {
            resultTitleLabel.setText("YOU WON!");
            resultTitleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        } else {
            resultTitleLabel.setText("YOU LOST");
            resultTitleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #f44336;");
        }
        
        // Set amount with styling
        String amountText = (amount >= 0 ? "+" : "") + "$" + Math.abs(amount);
        resultAmountLabel.setText(amountText);
        resultAmountLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: " + 
                                   (won ? "#4CAF50;" : "#f44336;"));
        
        // Set message
        resultMessageArea.setText(message);
        resultMessageArea.setStyle("-fx-font-size: 14px;");
        
        // Show the result panel
        resultPanel.setVisible(true);
        resultPanel.setManaged(true);
    }

    /**
     * Attempt to show a separate Win/Lose scene using winlose.fxml.
     * Returns true if the scene was shown, false if the FXML was not available or failed to load.
     */
    private boolean showResultScene(boolean won, int amount, String message) {
        try {
            java.net.URL fxmlUrl = getClass().getResource("/winlose.fxml");
            if (fxmlUrl == null) return false;

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Object ctrl = loader.getController();
            if (ctrl instanceof WinLoseController) {
                WinLoseController wlc = (WinLoseController) ctrl;
                wlc.setClient(client);
                wlc.setReturnController(this);
                wlc.setGameResult(won, amount, message);
            }

            // Show result in a modal dialog so the gameplay scene (and its info) remains intact
            Stage owner = (Stage) dealButton.getScene().getWindow();
            Stage dialog = new Stage();
            dialog.initOwner(owner);
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root, 900, 600);
            dialog.setScene(scene);
            dialog.setTitle("Game Result");
            // Show dialog and wait until user closes it (Play Again or Exit)
            dialog.showAndWait();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    @FXML
    public void handlePlayAgain() {
        // Hide result panel
        resultPanel.setVisible(false);
        resultPanel.setManaged(false);

        // Reset for new hand
        resetForNewHand();
        addGameMessage("----- New hand starting -----");
        
        // Send NEW_HAND to server
        PokerInfo info = new PokerInfo();
        info.setGameAction("NEW_HAND");
        
        new Thread(() -> {
            try {
                client.sendInfo(info);
                // Consume response to maintain sync
                PokerInfo response = client.receiveInfo();
                Platform.runLater(() -> {
                    if (response.getMessage() != null && !response.getMessage().isEmpty()) {
                        addGameMessage(response.getMessage());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> addGameMessage("Server sync error: " + e.getMessage()));
            }
        }).start();
    }
    
    public void returnFromResult() {
        resetForNewHand();
        addGameMessage("----- New hand starting -----");
        
        // Send NEW_HAND to server to log the action
        PokerInfo info = new PokerInfo();
        info.setGameAction("NEW_HAND");
        
        new Thread(() -> {
            try {
                client.sendInfo(info);
                // Consume response to maintain sync
                PokerInfo response = client.receiveInfo();
                Platform.runLater(() -> {
                    if (response.getMessage() != null && !response.getMessage().isEmpty()) {
                        addGameMessage(response.getMessage());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> addGameMessage("Server sync error: " + e.getMessage()));
            }
        }).start();
    }
    
    public int getTotalWinnings() {
        return totalWinnings;
    }
}
