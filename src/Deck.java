public class Deck {
	private int size = 52;
	private Card cards[] = new Card[size];

	//Generate Deck
	public Deck() {
		//Hearts A-K
		for (int i = 0; i < 13; i++) {
			cards[i] = new Card();
			cards[i].setSuit(Suit.HEARTS);
			cards[i].setValue(i+1);
		}
		//Diamonds A-K
		for (int i = 13; i < 26; i++) {
			cards[i] = new Card();
			cards[i].setSuit(Suit.DIAMONDS);
			cards[i].setValue(i-12);
		}
		//Clubs A-K
		for (int i = 26; i < 39; i++) {
			cards[i] = new Card();
			cards[i].setSuit(Suit.CLUBS);
			cards[i].setValue(i-25);
		}
		//Spades A-K
		for (int i = 39; i < 52; i++) {
			cards[i] = new Card();
			cards[i].setSuit(Suit.SPADES);
			cards[i].setValue(i-38);
		}
	}

	public void shuffle() {
		Card temp = new Card();
		for (int i = 0; i < 10000; i ++) {
			int rand1 = (int) (Math.random() * 52);
			int rand2 = (int) (Math.random() * 52);
			temp = cards[rand1];
			cards[rand1] = cards[rand2];
			cards[rand2] = temp;
		}
	}

	public Card drawCard() {
		Card topCard = cards[0];
		for (int i = 0; i < size-1; i++) {
			cards[i] = cards[i+1];
		}
		size = size - 1;
		return topCard;
	}

	public String toString() {
		String buffer = "";
		for(int i = 0; i < size; i++) {
			buffer += cards[i].toString();
		}
		return buffer;
	}
}
