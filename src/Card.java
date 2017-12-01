import java.io.Serializable;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

enum Suit {HEARTS, DIAMONDS, CLUBS, SPADES, CARDBACK}


public class Card  implements Serializable, TexasHoldemConstants {
	private int value;
	private Suit suit;

	//used in toString()
	private String stringOfSuit;

	//used to draw with JavaFX
	int xPos = 10000;
	int yPos = 10000;

	//cut position on sprite sheet
	double spriteX = 0;
	double spriteY = 0;
	int height = 64;
	int width = 44;

	public Card() {
		this.value = 0;
		this.suit = Suit.CARDBACK;
		stringOfSuit = "not set";
	}

	public void setSuit(Suit s) {
		this.suit = s;

		//sets toString() and coordinate for sprite sheet
		switch(this.suit) {
			case HEARTS:
				spriteY = 72;
				stringOfSuit = " of hearts\n";
				break;
			case DIAMONDS:
				spriteY = 0;
				stringOfSuit = " of diamonds\n";
				break;
			case CLUBS:
				spriteY = 217;
				stringOfSuit = " of clubs\n";
				break;
			case SPADES:
				spriteY = 145;
				stringOfSuit = " of spades\n";
				break;
			case CARDBACK:
				spriteY = 0;
				stringOfSuit = " card back\n";
				setValue(CARDBACK);
				break;
		}	
	}

	public Suit getSuit() {
		return this.suit;
	}


	public void setValue(int v) {
		this.value = v;

		// sets coordinate for sprite sheet
		if (this.value > 4) {
			spriteX = (v * 52) - 55;
		} else if (this.value != 1) {
			spriteX = (v * 52) - 53;
		} else {
			spriteX = (v * 52) - 52;
		}
	}


	public int getValue() {
		return this.value;
	}

	public void setX(int x) {
		xPos = x;
	}

	public void setY(int y) {
		yPos = y;
	}

	//Draws card at the position
	public void render(GraphicsContext gc) {
		Image spriteSheet = new Image("playing_cards.png");
		gc.drawImage(spriteSheet, spriteX, spriteY, width, height, xPos, yPos, width, height);
	}

	public String toString() {
		return this.value + stringOfSuit;
	}
}
