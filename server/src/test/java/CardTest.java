import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

    @Test
    void testCardSuit() {
        Card card = new Card('H', 10);
        assertEquals('H', card.getSuit(), "Card suit should be 'H'");
    }

    @Test
    void testCardValue() {
        Card card = new Card('D', 7);
        assertEquals(7, card.getValue(), "Card value should be 7");
    }

    @Test
    void testAceToString() {
        Card ace = new Card('S', 14);
        assertEquals("AS", ace.toString(), "Ace should display as 'AS'");
    }

    @Test
    void testKingToString() {
        Card king = new Card('C', 13);
        assertEquals("KC", king.toString(), "King should display as 'KC'");
    }

    @Test
    void testQueenToString() {
        Card queen = new Card('H', 12);
        assertEquals("QH", queen.toString(), "Queen should display as 'QH'");
    }

    @Test
    void testJackToString() {
        Card jack = new Card('D', 11);
        assertEquals("JD", jack.toString(), "Jack should display as 'JD'");
    }

    @Test
    void testNumberCardToString() {
        Card card = new Card('S', 5);
        assertEquals("5S", card.toString(), "5 of Spades should display as '5S'");
    }

    @Test
    void testClubsSuit() {
        Card card = new Card('C', 9);
        assertEquals('C', card.getSuit(), "Clubs suit should be 'C'");
        assertEquals("9C", card.toString(), "9 of Clubs should display as '9C'");
    }

    @Test
    void testDiamondsSuit() {
        Card card = new Card('D', 3);
        assertEquals('D', card.getSuit(), "Diamonds suit should be 'D'");
        assertEquals("3D", card.toString(), "3 of Diamonds should display as '3D'");
    }

    @Test
    void testMinimumValue() {
        Card card = new Card('H', 2);
        assertEquals(2, card.getValue(), "Minimum card value should be 2");
        assertEquals("2H", card.toString(), "2 of Hearts should display as '2H'");
    }
}
