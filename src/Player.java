import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javafx.scene.canvas.GraphicsContext;

public class Player implements Serializable {
	private int seatNumber;
	private String username;
	private int chipCount;
	Card card1;
	Card card2;
	ObjectOutputStream out;
	ObjectInputStream in;
	
	// IF USING DEFAULT CONSTRUCTOR, REQUIRES USE OF setSeatNum() 
	// IMMEDIATELY AFTER
	public Player() {
		seatNumber = 0;
		chipCount = 500;
		card1 = new Card();
		card2 = new Card();
	}
	
	public Player(int num) {
		seatNumber = num;
		chipCount = 500;
		card1 = new Card();
		card2 = new Card();
	}
	
	public void setChips(int chips) {
		chipCount = chips;
	}
	
	public int getChips() {
		return chipCount;
	}
	
	public void setUsername(String name) {
		username = name;
	}
	
	public String getUsername() {
		return username;
	}

	public void setCard(Card c) {
		if (card1.getValue() == 0) {
			card1 = c;
		} else {
			card2 = c;
		}
		if ((card1.getValue() != 0) && card2.getValue() != 0) {
			switch(seatNumber) {
			case 1:
				card1.setX(700);
				card1.setY(225);
				card2.setX(750);
				card2.setY(225);
				break;
			case 2:
				card1.setX(820);
				card1.setY(265);
				card2.setX(870);
				card2.setY(265);
				break;
			case 3:
				card1.setX(865);
				card1.setY(360);
				card2.setX(915);
				card2.setY(360);
				break;
			case 4:
				card1.setX(820);
				card1.setY(460);
				card2.setX(870);
				card2.setY(460);
				break;
			case 5:
				card1.setX(700);
				card1.setY(500);
				card2.setX(750);
				card2.setY(500);
				break;
			case 6:
				card1.setX(440);
				card1.setY(500);
				card2.setX(490);
				card2.setY(500);
				break;
			case 7:
				card1.setX(310);
				card1.setY(460);
				card2.setX(360);
				card2.setY(460);
				break;
			case 8:
				card1.setX(265);
				card1.setY(360);
				card2.setX(315);
				card2.setY(360);
				break;
			case 9:
				card1.setX(320);
				card1.setY(265);
				card2.setX(370);
				card2.setY(265);
				break;
			case 10:
				card1.setX(440);
				card1.setY(225);
				card2.setX(490);
				card2.setY(225);
				break;
		}
		}	
	}
	
	public Card getCard1() {
		return card1;
	}
	
	public Card getCard2() {
		return card2;
	}
	
	public void renderHand(GraphicsContext gc) {
		card1.render(gc);
		card2.render(gc);
	}
	
	public void setSeatNum(int s) {
		seatNumber = s;
	}
	
	public int getSeatNum() {
		return seatNumber;
	}
	
	public ObjectInputStream getInput() {
		return in;
	}
	
	public ObjectOutputStream getOut() {
		return out;
	}
	
	public void setInput(ObjectInputStream x) {
		in = x;
	}
	
	public void setOutput(ObjectOutputStream x) {
		out = x;
	}
	
	public void writeObject(Object obj) throws IOException {
		out.writeObject(obj);
	}
	
	public Object readObject() throws IOException, ClassNotFoundException {
		Object obj = in.readObject();
		return obj;
	}
}

