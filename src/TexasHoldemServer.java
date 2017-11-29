import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javafx.animation.KeyFrame;
//import javafx.animation.Timeline;
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

public class TexasHoldemServer extends Application implements TexasHoldemConstants {
	
	private Socket socket[];
	private int maxPlayers = 2;
	private TextArea log = new TextArea();
	ObjectOutputStream toPlayer[];
	ObjectInputStream fromPlayer[];
	
	Deck d;
	Table table;
	Player players[];
	Send playerMoves[];
	Card flop[];
	Card turn;
	Card river;
	int pot;
	int movesLeft;
	int currentBet;
	//int time = 10;
	
	int numOfPlayers = 0;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Texas Holdem Server");
		Group root = new Group();
        Scene scene = new Scene(root, 540, 190);
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
					}
					
					
					HandleAClient task = new HandleAClient(socket);
					new Thread(task).start();
				}
			} catch(IOException ex) {
			      System.err.println(ex);
			}
		;}).start();	
	}
	
	class HandleAClient implements Runnable {
		private Socket[] socket;
		private Deck d;
		private Table table;
		private ObjectOutputStream toPlayer[];
		private ObjectInputStream fromPlayer[];
		private Player players[];
		private Card flop[];
		private Card turn;
		private Card river;
		private int numOfPlayers;
		private int time = 10;

		public HandleAClient(Socket[] socket) {
			this.socket = socket;
			numOfPlayers = socket.length;
			players = new Player[numOfPlayers];
			toPlayer = new ObjectOutputStream[numOfPlayers];
			fromPlayer = new ObjectInputStream[numOfPlayers];
			
		}
		
		public void run() {
			log.appendText("HandleAClient Created a Thread");
			Send send;
			
			try {
				for (int i = 0; i < numOfPlayers; i++) {
					toPlayer[i] = new ObjectOutputStream(socket[i].getOutputStream());
					fromPlayer[i] = new ObjectInputStream(socket[i].getInputStream());
				}
				boolean stillPlaying = true;
				assignSeats(); //Sends client seat number
				
				//Game loop
						//GAME BEGINS HERE
						//Send send;
						try {
							//boolean stillPlaying = true;
							assignSeats(); //Sends client seat number
							System.out.println("seats asigned");
							int dealer = 0;
							
							playerMoves = new Send[numOfPlayers];
							//Game loop
							//while(stillPlaying) {//this loop is one round, so everytime it loops it is a new flop.
							while(!isGameOver()) {//this loop is one round, so everytime it loops it is a new flop.
								//run it while the game isn't over

								//NEED TO UPDATE ALL SENDTABLE FUNCTIONS TO ALSO SEND THE INDIVIDUAL PLAYERS, AND UPDATE CLIENT TO RECIEVE BOTH OBJECTS
								//and in general sync up the server and client sending/recieving
								dealCards();
								System.out.println("cards dealt");
								sendTable();
								System.out.println("table sent");

								currentBet = 0;

								playerMoves[(dealer+1)%numOfPlayers] = new Send(RAISE, 5);//little blind
								betFunction((dealer+1)%numOfPlayers);
								playerMoves[(dealer+2)%numOfPlayers] = new Send(RAISE, 10);//big blind
								betFunction((dealer+2)%numOfPlayers);
								
								loopPlayerTurn((dealer+3)%numOfPlayers);//preflop
								System.out.println("preflop");

								sendTableFlop();
								loopPlayerTurn((dealer+1)%numOfPlayers);
								System.out.println("flop");

								sendTableFlopTurn();
								loopPlayerTurn((dealer+1)%numOfPlayers);
								System.out.println("turn");

								sendTableFlopTurnRiver();
								loopPlayerTurn((dealer+1)%numOfPlayers);
								System.out.println("river");


								int winner=checkWinner();
								players[winner].addChips(pot);
								pot = 0;

								dealer++;
								dealer %= numOfPlayers;
							}
					stillPlaying = false;
				//}
			} catch(Exception ex) {
				System.out.println(ex);
			}
		} catch(Exception ex) {
			System.out.println(ex);
		}
	}

	public int checkWinner() {
		HandScore score[] = new HandScore[numOfPlayers];
		//int intScore[] = new int[numOfPlayers];
		int highScore = 0;
		int winner = 0;
		for(int i = 0; i < numOfPlayers; i++) {
			score[i] = new HandScore(players[i].getCard1(), players[i].getCard2(), table.getFlop1(), table.getFlop2(), table.getFlop3(), table.getTurn(), table.getRiver());
			//intScore[i] = score[i].getScore();
		}
		for(int i = 0; i < numOfPlayers; i++) {
			if(score[i].getScore() >= highScore) {
				winner = i;
				highScore = score[i].getScore();
			}
		}

		return winner;
	}

	public void dealCards() throws IOException {
		//Creates new Deck
		d = new Deck();
		d.shuffle();
		d.shuffle();
		d.shuffle();
		//Card c;
		//Card c = new Card();
		
		//Deal cards to each player
		for (int i = 0; i < numOfPlayers; i++) {
			players[i].clearCards();
			//c = d.drawCard();
			players[i].setCard(d.drawCard());
			//players[i].setCard(c);
			//toPlayer[i].writeObject(c);
			//c = d.drawCard();
			players[i].setCard(d.drawCard());

			players[i].printout();
			//toPlayer[i].writeObject(players[i]);//just send the client the entire player
		}
		table = new Table(players);
	}
	
	public void assignSeats() throws IOException {
		int seatNum = 1;
		for (int i = 0; i < numOfPlayers; i++) {
			//players[i] = new Player(i);
			players[i] = new Player(seatNum);
			toPlayer[i].writeObject(players[i]);
			seatNum++;
		}
		
	}
	
	public void sendTable() throws IOException {
			//players[0].printout();
			//players[0].printout();
			table.setPlayerCards();
			//players[0].printout();
		for (int i = 0; i < numOfPlayers; i++) {
			//players[i].printout();
			toPlayer[i].writeObject(table);
			toPlayer[i].writeObject(players[i]);//just send the client the entire player
			//players[i].printout();
			//System.out.println("-----");
		}	
	}
	
	public void sendTableFlop() throws IOException {
		flop = new Card[3];
		flop[0] = d.drawCard();
		flop[1] = d.drawCard();
		flop[2] = d.drawCard();
		
		table = new Table(players, flop);
		/*table.setPlayerCards();
		for (int i = 0; i < numOfPlayers; i++) {
			toPlayer[i].writeObject(table);
			toPlayer[i].writeObject(players[i]);//just send the client the entire player
		}*/
		sendTable();
	}
	
	public void sendTableFlopTurn() throws IOException {
		turn = d.drawCard();
		
		table = new Table(players, flop, turn);
		/*table.setPlayerCards();
		for (int i = 0; i < numOfPlayers; i++) {
			toPlayer[i].writeObject(table);
			toPlayer[i].writeObject(players[i]);//just send the client the entire player
		}*/
		sendTable();
	}
	
	public void sendTableFlopTurnRiver() throws IOException {
		river = d.drawCard();
		
		table = new Table(players, flop, turn, river);
		/*table.setPlayerCards();
		for (int i = 0; i < numOfPlayers; i++) {
			toPlayer[i].writeObject(table);
			toPlayer[i].writeObject(players[i]);//just send the client the entire player
		}*/
		sendTable();
	}

	public boolean isGameOver() {
		//subtract 1 so it works with array indices
		return isGameOver(numOfPlayers-1, 0);//just makes it easier to call this from other places, because you have to initially call it like this
		//don't judge my crappy hacks
	}
	public boolean isGameOver(int playerNum, int numPlayersWithChips) {
		if(numPlayersWithChips > 1) {
			return false; //multiple people have chips, so game is still going
		} else if(playerNum < 0) {
			return true; //the function got past the last player and never returned false, so the game is over
		} else if(players[playerNum].getChips() == 0) {
			return isGameOver(playerNum-1, numPlayersWithChips);
		} else {
			return isGameOver(playerNum-1, numPlayersWithChips+1);
		}
	}

	public boolean betFunction(int i) { //return true if player successfully bet, otherwise false
		int bet = currentBet;
		//what a screwed up if statement
		//so sorry
		if(playerMoves[i].getMove() == CALL || playerMoves[i].getBet() > currentBet) {
			if(bet <= players[i].getChips()) {//allow the player to bet, they have the money
				if(playerMoves[i].getMove() == RAISE) {
					bet = playerMoves[i].getBet();
					movesLeft = numOfPlayers;//need to loop through everyone again to let them catch up with the raise
				}
				currentBet = bet;
				pot += bet - players[i].getBet();
				players[i].setBet(playerMoves[i].getBet());
				return true;
			} else {
				//fail the bet, and they fold
				//TODO probably a good idea to set up a better system
				//until then, it's on the player to be smart
				players[i].clearCards();
				playerMoves[i] = new Send(CHECK);
				return false;
			}
		}
		return false;
	}

	public void loopPlayerTurn(int startingPlayer) throws IOException {
		movesLeft = numOfPlayers;
		int playersPlaying = numOfPlayers;
		int i = startingPlayer;
    //for(int i = 0; i < forLoopCounter; i++) {
		while(movesLeft > 0) {
      // in this loop, we need to get each players move, and deal with it
			//first need to tell player we're waiting for their move
      playerMoves[i] = getPlayerMove(i);
			switch(playerMoves[i].getMove()) {
				case RAISE:
					if(!betFunction(i)) {//if they couldn't bet, the function returns false and there's one less player playing
						playersPlaying--;
					}
					break;
				case CALL:
					if(!betFunction(i)) {//if they couldn't bet, the function returns false and there's one less player playing
						playersPlaying--;
					}
					break;
				case TIMEISUP:
				case FOLD:
					players[i].clearCards();
					playersPlaying--;
				case CHECK:
					if(players[i].getBet() != currentBet) {
						playersPlaying--;//they tried to check when they weren't allowed to, out of this round
					}
				default:
					break;
			}
			movesLeft--;
			
			toPlayer[i].writeObject(table);
			toPlayer[i].writeObject(players[i]);//just send the client the entire player


			i++;
			i %= numOfPlayers;//lets us always loop through each player even when we need to go through multiple times in the case of a raise
		}
	}

	public Send getPlayerMove(int playerNum) {
		boolean recieved = false;
		Send returnObject = new Send();
		while(!recieved) {
			try {
				returnObject = (Send) fromPlayer[playerNum].readObject();
				recieved = true;
			} catch(ClassNotFoundException e) {
				//System.out.println("class not found I guess:" + e);
			} catch(IOException e) {
				//System.out.println("object not recieved:" + e);
				//playerDisconnected(playerNum);
			} catch(Exception e) {
				//System.out.println(e);
			}
		}
		return returnObject;
  }
	}
}
