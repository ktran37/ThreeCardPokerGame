import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class PokerInfoTest {
    private PokerInfo pokerInfo;

    @BeforeEach
    void setUp() {
        pokerInfo = new PokerInfo();
    }

    @Test
    void testInitialPlayerHand() {
        assertNotNull(pokerInfo.getPlayerHand(), "Player hand should not be null");
        assertEquals(0, pokerInfo.getPlayerHand().size(), "Player hand should be empty initially");
    }

    @Test
    void testInitialDealerHand() {
        assertNotNull(pokerInfo.getDealerHand(), "Dealer hand should not be null");
        assertEquals(0, pokerInfo.getDealerHand().size(), "Dealer hand should be empty initially");
    }

    @Test
    void testInitialAnteBet() {
        assertEquals(0, pokerInfo.getAnteBet(), "Ante bet should be 0 initially");
    }

    @Test
    void testInitialPairPlusBet() {
        assertEquals(0, pokerInfo.getPairPlusBet(), "Pair plus bet should be 0 initially");
    }

    @Test
    void testSetAnteBet() {
        pokerInfo.setAnteBet(50);
        assertEquals(50, pokerInfo.getAnteBet(), "Ante bet should be 50");
    }

    @Test
    void testSetPairPlusBet() {
        pokerInfo.setPairPlusBet(25);
        assertEquals(25, pokerInfo.getPairPlusBet(), "Pair plus bet should be 25");
    }

    @Test
    void testSetPlayerHand() {
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card('H', 14));
        hand.add(new Card('H', 13));
        hand.add(new Card('H', 12));
        
        pokerInfo.setPlayerHand(hand);
        assertEquals(3, pokerInfo.getPlayerHand().size(), "Player hand should have 3 cards");
    }

    @Test
    void testSetDealerHand() {
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card('D', 10));
        hand.add(new Card('D', 9));
        hand.add(new Card('D', 8));
        
        pokerInfo.setDealerHand(hand);
        assertEquals(3, pokerInfo.getDealerHand().size(), "Dealer hand should have 3 cards");
    }

    @Test
    void testSetGameAction() {
        pokerInfo.setGameAction("PLAY");
        assertEquals("PLAY", pokerInfo.getGameAction(), "Game action should be 'PLAY'");
    }

    @Test
    void testSetTotalWinnings() {
        pokerInfo.setTotalWinnings(100);
        assertEquals(100, pokerInfo.getTotalWinnings(), "Total winnings should be 100");
    }
}
