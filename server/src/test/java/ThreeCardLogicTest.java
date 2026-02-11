import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class ThreeCardLogicTest {
    
    // Helper method to create a hand
    private ArrayList<Card> createHand(int v1, char s1, int v2, char s2, int v3, char s3) {
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(s1, v1));
        hand.add(new Card(s2, v2));
        hand.add(new Card(s3, v3));
        return hand;
    }
    
    @Test
    public void testStraightFlush() {
        ArrayList<Card> hand = createHand(5, 'H', 6, 'H', 7, 'H');
        assertEquals(5, ThreeCardLogic.evalHand(hand), "Should be a Straight Flush");
    }
    
    @Test
    public void testThreeOfAKind() {
        ArrayList<Card> hand = createHand(9, 'H', 9, 'D', 9, 'C');
        assertEquals(4, ThreeCardLogic.evalHand(hand), "Should be Three of a Kind");
    }
    
    @Test
    public void testStraight() {
        ArrayList<Card> hand = createHand(8, 'H', 9, 'D', 10, 'C');
        assertEquals(3, ThreeCardLogic.evalHand(hand), "Should be a Straight");
    }
    
    @Test
    public void testStraightAce23() {
        ArrayList<Card> hand = createHand(14, 'H', 2, 'D', 3, 'C');
        assertEquals(3, ThreeCardLogic.evalHand(hand), "Should be a Straight (A-2-3)");
    }
    
    @Test
    public void testFlush() {
        ArrayList<Card> hand = createHand(2, 'S', 5, 'S', 9, 'S');
        assertEquals(2, ThreeCardLogic.evalHand(hand), "Should be a Flush");
    }
    
    @Test
    public void testPair() {
        ArrayList<Card> hand = createHand(7, 'H', 7, 'D', 3, 'C');
        assertEquals(1, ThreeCardLogic.evalHand(hand), "Should be a Pair");
    }
    
    @Test
    public void testHighCard() {
        ArrayList<Card> hand = createHand(2, 'H', 5, 'D', 9, 'C');
        assertEquals(0, ThreeCardLogic.evalHand(hand), "Should be High Card");
    }
    
    @Test
    public void testPairPlusWinningsStraightFlush() {
        ArrayList<Card> hand = createHand(5, 'H', 6, 'H', 7, 'H');
        assertEquals(200, ThreeCardLogic.evalPPWinnings(hand, 5), "Straight Flush should pay 40 to 1");
    }
    
    @Test
    public void testPairPlusWinningsThreeOfAKind() {
        ArrayList<Card> hand = createHand(9, 'H', 9, 'D', 9, 'C');
        assertEquals(300, ThreeCardLogic.evalPPWinnings(hand, 10), "Three of a Kind should pay 30 to 1");
    }
    
    @Test
    public void testPairPlusWinningsStraight() {
        ArrayList<Card> hand = createHand(8, 'H', 9, 'D', 10, 'C');
        assertEquals(60, ThreeCardLogic.evalPPWinnings(hand, 10), "Straight should pay 6 to 1");
    }
    
    @Test
    public void testPairPlusWinningsFlush() {
        ArrayList<Card> hand = createHand(2, 'S', 5, 'S', 9, 'S');
        assertEquals(30, ThreeCardLogic.evalPPWinnings(hand, 10), "Flush should pay 3 to 1");
    }
    
    @Test
    public void testPairPlusWinningsPair() {
        ArrayList<Card> hand = createHand(7, 'H', 7, 'D', 3, 'C');
        assertEquals(5, ThreeCardLogic.evalPPWinnings(hand, 5), "Pair should pay 1 to 1");
    }
    
    @Test
    public void testPairPlusWinningsLoss() {
        ArrayList<Card> hand = createHand(2, 'H', 5, 'D', 9, 'C');
        assertEquals(0, ThreeCardLogic.evalPPWinnings(hand, 10), "High Card should lose");
    }
    
    @Test
    public void testCompareHandsDealerNotQualified() {
        ArrayList<Card> dealer = createHand(2, 'H', 5, 'D', 9, 'C');
        ArrayList<Card> player = createHand(3, 'H', 6, 'D', 10, 'C');
        assertEquals(0, ThreeCardLogic.compareHands(dealer, player), "Dealer doesn't qualify (no Queen high)");
    }
    
    @Test
    public void testCompareHandsDealerQualified() {
        ArrayList<Card> dealer = createHand(12, 'H', 5, 'D', 9, 'C');
        ArrayList<Card> player = createHand(3, 'H', 6, 'D', 10, 'C');
        assertEquals(1, ThreeCardLogic.compareHands(dealer, player), "Dealer qualifies with Queen high");
    }
    
    @Test
    public void testCompareHandsPlayerWins() {
        ArrayList<Card> dealer = createHand(12, 'H', 5, 'D', 9, 'C');
        ArrayList<Card> player = createHand(7, 'H', 7, 'D', 3, 'C');
        assertEquals(2, ThreeCardLogic.compareHands(dealer, player), "Player wins with pair");
    }
    
    @Test
    public void testCompareHandsDealerWins() {
        ArrayList<Card> dealer = createHand(7, 'H', 7, 'D', 12, 'C'); // Pair of 7s with Queen
        ArrayList<Card> player = createHand(12, 'H', 5, 'D', 9, 'C'); // High card Queen
        assertEquals(1, ThreeCardLogic.compareHands(dealer, player), "Dealer wins with pair");
    }
    
    @Test
    public void testCompareHandsPlayerHigher() {
        ArrayList<Card> dealer = createHand(12, 'H', 10, 'D', 9, 'C');
        ArrayList<Card> player = createHand(14, 'H', 10, 'D', 9, 'C');
        assertEquals(2, ThreeCardLogic.compareHands(dealer, player), "Player wins with Ace high vs Queen high");
    }
    
    @Test
    public void testCompareHandsTie() {
        ArrayList<Card> dealer = createHand(12, 'H', 10, 'D', 9, 'C');
        ArrayList<Card> player = createHand(12, 'S', 10, 'H', 9, 'S');
        assertEquals(3, ThreeCardLogic.compareHands(dealer, player), "Should be a tie");
    }
    
    @Test
    public void testNullHand() {
        assertEquals(-1, ThreeCardLogic.evalHand(null), "Null hand should return -1");
    }
    
    @Test
    public void testInvalidHandSize() {
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card('H', 5));
        hand.add(new Card('D', 6));
        assertEquals(-1, ThreeCardLogic.evalHand(hand), "Hand with 2 cards should return -1");
    }
    
    // Additional tests for evalTotalWinnings method
    
    @Test
    public void testTotalWinningsPlayerWinsWithPairPlus() {
        // Player wins main game and pair plus
        int total = ThreeCardLogic.evalTotalWinnings(10, 10, 5, 15, 2);
        assertEquals(60, total, "Player wins both should calculate correctly");
    }
    
    @Test
    public void testTotalWinningsDealerWinsPlayerLosesPairPlus() {
        // Dealer wins main game, player loses pair plus
        int total = ThreeCardLogic.evalTotalWinnings(10, 10, 5, 0, 1);
        assertEquals(-25, total, "Dealer wins and PP loss should calculate correctly");
    }
    
    @Test
    public void testTotalWinningsDealerNotQualified() {
        int total = ThreeCardLogic.evalTotalWinnings(10, 10, 5, 0, 0);
        assertEquals(5, total, "Dealer not qualified should return play bet");
    }
    
    @Test
    public void testTotalWinningsTie() {
        int total = ThreeCardLogic.evalTotalWinnings(10, 10, 0, 0, 3);
        assertEquals(20, total, "Tie should return ante and play bets");
    }
    
    @Test
    public void testTotalWinningsPlayerWinsNoPairPlus() {
        int total = ThreeCardLogic.evalTotalWinnings(10, 10, 0, 0, 2);
        assertEquals(40, total, "Player wins without PP should calculate correctly");
    }
    
    @Test
    public void testTotalWinningsDealerWinsNoPairPlus() {
        int total = ThreeCardLogic.evalTotalWinnings(10, 10, 0, 0, 1);
        assertEquals(-20, total, "Dealer wins without PP should calculate correctly");
    }
    
    @Test
    public void testTotalWinningsMinimumBet() {
        int total = ThreeCardLogic.evalTotalWinnings(5, 5, 5, 5, 2);
        assertEquals(30, total, "Minimum bet should calculate correctly");
    }
    
    @Test
    public void testTotalWinningsMaximumBet() {
        int total = ThreeCardLogic.evalTotalWinnings(25, 25, 25, 1000, 2);
        assertEquals(1125, total, "Maximum bet should calculate correctly");
    }
    
    // Additional edge case tests
    
    @Test
    public void testStraightQueenKingAce() {
        ArrayList<Card> hand = createHand(12, 'H', 13, 'D', 14, 'C');
        assertEquals(3, ThreeCardLogic.evalHand(hand), "Q-K-A should be a Straight");
    }
    
    @Test
    public void testNotStraightAce2King() {
        ArrayList<Card> hand = createHand(14, 'H', 2, 'D', 13, 'C');
        assertEquals(0, ThreeCardLogic.evalHand(hand), "A-2-K should not be a Straight");
    }
    
    @Test
    public void testPairPlusZeroBet() {
        ArrayList<Card> hand = createHand(5, 'H', 6, 'H', 7, 'H');
        assertEquals(0, ThreeCardLogic.evalPPWinnings(hand, 0), "Zero bet should return 0 winnings");
    }
    
    @Test
    public void testDealerQualifiesWithKing() {
        ArrayList<Card> dealer = createHand(13, 'H', 5, 'D', 9, 'C');
        ArrayList<Card> player = createHand(3, 'H', 6, 'D', 10, 'C');
        assertEquals(1, ThreeCardLogic.compareHands(dealer, player), "Dealer qualifies with King high");
    }
    
    @Test
    public void testDealerQualifiesWithAce() {
        ArrayList<Card> dealer = createHand(14, 'H', 5, 'D', 9, 'C');
        ArrayList<Card> player = createHand(3, 'H', 6, 'D', 10, 'C');
        assertEquals(1, ThreeCardLogic.compareHands(dealer, player), "Dealer qualifies with Ace high");
    }
    
    @Test
    public void testCompareHandsBothStraightFlush() {
        ArrayList<Card> dealer = createHand(10, 'H', 11, 'H', 12, 'H'); // 10-J-Q straight flush
        ArrayList<Card> player = createHand(8, 'D', 9, 'D', 10, 'D'); // 8-9-10 straight flush
        assertEquals(1, ThreeCardLogic.compareHands(dealer, player), "Dealer's higher straight flush wins");
    }
    
    @Test
    public void testCompareHandsBothThreeOfAKind() {
        ArrayList<Card> dealer = createHand(12, 'H', 12, 'D', 12, 'C'); // Three Queens
        ArrayList<Card> player = createHand(10, 'H', 10, 'D', 10, 'C'); // Three 10s
        assertEquals(1, ThreeCardLogic.compareHands(dealer, player), "Dealer's higher three of a kind wins");
    }
    
    @Test
    public void testCompareHandsBothPairs() {
        ArrayList<Card> dealer = createHand(8, 'H', 8, 'D', 12, 'C');
        ArrayList<Card> player = createHand(8, 'S', 8, 'C', 14, 'H');
        assertEquals(2, ThreeCardLogic.compareHands(dealer, player), "Same pair, player's higher kicker wins");
    }
    
    @Test
    public void testCompareHandsHighCardComparison() {
        ArrayList<Card> dealer = createHand(12, 'H', 10, 'D', 8, 'C');
        ArrayList<Card> player = createHand(12, 'S', 10, 'H', 7, 'S');
        assertEquals(1, ThreeCardLogic.compareHands(dealer, player), "Same high cards, dealer's third card wins");
    }
    
    @Test
    public void testFlushDifferentValues() {
        ArrayList<Card> hand = createHand(14, 'S', 2, 'S', 7, 'S');
        assertEquals(2, ThreeCardLogic.evalHand(hand), "Flush with different values");
    }
    
    @Test
    public void testPairWithDifferentPositions() {
        ArrayList<Card> hand1 = createHand(5, 'H', 5, 'D', 9, 'C');
        ArrayList<Card> hand2 = createHand(5, 'H', 9, 'D', 5, 'C');
        ArrayList<Card> hand3 = createHand(9, 'H', 5, 'D', 5, 'C');
        assertEquals(1, ThreeCardLogic.evalHand(hand1), "Pair in first two positions");
        assertEquals(1, ThreeCardLogic.evalHand(hand2), "Pair in first and third positions");
        assertEquals(1, ThreeCardLogic.evalHand(hand3), "Pair in last two positions");
    }
}
