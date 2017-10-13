
public class Player {
	private String username;
	private int chipCount;
	Card card1;
	Card card2;
	
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
}
