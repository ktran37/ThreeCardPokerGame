import java.io.Serializable;
import java.util.ArrayList;

public class PokerInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;
    private int anteBet;
    private int pairPlusBet;
    private int playBet;
    private int totalWinnings;
    private String message;
    private String gameAction; // "BET", "DEAL", "PLAY", "FOLD", "RESULT", "NEW_GAME"
    
    public PokerInfo() {
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
        anteBet = 0;
        pairPlusBet = 0;
        playBet = 0;
        totalWinnings = 0;
        message = "";
        gameAction = "";
    }
    
    // Getters and Setters
    public ArrayList<Card> getPlayerHand() { return playerHand; }
    public void setPlayerHand(ArrayList<Card> playerHand) { this.playerHand = playerHand; }
    
    public ArrayList<Card> getDealerHand() { return dealerHand; }
    public void setDealerHand(ArrayList<Card> dealerHand) { this.dealerHand = dealerHand; }
    
    public int getAnteBet() { return anteBet; }
    public void setAnteBet(int anteBet) { this.anteBet = anteBet; }
    
    public int getPairPlusBet() { return pairPlusBet; }
    public void setPairPlusBet(int pairPlusBet) { this.pairPlusBet = pairPlusBet; }
    
    public int getPlayBet() { return playBet; }
    public void setPlayBet(int playBet) { this.playBet = playBet; }
    
    public int getTotalWinnings() { return totalWinnings; }
    public void setTotalWinnings(int totalWinnings) { this.totalWinnings = totalWinnings; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getGameAction() { return gameAction; }
    public void setGameAction(String gameAction) { this.gameAction = gameAction; }
}
