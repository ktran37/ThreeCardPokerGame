import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String ipAddress;
    private int port;
    private boolean connected;
    
    public Client(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.connected = false;
    }
    
    public boolean connect() {
        try {
            socket = new Socket(ipAddress, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;
            return true;
        } catch (IOException e) {
            // Suppress repeated stderr spam; caller will handle user feedback
            connected = false;
            return false;
        }
    }
    
    public void disconnect() {
        try {
            connected = false;
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            // Ignore disconnect errors
        }
    }
    
    public void sendInfo(PokerInfo info) throws IOException {
        if (!connected) {
            throw new IOException("Not connected to server");
        }
        out.writeObject(info);
        out.flush();
    }
    
    public PokerInfo receiveInfo() throws IOException, ClassNotFoundException {
        if (!connected) {
            throw new IOException("Not connected to server");
        }
        return (PokerInfo) in.readObject();
    }
    
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }
}
