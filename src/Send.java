import java.io.Serializable;
public class Send implements Serializable, TexasHoldemConstants {
	public int move; 
	public int bet;
	//moves:
	//	CHECK = 11;
	//	CALL = 12;
	//	RAISE = 13;
	//	FOLD = 14;
	//	TIMEISUP = 15;
	//	CARDBACK = 16;
	//	TEST = 99;

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
		//if (move != RAISE) { //why bother changing this if the server will ignore it anyways?
			//bet = TIMEISUP;
		//}
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
