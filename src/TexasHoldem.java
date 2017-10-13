
public class TexasHoldem {
	public static void main(String[] args) {
		Deck deck = new Deck();
		System.out.println("************Newly created deck, unshuffled:\n" + deck.toString());
		deck.shuffle();
		System.out.println("************Shuffled Deck:\n" + deck.toString());
		System.out.println("************The drawCard() function:\n" + deck.drawCard());
		System.out.println("************Deck with its (size - 1) and elements shifted from drawCard() function: \n" + deck.toString());
	}
}