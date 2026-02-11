import java.util.ArrayList;
import java.util.Collections;

public class ThreeCardLogic {
    
    // Hand values: 0=High Card, 1=Pair, 2=Flush, 3=Straight, 4=Three of a Kind, 5=Straight Flush
    
    /**
     * Evaluates a 3-card poker hand
     * @param hand ArrayList of 3 Card objects
     * @return int representing hand type (0-5)
     */
    public static int evalHand(ArrayList<Card> hand) {
        if (hand == null || hand.size() != 3) {
            return -1;
        }
        
        if (isStraightFlush(hand)) return 5;
        if (isThreeOfAKind(hand)) return 4;
        if (isStraight(hand)) return 3;
        if (isFlush(hand)) return 2;
        if (isPair(hand)) return 1;
        return 0; // High card
    }
    
    /**
     * Evaluates Pair Plus winnings
     * @param hand Player's hand
     * @param bet Pair Plus bet amount
     * @return Winnings (0 if loss, otherwise bet * multiplier)
     */
    public static int evalPPWinnings(ArrayList<Card> hand, int bet) {
        if (bet == 0) return 0;
        
        int handValue = evalHand(hand);
        
        switch (handValue) {
            case 5: return bet * 40; // Straight Flush
            case 4: return bet * 30; // Three of a Kind
            case 3: return bet * 6;  // Straight
            case 2: return bet * 3;  // Flush
            case 1: return bet * 1;  // Pair
            default: return 0;       // Loss
        }
    }
    
    /**
     * Calculates total winnings based on bets and game result
     * @param ante Ante bet
     * @param play Play bet
     * @param pairPlus Pair Plus bet
     * @param ppWinnings Pair Plus winnings
     * @param compareResult Result of hand comparison (0=dealer no qualify, 1=dealer wins, 2=player wins, 3=tie)
     * @return Total winnings value
     */
    public static int evalTotalWinnings(int ante, int play, int pairPlus, int ppWinnings, int compareResult) {
        int totalWinnings = 0;
        
        // Pair Plus evaluation
        if (pairPlus > 0) {
            if (ppWinnings > 0) {
                totalWinnings += ppWinnings + pairPlus;
            } else {
                totalWinnings -= pairPlus;
            }
        }
        
        // Main game evaluation
        if (compareResult == 0) {
            // Dealer doesn't qualify
            totalWinnings += play;
        } else if (compareResult == 1) {
            // Dealer wins
            totalWinnings -= (ante + play);
        } else if (compareResult == 2) {
            // Player wins
            totalWinnings += (ante + play) * 2;
        } else {
            // Tie
            totalWinnings += (ante + play);
        }
        
        return totalWinnings;
    }
    
    /**
     * Compares dealer and player hands
     * @param dealer Dealer's hand
     * @param player Player's hand
     * @return 0=dealer doesn't qualify, 1=dealer wins, 2=player wins, 3=tie
     */
    public static int compareHands(ArrayList<Card> dealer, ArrayList<Card> player) {
        // Check if dealer qualifies (Queen high or better)
        if (!dealerQualifies(dealer)) {
            return 0;
        }
        
        int dealerValue = evalHand(dealer);
        int playerValue = evalHand(player);
        
        // Higher hand type wins
        if (dealerValue > playerValue) {
            return 1; // Dealer wins
        } else if (playerValue > dealerValue) {
            return 2; // Player wins
        }
        
        // Same hand type, compare high cards
        int comparison = compareHighCards(dealer, player);
        if (comparison > 0) {
            return 1; // Dealer wins
        } else if (comparison < 0) {
            return 2; // Player wins
        }
        
        return 3; // Tie
    }
    
    private static boolean isStraightFlush(ArrayList<Card> hand) {
        return isStraight(hand) && isFlush(hand);
    }
    
    private static boolean isThreeOfAKind(ArrayList<Card> hand) {
        return hand.get(0).getValue() == hand.get(1).getValue() &&
               hand.get(1).getValue() == hand.get(2).getValue();
    }
    
    private static boolean isStraight(ArrayList<Card> hand) {
        ArrayList<Integer> values = new ArrayList<>();
        for (Card card : hand) {
            values.add(card.getValue());
        }
        Collections.sort(values);
        
        // Check for normal straight
        if (values.get(2) - values.get(1) == 1 && values.get(1) - values.get(0) == 1) {
            return true;
        }
        
        // Check for Ace-2-3 straight (A=14, 2, 3)
        if (values.get(0) == 2 && values.get(1) == 3 && values.get(2) == 14) {
            return true;
        }
        
        return false;
    }
    
    private static boolean isFlush(ArrayList<Card> hand) {
        return hand.get(0).getSuit() == hand.get(1).getSuit() &&
               hand.get(1).getSuit() == hand.get(2).getSuit();
    }
    
    private static boolean isPair(ArrayList<Card> hand) {
        return hand.get(0).getValue() == hand.get(1).getValue() ||
               hand.get(1).getValue() == hand.get(2).getValue() ||
               hand.get(0).getValue() == hand.get(2).getValue();
    }
    
    private static boolean dealerQualifies(ArrayList<Card> hand) {
        // Dealer must have at least Queen high
        for (Card card : hand) {
            if (card.getValue() >= 12) { // 12 = Queen
                return true;
            }
        }
        return false;
    }
    
    private static int compareHighCards(ArrayList<Card> dealer, ArrayList<Card> player) {
        ArrayList<Integer> dealerValues = new ArrayList<>();
        ArrayList<Integer> playerValues = new ArrayList<>();
        
        for (Card card : dealer) dealerValues.add(card.getValue());
        for (Card card : player) playerValues.add(card.getValue());
        
        Collections.sort(dealerValues, Collections.reverseOrder());
        Collections.sort(playerValues, Collections.reverseOrder());
        
        for (int i = 0; i < 3; i++) {
            if (dealerValues.get(i) > playerValues.get(i)) {
                return 1; // Dealer higher
            } else if (playerValues.get(i) > dealerValues.get(i)) {
                return -1; // Player higher
            }
        }
        
        return 0; // Tie
    }
}
