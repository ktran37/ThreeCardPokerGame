import javafx.application.Platform;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private ServerSocket serverSocket;
    private List<ClientHandler> clientHandlers;
    private boolean running;
    private int clientCounter;
    private ServerController controller;
    private Thread serverThread;
    private static final int MAX_CLIENTS = 8;
    
    public Server(int port, ServerController controller) {
        this.port = port;
        this.controller = controller;
        this.clientHandlers = new ArrayList<>();
        this.running = false;
        this.clientCounter = 0;
    }
    
    public void start() {
        if (running) return;
        
        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                running = true;
                addMessage("Server started on port " + port);
                
                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        
                        if (clientHandlers.size() >= MAX_CLIENTS) {
                            addMessage("Connection rejected: Server full (Max " + MAX_CLIENTS + " clients)");
                            clientSocket.close();
                            continue;
                        }
                        
                        clientCounter++;
                        String remote = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
                        addMessage("Accepted connection #" + clientCounter + " from " + remote);
                        
                        ClientHandler handler = new ClientHandler(clientSocket, clientCounter, this);
                        clientHandlers.add(handler);
                        
                        Thread clientThread = new Thread(handler);
                        clientThread.start();
                        
                        updateClientCount();
                        
                    } catch (IOException e) {
                        if (running) {
                            addMessage("Error accepting client: " + e.getMessage());
                        }
                    }
                }
                
            } catch (IOException e) {
                addMessage("Error starting server: " + e.getMessage());
            }
        });
        
        serverThread.start();
    }
    
    public void stop() {
        if (!running) return;
        
        running = false;
        
        // Close all client connections
        for (ClientHandler handler : new ArrayList<>(clientHandlers)) {
            handler.stop();
        }
        clientHandlers.clear();
        
        // Close server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            addMessage("Error closing server: " + e.getMessage());
        }
        
        addMessage("Server stopped");
        updateClientCount();
    }
    
    public void addMessage(String message) {
        Platform.runLater(() -> controller.addMessage(message));
    }
    
    public void removeClient(ClientHandler handler) {
        clientHandlers.remove(handler);
        updateClientCount();
        addMessage("Client disconnected");
    }
    
    private void updateClientCount() {
        int count = clientHandlers.size();
        Platform.runLater(() -> controller.updateClientCount(count));
    }
    
    public int getClientCount() {
        return clientHandlers.size();
    }
    
    public boolean isRunning() {
        return running;
    }
}
