import java.io.Serializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
public class Table implements Serializable {
    Player player[];
    Card flop[];
    Card turn;
    Card river;
    Card card;
    int playerChips[];
    
    int stage;
	int playerNumTurn;
	int pot;

	public Card getFlop1() {
		return flop[0];
	}
	public Card getFlop2() {
		return flop[1];
	}
	public Card getFlop3() {
		return flop[2];
	}
	public Card getTurn() {
		return turn;
	}
	public Card getRiver() {
		return river;
	}
    
	public void clearChips() {
		playerChips = new int[10];
		
		for(int i = 0; i < 10; i++) {
			playerChips[i] = 0;
		}
	}
    public Table(Player[] p) {
        //player = p;
    		stage = 0;
		player = new Player[p.length];
		clearChips();
		
		for (int i = 0; i < player.length; i++) {
			//p[i].printout();
			player[i] = new Player(p[i].getSeatNum(), p[i].getChips());
			playerChips[i] = p[i].getChips();
			//player[i].printout();
			//player[i].clearCards();
			//player[i].printout();
		}
        
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
        //player = p;
    		stage = 1;
		player = new Player[p.length];
		clearChips();
		
		for (int i = 0; i < player.length; i++) {
			//p[i].printout();
			player[i] = new Player(p[i].getSeatNum(), p[i].getChips());
			playerChips[i] = p[i].getChips();
			//player[i].printout();
			//player[i].clearCards();
			//player[i].printout();
		}
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
        //player = p;
    		stage = 2;
		player = new Player[p.length];
		clearChips();
		for (int i = 0; i < player.length; i++) {
			//p[i].printout();
			player[i] = new Player(p[i].getSeatNum(), p[i].getChips());
			playerChips[i] = p[i].getChips();
			//player[i].printout();
			//player[i].clearCards();
			//player[i].printout();
		}
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
    		stage = 3;
        //player = p;
		player = new Player[p.length];
		clearChips();
		
		for (int i = 0; i < player.length; i++) {
			//p[i].printout();
			player[i] = new Player(p[i].getSeatNum(), p[i].getChips());
			playerChips[i] = p[i].getChips();
			//player[i].printout();
			//player[i].clearCards();
			//player[i].printout();
		}
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
    
    public int[] getPlayerChips() {
    		return playerChips;
    }
    
    public int getStage() {
    		return stage;
    }
}
