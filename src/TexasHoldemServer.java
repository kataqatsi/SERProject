import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class TexasHoldemServer extends Application implements TexasHoldemConstants{
	ObjectOutputStream toPlayer[];
	ObjectInputStream fromPlayer[];
	
	Deck d;
	Player players[];
	int numOfPlayers = 0;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Texas Holdem Server");
		Group root = new Group();
        Scene scene = new Scene(root, 540, 190);
        TextArea log = new TextArea();
        ScrollPane scrollPane = new ScrollPane(log);
        
        root.getChildren().add(log);
        primaryStage.setScene(scene);
		primaryStage.show();
		
		//JavaFX requires its own thread to show GUI
		new Thread(()->{
			try {
				ServerSocket serverSocket = new ServerSocket(8000);
				log.appendText(new Date() + ": Server started at socket 8000\n");
				int sessionNo = 0;
				
				//Loop connects new players to a game thread
				while(true) {
					sessionNo++;
					Socket player1 = serverSocket.accept();
					toPlayer1 = new ObjectOutputStream(player1.getOutputStream());
					fromPlayer1 = new ObjectInputStream(player1.getInputStream());
					log.appendText(new Date() + ": Player 1 joined session " + sessionNo + '\n');
					log.appendText("Player 1's IP address " +player1.getInetAddress().getHostAddress() + '\n');
					numOfPlayers++;
						
					Socket player2 = serverSocket.accept();
					toPlayer2 = new ObjectOutputStream(player2.getOutputStream());
					fromPlayer2 = new ObjectInputStream(player2.getInputStream());
					log.appendText(new Date() + ": Player 2 joined session " + sessionNo + '\n');
					log.appendText("Player 2's IP address " +player1.getInetAddress().getHostAddress() + '\n');
					numOfPlayers++;
					players = new Player[numOfPlayers];
					toPlayer = new ObjectOutputStream[numOfPlayers];
					fromPlayer = new ObjectInputStream[numOfPlayers];
					
					
					toPlayer[0] = players[0].getOut();
					fromPlayer[1] = players[1].getInput();
					
					//Game thread, sends players off into instance of the game
					new Thread(()->{
						//GAME BEGINS HERE
						Send send;
						try {
							//Assign player seat numbers
							boolean stillPlaying = true;
							assignSeats();
							
							
							//Game loop
							//while(stillPlaying) {
								startNewGame();
								stillPlaying = false;
							//}
						} catch(Exception ex) {
							
						}
					}).start();
				}
			} catch(IOException ex) {
			      System.err.println(ex);
			}
		;}).start();	
	}
	
	public void startNewGame() throws IOException {
		//Creates new Deck
		d = new Deck();
		d.shuffle();
		d.shuffle();
		d.shuffle();
		
		//Deal cards to player 1
		Card c = d.drawCard();
		toPlayer1.writeObject(c);
		c = d.drawCard();
		toPlayer1.writeObject(c);
		
		
		//Deal cards to player 2
		c = d.drawCard();
		toPlayer2.writeObject(c);
		c = d.drawCard();
		toPlayer2.writeObject(c);
		
		//Deal blank cards to every player to set for opponent
		c.setSuit(Suit.CARDBACK);
		toPlayer1.writeObject(c);
		toPlayer1.writeObject(c);
		toPlayer2.writeObject(c);
		toPlayer2.writeObject(c);
	}
	
	public void assignSeats() throws IOException {
		int seatNum = 3;
		
		toPlayer1.writeInt(seatNum);
		seatNum++;
		toPlayer1.writeInt(seatNum);
		toPlayer2.writeInt(seatNum);
		seatNum--;
		toPlayer2.writeInt(seatNum);
	}
}
