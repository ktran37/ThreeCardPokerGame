
public interface WelcomeInterface {
    // Lifecycle
    void initialize();

    // Connection flow
    void handleConnect();
    void connectWithRetry(int maxAttempts);

    // Helpers / accessors
    String getIp();
    int getPort();
    void setMessage(String message);
}
