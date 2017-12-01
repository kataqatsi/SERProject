import java.io.Serializable;
public class Send implements Serializable, TexasHoldemConstants {
	public int move; 
	public int bet;

	public Send() {
		move = TIMEISUP;
		bet = TIMEISUP;
	}

	public Send(int playerMove) {
		move = playerMove;
		bet = TIMEISUP;
	}

	public Send(int playerMove, int playerBet) {
		move = playerMove;
		bet = playerBet;
	}

	public void setMove(int move) {
		this.move = move;
	}

	public int getMove() {
		return move;
	}

	public void setBet(int bet) {
		this.bet = bet;
	}

	public int getBet() {
		return bet;
	}
}
