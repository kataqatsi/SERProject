import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TexasHoldemServer extends Application implements TexasHoldemConstants{
	ObjectOutputStream toPlayer[];
	ObjectInputStream fromPlayer[];
	
	Deck d;
	Table table;
	Player players[];
	Socket socket[];
	Card flop[];
	Card turn;
	Card river;
	int time = 10;
	
	int maxPlayers = 2;
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
					socket = new Socket[maxPlayers];
					
					for (int i = 0; i < maxPlayers; i++) {
						socket[i] = serverSocket.accept();
						log.appendText(new Date() + ": Player " + (i+1) + " joined session " + sessionNo + '\n');
						log.appendText("Player " + (i+1) +"'s IP address " + socket[i].getInetAddress().getHostAddress() + '\n');
						numOfPlayers++;
					}
					
					players = new Player[numOfPlayers];
					toPlayer = new ObjectOutputStream[numOfPlayers];
					fromPlayer = new ObjectInputStream[numOfPlayers];
					
					for (int i = 0; i < numOfPlayers; i++) {
						toPlayer[i] = new ObjectOutputStream(socket[i].getOutputStream());
						fromPlayer[i] = new ObjectInputStream(socket[i].getInputStream());
					}
					
					//Game thread, sends players off into instance of the game
					new Thread(()->{
						//GAME BEGINS HERE
						Send send;
						try {
							boolean stillPlaying = true;
							assignSeats(); //Sends client seat number
							
							//Game loop
							//while(stillPlaying) {
								sendTable(); //Sends client blank cards all players
								dealCards(); //Sends client 2 cards
								
								
								EventHandler<ActionEvent> eventHandler = e -> {
						            if (time == 10) {
						            		log.appendText("Player has " +time + " seconds to make a decision\n");
						            }
						            if (time == 5) {
					            			log.appendText("Player has " +time + " seconds to make a decision\n");
						            }
						            if (time == 0) {
						            		log.appendText("Player's time is up!\n");
						            }
						            time--;  
						        };
								
								Timeline animation = new Timeline(
						        	      new KeyFrame(Duration.millis(1000), eventHandler));
						        	animation.setCycleCount(Timeline.INDEFINITE);
						        	animation.play();
								
								sendTableFlop(); //Sends client blank cards+flop
								sendTableFlopTurn(); //Sends client blank cards+flop+turn
								sendTableFlopTurnRiver(); //Sends client blank cards+flop+turn+river
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
	
	public void dealCards() throws IOException {
		//Creates new Deck
		d = new Deck();
		d.shuffle();
		d.shuffle();
		d.shuffle();
		Card c = new Card();
		
		//Deal cards to each player
		for (int i = 0; i < numOfPlayers; i++) {
			c = d.drawCard();
			players[i].setCard(c);
			toPlayer[i].writeObject(c);
			c = d.drawCard();
			players[i].setCard(c);
			toPlayer[i].writeObject(c);
		}
	}
	
	public void assignSeats() throws IOException {
		//seatNum set to 3 for testing purposes
		int seatNum = 3;
		for (int i = 0; i < numOfPlayers; i++) {
			players[i] = new Player(seatNum);
			toPlayer[i].writeInt(seatNum);
			seatNum++;
		}
		seatNum = 3;	
	}
	
	public void sendTable() throws IOException {
		table = new Table(players);
		table.setPlayerCards();
		for (int i = 0; i < numOfPlayers; i++) {
			toPlayer[i].writeObject(table);
		}	
	}
	
	public void sendTableFlop() throws IOException {
		flop = new Card[3];
		flop[0] = d.drawCard();
		flop[1] = d.drawCard();
		flop[2] = d.drawCard();
		
		table = new Table(players, flop);
		table.setPlayerCards();
		for (int i = 0; i < numOfPlayers; i++) {
			toPlayer[i].writeObject(table);
		}	
	}
	
	public void sendTableFlopTurn() throws IOException {
		turn = d.drawCard();
		
		table = new Table(players, flop, turn);
		table.setPlayerCards();
		for (int i = 0; i < numOfPlayers; i++) {
			toPlayer[i].writeObject(table);
		}	
	}
	
	public void sendTableFlopTurnRiver() throws IOException {
		river = d.drawCard();
		
		table = new Table(players, flop, turn, river);
		table.setPlayerCards();
		for (int i = 0; i < numOfPlayers; i++) {
			toPlayer[i].writeObject(table);
		}	
	}
	
}
