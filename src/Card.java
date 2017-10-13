enum Suit {HEARTS, DIAMONDS, CLUBS, SPADES}


public class Card {
	private int value;
	private Suit suit;
		
	public Card() {
		this.value = 1;
		this.suit = Suit.SPADES;
	}
	
	public void setSuit(Suit s) {
		this.suit = s;
	}
	
	public Suit getSuit() {
		return this.suit;
	}
	
	public void setValue(int v) {
		this.value = v;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public String toString() {
		String s = "";
		switch(this.suit) {
			case HEARTS:
				s = " of hearts\n";
				break;
			case DIAMONDS:
				s = " of diamonds\n";
				break;
			case CLUBS:
				s = " of clubs\n";
				break;
			case SPADES:
				s = " of spades\n";
				break;
		}
		return this.value + s;
	}
 }