import java.io.Serializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
public class Table implements Serializable {
    Player player[];
    Card flop[];
    Card turn;
    Card river;
    Card card;
    HandScore score;
    
    
    public Table(Player[] p) {
        player = p;
        
        //set flop to blank cards
        flop = new Card[3];
        for (int i = 0; i < 3; i++) {
        		card = new Card();
	        card.setSuit(Suit.CARDBACK);
        		flop[i] = card;
        }
        
        //set turn to blank card
        card = new Card();
        card.setSuit(Suit.CARDBACK);
        turn = card;
        
        //set river to blank card
        card = new Card();
        card.setSuit(Suit.CARDBACK);
        river = card;
        
        //set coordinates
        flop[0].setX(480);
        flop[0].setY(340);
        flop[1].setX(530);
        flop[1].setY(340);
        flop[2].setX(580);
        flop[2].setY(340);
        turn.setX(630);
        turn.setY(340);
        river.setX(680);
        river.setY(340);
    }
    
    public Table(Player[] p, Card[] f) {
        player = p;
        flop = f;
        
        //set turn to blank card
        card = new Card();
        card.setSuit(Suit.CARDBACK);
        turn = card;
        
        //set river to blank card
        card = new Card();
        card.setSuit(Suit.CARDBACK);
        river = card;
        
        //set coordinates
        flop[0].setX(480);
        flop[0].setY(340);
        flop[1].setX(530);
        flop[1].setY(340);
        flop[2].setX(580);
        flop[2].setY(340);
        turn.setX(630);
        turn.setY(340);
        river.setX(680);
        river.setY(340);
    }
    
    public Table(Player[] p, Card[] f, Card t) {
        player = p;
        flop = f;
        turn = t;
        
        //set river to blank card
        card = new Card();
        card.setSuit(Suit.CARDBACK);
        river = card;
        
        //set coordinates
        flop[0].setX(480);
        flop[0].setY(340);
        flop[1].setX(530);
        flop[1].setY(340);
        flop[2].setX(580);
        flop[2].setY(340);
        turn.setX(630);
        turn.setY(340);
        river.setX(680);
        river.setY(340);
    }
    
    public Table(Player[] p, Card[] f, Card t, Card r) {
        player = p;
        flop = f;
        turn = t;
        river = r;
        flop[0].setX(480);
        flop[0].setY(340);
        flop[1].setX(530);
        flop[1].setY(340);
        flop[2].setX(580);
        flop[2].setY(340);
        turn.setX(630);
        turn.setY(340);
        river.setX(680);
        river.setY(340);
        
    }
   
    public void setPlayerCards() {
    		for (int i = 0; i < player.length; i++) {
    			card = new Card();
    	        card.setSuit(Suit.CARDBACK);
    	        player[i].setCard(card);
    	        
    	        //second statement required to give another card
    	        card = new Card();
    	        card.setSuit(Suit.CARDBACK);
    	        player[i].setCard(card);
    		}
    }
    
    public void render(GraphicsContext gc) {
        Image table = new Image("poker_table.png");
        
        //Draws table
        gc.drawImage(table, 0, 0);
   
        flop[0].render(gc);
        flop[1].render(gc);
        flop[2].render(gc);
        turn.render(gc);
        river.render(gc);
        
        //Draws blank cards at each player
        for (int i = 0; i < player.length; i++) {
        		player[i].renderHand(gc);
        }
        
    }
}