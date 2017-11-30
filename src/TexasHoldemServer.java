import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

	int roundCount = 0;
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
	int playerNumTurn;
	int playersPlaying;
	Send send;
	int time = 15;

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
		private int time = 15;

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
				System.out.println("Assigned Seats");
				startRound();
				System.out.println("round " + roundCount + "started");
				roundCount++;


				//Game loop
				try {
					//while(!isGameOver()) {//this loop is one round, so everytime it loops it is a new flop.
					//run it while the game isn't over

					playersPlaying = numOfPlayers;
					EventHandler<ActionEvent> eventHandler = e -> {
						if (time == 10) {
							log.appendText("Player has " +time + " seconds to make a decision\n");
						}
						if (time == 5) {
							log.appendText("Player has " +time + " seconds to make a decision\n");
						}
						if (time == 0) {
							log.appendText("Player's time is up!\n");

							if(playersPlaying == 0) {
								startRound();//reset new round, nobody's playing
								System.out.println("round " + roundCount + "started");
								roundCount++;
							}
							switch(table.getStage()) {
								case 0:
									handleTurn();
									System.out.println("HandleTurn case 0");
									if(movesLeft == 0) {
										try {
											sendTableFlop();
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
									}	
									break;
								case 1:
									handleTurn();
									System.out.println("HandleTurn case 1");
									if(movesLeft == 0) {
										try {
											sendTableFlopTurn();
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
									}	

									break;
								case 2:
									handleTurn();
									System.out.println("HandleTurn case 2");
									if(movesLeft == 0) {
										try {
											sendTableFlopTurnRiver();
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
									}	
									break;
								case 3:
									handleTurn();
									System.out.println("HandleTurn case 3");
									if(movesLeft == 0) {
										int winner=checkWinner();
										players[winner].addChips(pot);
										pot = 0;
										startRound();//reset new round, nobody's playing
										System.out.println("round " + roundCount + "started");
										roundCount++;
									}
									break;
							}

							try {
								sendTable();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							time = 16;
						}
						time--;  
					};

					Timeline animation = new Timeline(
							new KeyFrame(Duration.millis(1000), eventHandler));
					animation.setCycleCount(Timeline.INDEFINITE);
					animation.play();


					//}
					stillPlaying = false;
					//}
				} catch(Exception ex) {
					System.out.println(ex);
				}
			} catch(Exception ex) {
				System.out.println(ex);
			}
		}

		public void startRound() {
			boolean success = false;
			while(!success) {
				try {
					dealCards();
					System.out.println("Cards dealt");
					getPlayerTurn();
					sendTable();
					System.out.println("Table sent");
					success = true;
				} catch(Exception e) {
					System.out.println(e);
				}
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
			System.out.println("table-setPlayerCard()");
			//players[0].printout();
			for (int i = 0; i < numOfPlayers; i++) {
				//players[i].printout();
				toPlayer[i].reset();
				toPlayer[i].writeObject(table);
				System.out.println("writeObject table to player " + i);
				players[i].printout();
				toPlayer[i].writeObject(players[i]);//just send the client the entire player
				System.out.println("writeObject Player to player " + i);
				//System.out.println("-----");
			}	
		}

		public void sendTableFlop() throws IOException {
			flop = new Card[3];
			flop[0] = d.drawCard();
			flop[1] = d.drawCard();
			flop[2] = d.drawCard();

			table = new Table(players, flop);
		}

		public void sendTableFlopTurn() throws IOException {
			turn = d.drawCard();
			table = new Table(players, flop, turn);
		}

		public void sendTableFlopTurnRiver() throws IOException {
			river = d.drawCard();
			table = new Table(players, flop, turn, river);
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
			if(send.getMove() == CALL || send.getBet() > currentBet) {
				if(bet <= players[i].getChips()) {//allow the player to bet, they have the money
					if(send.getMove() == RAISE) {
						bet = send.getBet();
						movesLeft = numOfPlayers;//need to loop through everyone again to let them catch up with the raise
					}
					currentBet = bet;
					pot += bet - players[i].getBet();
					players[i].setBet(send.getBet());
					return true;
				} else {
					//fail the bet, and they fold
					//TODO probably a good idea to set up a better system
					//until then, it's on the player to be smart
					players[i].clearCards();
					return false;
				}
			}
			return false;
		}

		public void handleTurn() {
			System.out.println("Getting Player Turn");
			int turn = getPlayerTurn();
			System.out.println("Got Player Turn");
			try {
				System.out.println("Receiving Send object from player " + turn);
				send = (Send) fromPlayer[turn].readObject();
				System.out.println("Received Send-" +send.getMove());
			} catch (ClassNotFoundException | IOException e1) {
				System.out.println("ERROR");
				//e1.printStackTrace();
			}

			switch(send.getMove()) {
				case RAISE:
					System.out.println("Received RAISE");
					if(!betFunction(turn)) {//if they couldn't bet, the function returns false and there's one less player playing
						playersPlaying--;
					}
					System.out.println("RAISE passed if statement");
					break;
				case CALL:
					System.out.println("Received CALL");
					if(!betFunction(turn)) {//if they couldn't bet, the function returns false and there's one less player playing
						playersPlaying--;
					}
					System.out.println("CALL passed if statement");
					break;
				case TIMEISUP:
				case FOLD:
					System.out.println("Received FOLD/TIMEISUP");
					players[turn].clearCards();
					playersPlaying--;
				case CHECK:
					System.out.println("Received CHECK");
					if(players[turn].getBet() != currentBet) {
						playersPlaying--;//they tried to check when they weren't allowed to, out of this round
					}
					System.out.println("CHECK passed if statement");
				default:
					break;
			}
			movesLeft--;
			incrementPlayerTurn();
		}

		public int getPlayerTurn() {
			for(int i = 0; i < numOfPlayers; i++) {
				if(players[i].getTurn()) {
					return i;
				}
			}
			players[0].setTurn(true);
			return 0;
		}

		public void incrementPlayerTurn() {
			int turn = getPlayerTurn();
			players[turn].setTurn(false);
			players[(turn+1) % numOfPlayers].setTurn(true);
		}
	}
}
