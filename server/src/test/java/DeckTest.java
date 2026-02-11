import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

public class DeckTest {
    private Deck deck;

    @BeforeEach
    void setUp() {
        deck = new Deck();
    }

    @Test
    void testNewDeckSize() {
        assertEquals(52, deck.size(), "A new deck should contain 52 cards");
    }

    @Test
    void testDealCardReducesSize() {
        int initialSize = deck.size();
        deck.dealCard();
        assertEquals(initialSize - 1, deck.size(), "Dealing a card should reduce deck size by 1");
    }

    @Test
    void testDealCardReturnsValidCard() {
        Card card = deck.dealCard();
        assertNotNull(card, "dealCard() should not return null");
        assertTrue(card.getValue() >= 2 && card.getValue() <= 14, "Card value should be between 2 and 14");
    }

    @Test
    void testDeckHasUniqueCards() {
        HashSet<String> cardSet = new HashSet<>();
        
        for (int i = 0; i < 52; i++) {
            Card card = deck.dealCard();
            String cardKey = card.getSuit() + "" + card.getValue();
            assertFalse(cardSet.contains(cardKey), "Deck should not contain duplicate cards");
            cardSet.add(cardKey);
        }
        
        assertEquals(52, cardSet.size(), "Deck should contain exactly 52 unique cards");
    }

    @Test
    void testShuffleMaintainsSize() {
        int sizeBefore = deck.size();
        deck.shuffle();
        assertEquals(sizeBefore, deck.size(), "Shuffle should not change deck size");
    }

    @Test
    void testDealingAllCardsRebuilds() {
        for (int i = 0; i < 52; i++) {
            deck.dealCard();
        }
        assertEquals(0, deck.size(), "Deck should be empty after dealing all cards");
        
        Card card = deck.dealCard();
        assertNotNull(card, "Deck should rebuild and deal a card when empty");
        assertEquals(51, deck.size(), "Deck should have 51 cards after rebuild and one deal");
    }

    @Test
    void testDeckHasAllSuits() {
        HashSet<Character> suits = new HashSet<>();
        
        for (int i = 0; i < 52; i++) {
            Card card = deck.dealCard();
            suits.add(card.getSuit());
        }
        
        assertTrue(suits.contains('C'), "Deck should contain Clubs");
        assertTrue(suits.contains('D'), "Deck should contain Diamonds");
        assertTrue(suits.contains('H'), "Deck should contain Hearts");
        assertTrue(suits.contains('S'), "Deck should contain Spades");
    }

    @Test
    void testMultipleDealsReduceDeck() {
        deck.dealCard();
        deck.dealCard();
        deck.dealCard();
        assertEquals(49, deck.size(), "Dealing 3 cards should reduce deck to 49");
    }

    @Test
    void testShuffleAfterDealing() {
        deck.dealCard();
        deck.dealCard();
        
        int sizeBeforeShuffle = deck.size();
        deck.shuffle();
        
        assertEquals(sizeBeforeShuffle, deck.size(), "Shuffle should not change deck size after dealing");
        assertEquals(50, deck.size(), "Deck should have 50 cards after dealing 2");
    }

    @Test
    void testMultipleRounds() {
        // First round
        for (int i = 0; i < 52; i++) {
            deck.dealCard();
        }
        
        // Second round (should auto-rebuild)
        for (int i = 0; i < 10; i++) {
            Card card = deck.dealCard();
            assertNotNull(card, "Should be able to deal in second round");
        }
        
        assertEquals(42, deck.size(), "Should have 42 cards after dealing 10 in second round");
    }
}
