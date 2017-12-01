//Calculates a players total hand score

public class HandScore implements java.io.Serializable {

	//Players cards and flop, turn, river
	private Card pCard1;
	private Card pCard2;
	private Card flop1;
	private Card flop2;
	private Card flop3; 	 
	private Card turn;
	private Card river;

	//array to loadCards
	private Card[] loadCards;

	//Constructor
	public HandScore(Card p1, Card p2, Card f1, Card f2, Card f3, Card t, Card r) {
		pCard1 = p1;
		pCard2 = p2;
		flop1 = f1;
		flop2 = f2;
		flop3 = f3;
		turn = t;
		river = r;
		loadCards = new Card[] { pCard1, pCard2, flop1, flop2, flop3, turn, river };
		bubbleSort(loadCards);
	}

	// using standard bubble sort
	private void bubbleSort(Card[] arr) {
		int n = arr.length;
		Card temp = null;
		for (int i = 0; i < n; i++) {
			for (int j = 1; j < (n - i); j++) {
				if (arr[j - 1].getValue() > arr[j].getValue()) {
					// swap elements
					temp = arr[j - 1];
					arr[j - 1] = arr[j];
					arr[j] = temp;
				}

			}
		}

	}

	//Checks if there is 5 of any suit
	private boolean isFlush() {
		int hearts = 0;
		int diamonds = 0;
		int clubs = 0;
		int spades = 0;
		Card card = new Card();
		card.setSuit(Suit.HEARTS);
		Card card1 = new Card();
		card1.setSuit(Suit.DIAMONDS);
		Card card2 = new Card();
		card2.setSuit(Suit.CLUBS);
		for (int i = 0; i < loadCards.length; i++) {
			if (loadCards[i].getSuit() == card.getSuit()) {
				hearts++;
				if (hearts == 5)
					return true;
			} else if (loadCards[i].getSuit() == card1.getSuit()) {
				diamonds++;
				if (diamonds == 5)
					return true;
			} else if (loadCards[i].getSuit() == card2.getSuit()) {
				clubs++;
				if (clubs == 5)
					return true;
			} else {
				spades++;
				if (spades == 5)
					return true;
			}
		}
		return false;
	}

	//checks if there are 5 values in a row
	//uses modular arithmetic to check for odd straights with an ace
	private boolean isStraight() {
		int count = 1;
		int count2 = 15;

		for (int i = 0; i < count2; i++) {
			if ((((loadCards[i % 7].getValue()) + 1) % 13) == loadCards[(i + 1) % 7].getValue()) {
				count++;
			} 
			else if (((loadCards[i % 7].getValue())) == loadCards[(i + 1) % 7].getValue()) {
				count2++;
			} 
			else {
				count = 1;
				count2++;
			}
			if (count == 5) {
				return true;
			}
		}

		return false;
	}

	// has a to be at least a straight flush to be a royal flush
	private boolean isRoyalFlush() {
		if (isStraightFlush() == false) {
			return false;
		}
		// must be the high cards
		if (loadCards[0].getValue() == 0 && loadCards[4].getValue() == 12) {
			return true;
		}
		if (loadCards[1].getValue() == 0 && loadCards[5].getValue() == 12) {
			return true;
		}
		if (loadCards[2].getValue() == 0 && loadCards[6].getValue() == 12) {
			return true;
		}
		return false;
	}
	//if straight is true and flush is true
	private boolean isStraightFlush() {
		if (isStraight() == true && isFlush() == true) {
			return true;
		} 
		return false;

	}

	//if card[i] is the same as the next 3 cards return true
	private boolean isFour() {
		for (int i = 0; i < loadCards.length-3; i++) {
			if(loadCards[i].getValue() == loadCards[i+1].getValue()&&loadCards[i].getValue() == loadCards[i+2].getValue()
					&&loadCards[i].getValue() == loadCards[i+3].getValue()) {
				return true;

					}
		}

		return false;
	}

	//if card[i] is equal to the next 2 cards
	private boolean isThree() {
		for (int i = 0; i < loadCards.length-2; i++) {
			if(loadCards[i].getValue() == loadCards[i+1].getValue()&&loadCards[i].getValue() == loadCards[i+2].getValue()) {
				return true;

			}
		}

		return false;
	}
	// if there is a pair
	private boolean isTwo() {
		int count = 1;
		for (int i = 0; i < loadCards.length-1; i++) {
			if(loadCards[i].getValue() == loadCards[i+1].getValue()) {
				count++;
				if(loadCards[i].getValue() == loadCards[(i+2)%7].getValue()) {
					count--;
					i++;
				}
			}
		}
		if(count == 2) {
			return true;
		}
		return false;
	}
	// if 3 of a kind is true and two of a kind is true
	private boolean isFullHouse() {
		if (isThree() == true && isTwo() == true) {
			return true;
		}
		return false;
	}

	// if there are two separate pairs of cards
	private boolean isTwoPair() {
		int count = 0;
		for(int i = 0; i < loadCards.length-1; i++) {
			if(loadCards[i].getValue() == loadCards[i+1].getValue()) {
				count++;
			}
			if(count == 2) {
				return true;
			}
		}
		return false;
	}
	//check which card in your hand has the higher value
	public int getHighCard() {
		if (pCard1.getValue() == 1 || pCard2.getValue() == 1) {
			return 13;
		}
		if (pCard1.getValue() > pCard2.getValue()) {
			return pCard1.getValue()-1;
		}

		return pCard2.getValue()-1;
	}


	//if else statements to determine total score
	public int getScore() {
		if (isRoyalFlush())
			return 22;

		else if (isStraightFlush())
			return 21;

		else if (isFour())
			return 20;

		else if (isFullHouse())
			return 19;

		else if (isFlush())
			return 18;

		else if (isStraight())
			return 17;

		else if (isThree())
			return 16;

		else if (isTwoPair())
			return 15;

		else if (isTwo())
			return 14;

		else
			return getHighCard();

	}
}
