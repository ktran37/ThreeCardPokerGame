import java.io.Serializable;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;
    private char suit; // 'C', 'D', 'H', 'S'
    private int value; // 2-14 (11=Jack, 12=Queen, 13=King, 14=Ace)
    
    public Card(char suit, int value) {
        this.suit = suit;
        this.value = value;
    }
    
    public char getSuit() {
        return suit;
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        String val = "";
        switch(value) {
            case 14: val = "A"; break;
            case 13: val = "K"; break;
            case 12: val = "Q"; break;
            case 11: val = "J"; break;
            default: val = String.valueOf(value);
        }
        return val + suit;
    }
}
