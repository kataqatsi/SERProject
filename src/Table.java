import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Table {
	Player player[];
	Card card;
	
	public Table(Player[] p) {
		card = new Card();
		card.setSuit(Suit.CARDBACK);
		player = p;
	}
	
	public void setCards() {
		for (int i = 0; i < player.length; i++) {
			player[i].setCard(card);
			player[i].setCard(card);
		}
	}
	
	public void render(GraphicsContext gc) {
		Image table = new Image("poker_table.png");
		
		//Draws table
		gc.drawImage(table, 0, 0);
		
		//Draws blank cards at each player
		for (int i = 0; i < player.length; i++) {
			player[i].renderHand(gc);
		}
	}
}
