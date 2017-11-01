import java.io.Serializable;

public class Send implements Serializable, TexasHoldemConstants{
	public int choice;
	
	public Send(int i) {
			choice = i;
	}
	
}
