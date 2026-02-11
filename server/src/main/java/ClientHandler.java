import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Deck deck;
    private int clientId;
    private Server server;
    private boolean running;
    private boolean deckFresh;
    
    public ClientHandler(Socket socket, int clientId, Server server) {
        this.socket = socket;
        this.clientId = clientId;
        this.server = server;
        this.deck = new Deck();
        this.running = true;
        this.deckFresh = true;
        deck.shuffle();
    }
    
    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            server.addMessage("Client #" + clientId + " connected");
            
            while (running) {
                PokerInfo info = (PokerInfo) in.readObject();
                
                if (info == null) break;
                
                String action = info.getGameAction();
                // Normalize action to uppercase to handle variations like "new_game", "New_Hand"
                if (action != null) {
                    action = action.toUpperCase();
                }
                
                server.addMessage("Client #" + clientId + " action: " + action);
                
                if ("BET".equals(action)) {
                    handleBet(info);
                } else if ("PLAY".equals(action)) {
                    handlePlay(info);
                } else if ("FOLD".equals(action)) {
                    handleFold(info);
                } else if ("NEW_GAME".equals(action)) {
                    handleNewGame(info);
                } else if ("NEW_HAND".equals(action)) {
                    handleNewHand(info);
                } else {
                    server.addMessage("Client #" + clientId + " sent unknown action: " + action);
                }
            }
            
        } catch (EOFException e) {
            server.addMessage("Client #" + clientId + " disconnected");
        } catch (Exception e) {
            server.addMessage("Client #" + clientId + " error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }
    
    private void handleBet(PokerInfo info) throws IOException {
        if (!deckFresh) {
            server.addMessage("Client #" + clientId + " is playing another hand");
            deck = new Deck();
            deck.shuffle();
        }
        deckFresh = false;

        int ante = info.getAnteBet();
        int pairPlus = info.getPairPlusBet();
        
        server.addMessage("Client #" + clientId + " bet - Ante: $" + ante + 
                         ", Pair Plus: $" + pairPlus);
        
        // Deal cards
        ArrayList<Card> playerHand = new ArrayList<>();
        ArrayList<Card> dealerHand = new ArrayList<>();
        
        for (int i = 0; i < 3; i++) {
            playerHand.add(deck.dealCard());
            dealerHand.add(deck.dealCard());
        }
        
        info.setPlayerHand(playerHand);
        info.setDealerHand(dealerHand);
        info.setGameAction("DEAL");
        info.setMessage("Cards dealt! Choose to Play or Fold.");
        
        out.writeObject(info);
        out.flush();
        out.reset();
    }
    
    private void handlePlay(PokerInfo info) throws IOException {
        int ante = info.getAnteBet();
        int pairPlus = info.getPairPlusBet();
        int playBet = info.getPlayBet();
        
        server.addMessage("Client #" + clientId + " plays with bet: $" + playBet);
        
        ArrayList<Card> playerHand = info.getPlayerHand();
        ArrayList<Card> dealerHand = info.getDealerHand();
        
        // Evaluate Pair Plus
        int ppWinnings = ThreeCardLogic.evalPPWinnings(playerHand, pairPlus);
        
        // Compare hands
        int result = ThreeCardLogic.compareHands(dealerHand, playerHand);
        
        // Calculate total winnings
        int totalWinnings = ThreeCardLogic.evalTotalWinnings(ante, playBet, pairPlus, ppWinnings, result);
        
        StringBuilder message = new StringBuilder();
        
        // Pair Plus evaluation
        if (pairPlus > 0) {
            if (ppWinnings > 0) {
                message.append("Pair Plus wins $").append(ppWinnings).append("! ");
            } else {
                message.append("Pair Plus loses. ");
            }
        }
        
        // Main game evaluation
        if (result == 0) {
            // Dealer doesn't qualify
            message.append("Dealer does not have Queen high or better. ");
            message.append("Play bet returned.");
        } else if (result == 1) {
            // Dealer wins
            message.append("Dealer wins. ");
            message.append("You lose ante and play bets.");
        } else if (result == 2) {
            // Player wins
            message.append("You beat the dealer! You win!");
        } else {
            // Tie
            message.append("Push - It's a tie!");
        }
        
        info.setTotalWinnings(totalWinnings);
        info.setMessage(message.toString());
        info.setGameAction("RESULT");
        info.setDealerHand(dealerHand);  // Send dealer hand back to client
        info.setPlayerHand(playerHand);  // Send player hand back too
        
        server.addMessage("Client #" + clientId + " - " + message.toString());
        server.addMessage("Client #" + clientId + " hand result: " + 
                         (totalWinnings >= 0 ? "Won" : "Lost") + " $" + Math.abs(totalWinnings));
        
        out.writeObject(info);
        out.flush();
        out.reset();
    }
    
    private void handleFold(PokerInfo info) throws IOException {
        int ante = info.getAnteBet();
        int pairPlus = info.getPairPlusBet();
        
        server.addMessage("Client #" + clientId + " folds");
        server.addMessage("Client #" + clientId + " lost $" + (ante + pairPlus) + " (ante + pair plus)");
        
        int totalLoss = -(ante + pairPlus);
        
        ArrayList<Card> playerHand = info.getPlayerHand();
        ArrayList<Card> dealerHand = info.getDealerHand();
        
        info.setTotalWinnings(totalLoss);
        info.setMessage("You folded. Lost ante and pair plus bets.");
        info.setGameAction("RESULT");
        info.setDealerHand(dealerHand);  // Send dealer hand back to client
        info.setPlayerHand(playerHand);  // Send player hand back too
        
        out.writeObject(info);
        out.flush();
        out.reset();
    }
    
    private void handleNewGame(PokerInfo info) throws IOException {
        deck = new Deck();
        deck.shuffle();
        server.addMessage("Client #" + clientId + " started a new game (fresh start)");
        deckFresh = true;
        
        info.setGameAction("NEW_GAME");
        info.setMessage("New game started");
        out.writeObject(info);
        out.flush();
        out.reset();
    }

    private void handleNewHand(PokerInfo info) throws IOException {
        server.addMessage("Client #" + clientId + " is playing another hand");

        deck = new Deck();
        deck.shuffle();
        deckFresh = true;
        
        info.setGameAction("NEW_HAND");
        info.setMessage("New hand started");
        out.writeObject(info);
        out.flush();
        out.reset();
    }

    
    private void cleanup() {
        try {
            running = false;
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            server.removeClient(this);
        } catch (IOException e) {
            // Cleanup errors can be safely ignored
        }
    }
    
    public void stop() {
        running = false;
        cleanup();
    }
}
