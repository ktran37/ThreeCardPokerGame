import javafx.scene.image.Image;


public interface GamePlayInterface {
    // Lifecycle / wiring
    void initialize();
    void setClient(Client client);

    // User actions
    void handleDeal();
    void handlePlay();
    void handleFold();

    // Result flow
    void returnFromResult();
    void resetForNewHand();

    // Rendering helpers
    void displayCards(PokerInfo info);
    void revealDealerCards(PokerInfo info);
    Image getCardImage(Card card);
    Image getCardBackImage();

    // Result presentation
    boolean showResultScene(PokerInfo info);
    void showResultInline(PokerInfo info);
}
