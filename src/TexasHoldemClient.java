import java.io.IOException;
import static java.util.concurrent.TimeUnit.*;
//import java.lang.Object.Enum<TimeUnit>;
import java.util.concurrent.*;
//import java.util.concurrent.TimeUnit.*;
//import java.util.concurrent.TimeUnit;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.beans.value.*;

import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TexasHoldemClient extends Application implements TexasHoldemConstants{
	//used for testing
	static int playerCount = 0;

	private static String host = "localhost";
	private static ObjectInputStream fromServer;
	private static ObjectOutputStream toServer;
	private boolean connected = false;
	private boolean waiting = true;
	//private boolean myTurn = false;
	private Player player;
	private boolean gameOver = false;
	private int time = 17;
	private Send send = new Send();
	private Table table;
	private TextField inputBetAmount = new TextField();
	private Button btnExit = new Button();
	private Button btnCheck = new Button();
	private Button btnCall = new Button();
	private Button btnRaise = new Button();
	private Button btnFold = new Button();
	private Button btnTest = new Button();
	private Button btnJoinGame = new Button();
	private static Text txtNotify = new Text();
	private static Text txtNotify2 = new Text(); 
	private static Text txtNotify3 = new Text();
	private static Text txtNotify4 = new Text(); 
	private Text txtPlayerChips[] = new Text[10];
	private Text txtPot = new Text();
	private Text timer = new Text();
	private Canvas canvas;
	private Canvas canvas2;
	private Scene scene;
	private Scene scene2;
	private Group root;
	private Group root2;
	private Stage primaryStage;
	private GraphicsContext gc;
	private GraphicsContext gc2;
	private int connectAnimation = 1;
	Timeline animationConnect;

	public static void main(String[] args) {
		//Connect to server
		//connectToServer();
		//Launch JavaFX Game
		launch(args);
	}


	public void start(Stage primaryStage) {

		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Lobby");
		//Canvas canvas = new Canvas(1231,781);
		canvas = new Canvas(500, 300);
		gc = canvas.getGraphicsContext2D();
		root = new Group();
		//Scene scene = new Scene(root, 1231, 781);
		scene = new Scene(root, 500, 300);

		btnJoinGame.setText("Join Game");
		btnJoinGame.setScaleX(3);
		btnJoinGame.setScaleY(3);
		btnJoinGame.setLayoutX(200);
		btnJoinGame.setLayoutY(150);
		btnJoinGame.setOnAction(e -> joinGame());

		btnExit.setVisible(false);
		btnCheck.setVisible(false);
		btnCall.setVisible(false);
		btnRaise.setVisible(false);
		btnFold.setVisible(false);
		btnTest.setVisible(false); 

		root.getChildren().add(canvas);
		root.getChildren().add(txtNotify);
		root.getChildren().add(txtNotify2);
		root.getChildren().add(txtNotify3);
		root.getChildren().add(txtNotify4);
		root.getChildren().add(btnJoinGame);
		root.getChildren().add(btnExit);
		root.getChildren().add(btnCheck);
		root.getChildren().add(btnCall);
		root.getChildren().add(btnRaise);
		root.getChildren().add(btnFold);
		root.getChildren().add(btnTest);

		this.primaryStage.setScene(scene);
		this.primaryStage.show();

		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		lobbyTitleNotification(txtNotify, txtNotify2, "TEXAS HOLD'EM");    
		primaryStage.setScene(scene);
		primaryStage.show();

		//renderGameScreen(gc);


		//recieveObjects();

		//renderGameScreen(gc);
		//table.render(gc);
		//player.renderHand(gc);
	}

	public void receiveObjects() {
		boolean didReceive = false;
		while(!didReceive) {
			try {
				//System.out.println("waiting for table");
				table = (Table) fromServer.readObject(); //Table with blank cards for opponents
				System.out.println("table recieved");
				//player = new Player();
				//player.printout();
				player = (Player) fromServer.readObject();//receive the player info from the server
				System.out.println("player recieved");
				player.printout();
				System.out.println();
				didReceive = true;
				/*if(player.getCard1().getValue() == 0) {
					didReceive = false;
					}*/
			} catch (Exception ex) {
				//at this point the server must have been disconnected, so whoever has the most chips wins
				int largest = 0;
				for(int i = 0; i < table.getPlayerChips().length; i++) {
					if(table.getPlayerChips()[i] > table.getPlayerChips()[largest]) {
						largest = i;
					}
				}
				displayNotification(txtNotify, txtNotify2, "Player " + (largest++) + " won the game!");
			}
		}
		
		renderGameScreen(gc2);
		table.render(gc2);
		player.renderHand(gc2);
		
		if(player.getTurn()) {
			if(table.getHandWinner() >= 0) {
				if(table.getBet() == 0) {
					displayNotification(txtNotify, txtNotify2, "Player " + table.getHandWinner() + " won the last round!\nIt's your turn!");
				} else {
					displayNotification(txtNotify, txtNotify2, "Player " + table.getHandWinner() + " won the last round!\nIt's your turn!\n" + table.getBet() + " TO CALL");
				}
			} else {
				if(table.getBet() == 0) {
					displayNotification(txtNotify, txtNotify2, "It's your turn!");
				} else {
					displayNotification(txtNotify, txtNotify2, "It's your turn!\n" + table.getBet() + " TO CALL");
				}
			}
		} else {
			if(table.getHandWinner() >= 0) {
				displayNotification(txtNotify, txtNotify2, "Player " + table.getHandWinner() + " won the last round!\nIt's not your turn");
			} else {
				displayNotification(txtNotify, txtNotify2, "It's not your turn");
			}
		}
		
		txtPot.setText(""+table.getPot());
		for(int i = 0; i < table.player.length; i++) {
			txtPlayerChips[i].setText("Player " + (i + 1) + "\nChips: " + table.getPlayerChips()[i] + "\nBet: " + table.player[i].getBet());
		}
	}

	public void incrementPlayerCount() {
		playerCount++;
	}

	public void renderGameScreen(GraphicsContext gc) {
		Image table = new Image("poker_table.png");
		gc.drawImage(table, 0, 0);
	}

	public static void lobbyTitleNotification(Text t1, Text t2, String text) {
		t1.setX(10);
		t1.setY(75);
		t1.setCache(true);
		t1.setText(text);
		t1.setFill(Color.RED);
		t1.setFont(Font.font(null, FontWeight.BOLD, 56));
		t1.setEffect(new GaussianBlur());

		t2.setX(10);
		t2.setY(75);
		t2.setCache(true);
		t2.setText(text);
		t2.setFill(Color.WHITE);
		t2.setFont(Font.font(null, FontWeight.BOLD, 56));       
	}

	public void lobbySmallNotification(Text t1, Text t2, String text) {
		t1.setX(100);
		t1.setY(250);
		t1.setCache(true);
		t1.setText(text);
		t1.setFill(Color.RED);
		t1.setFont(Font.font(null, FontWeight.BOLD, 24));
		t1.setEffect(new GaussianBlur());

		t2.setX(100);
		t2.setY(250);
		t2.setCache(true);
		t2.setText(text);
		t2.setFill(Color.WHITE);
		t2.setFont(Font.font(null, FontWeight.BOLD, 24));       
	}

	public void displayNotification(Text t1, Text t2, String text) {
		t1.setX(475);
		t1.setY(55);
		t1.setCache(true);
		t1.setText(text);
		t1.setFill(Color.RED);
		t1.setFont(Font.font(null, FontWeight.BOLD, 36));
		t1.setEffect(new GaussianBlur());

		t2.setX(475);
		t2.setY(55);
		t2.setCache(true);
		t2.setText(text);
		t2.setFill(Color.WHITE);
		t2.setFont(Font.font(null, FontWeight.BOLD, 36));       
	}

	public void connectToServer() {
		try {
			Thread.sleep(1000);
			Socket socket = new Socket(host, 8000);
			fromServer = new ObjectInputStream(socket.getInputStream());
			toServer = new ObjectOutputStream(socket.getOutputStream());

			//Get Seat
			//int seatNum = fromServer.readInt();
			//player = new Player(seatNum);  

			connected = true;

			//int seatNum = fromServer.readInt();
			player = (Player) fromServer.readObject();
			System.out.println("read player at seat number " + (player.getSeatNum() + 1) + "is it their turn?" + player.getTurn());
			/*if(player.getSeatNum() == 1) {
				player.setTurn(true);
			}*/
			//player = new Player(seatNum);
		} catch (Exception ex) {
			System.out.println("failed");
			System.err.println(ex);
		} 
	}	

	/*	private void waitForPlayerAction() throws InterruptedException {
			while (waiting) {
			Thread.sleep(100);
			}
			waiting = true;
			}*/

	public void check() {
		displayNotification(txtNotify, txtNotify2, "You Check Your Hand");
		send = new Send(CHECK);
		//sendTurn(new Send(CHECK));
	}

	private void call() {
		//if (myTurn == true) {
		if (player.getChips() >= table.getBet()) {
			displayNotification(txtNotify, txtNotify2, "You Call " + table.getBet());
		} else {
			displayNotification(txtNotify, txtNotify2, "NOT ENOUGH CHIPS");
		}
		//sendTurn(new Send(CALL));
		send = new Send(CALL);
		//}	else {
		//displayNotification(txtNotify, txtNotify2, "It is not your turn yet");
		//}
	}

	public void test() {
		if(player.getTurn()) {
			displayNotification(txtNotify, txtNotify2, "It's your turn!\nmake a move before\nthe timer runs out!");
		} else {
			displayNotification(txtNotify, txtNotify2, "It's not your turn");
		}
		renderGameScreen(gc2);
		table.render(gc2);
		player.renderHand(gc2);
	}

	public void fold() {
		displayNotification(txtNotify, txtNotify2, "You Have Folded");
		//sendTurn(new Send(FOLD));
		send = new Send(FOLD);
	}

	public void raise() {
		if (player.getChips() >= table.getBet()) {
			displayNotification(txtNotify, txtNotify2, "You Raise " + table.getBet());
		} else {
			displayNotification(txtNotify, txtNotify2, "NOT ENOUGH CHIPS");
		}
		if(inputBetAmount.getText().isEmpty()) {
			call();//if you didn't input anything, then just call
		} else {
			//sendTurn(new Send(RAISE, Integer.parseInt(inputBetAmount.getText())));
			send = new Send(RAISE, Integer.parseInt(inputBetAmount.getText()));
			inputBetAmount.setText("");
		}
	}


	private void sendTurn(Send send) {
		try {
			toServer.reset();
			toServer.writeObject(send);
			//didSend = true;
			System.out.println("turn sent");
		} catch (Exception ex) {
			System.out.println("failed to send I guess");
			//at this point the server must have been disconnected, so whoever has the most chips wins
			int largest = 0;
			for(int i = 0; i < table.getPlayerChips().length; i++) {
				if(table.getPlayerChips()[i] > table.getPlayerChips()[largest]) {
					largest = i;
				}
			}
			displayNotification(txtNotify, txtNotify2, "Player " + largest + " won the game!");
		}
		send = new Send();
	}


	public void joinGame() {
		//player = new Player();
		new Thread(() -> {connectToServer();}).start();

		EventHandler<ActionEvent> eventHandler = e -> {
			if (!connected) {
				switch(connectAnimation) {
					case 1:
						lobbySmallNotification(txtNotify3, txtNotify4, "Searching for Game");
						break;
					case 2:
						lobbySmallNotification(txtNotify3, txtNotify4, "Searching for Game.");
						break;
					case 3:
						lobbySmallNotification(txtNotify3, txtNotify4, "Searching for Game..");
						break;
					case 4:
						lobbySmallNotification(txtNotify3, txtNotify4, "Searching for Game...");
						break;
				}
			} else {
				displayGameScreen();
				animationConnect.stop();
			}

			connectAnimation++;
			if (connectAnimation == 5) {
				connectAnimation = 1;
			}
		};

		animationConnect = new Timeline(
				new KeyFrame(Duration.millis(200), eventHandler));
		animationConnect.setCycleCount(Timeline.INDEFINITE);
		animationConnect.play();
	}

	public void displayGameScreen() {
		canvas2 = new Canvas(1231,781);
		gc2 = canvas2.getGraphicsContext2D();
		root2 = new Group();
		scene2 = new Scene(root2, 1231, 781);
		timer.setLayoutX(1100);
		timer.setLayoutY(720);
		timer.setCache(true);
		timer.setText(""+ time);
		timer.setFill(Color.YELLOW);
		timer.setFont(Font.font(null, FontWeight.BOLD, 56));

		txtPot.setCache(true);
		txtPot.setFill(Color.GOLD);
		txtPot.setFont(Font.font(null, FontWeight.BOLD, 24));
		txtPot.setX(595);
		txtPot.setY(450);
		
		for(int i = 0; i < 10; i++) {
			txtPlayerChips[i] = new Text();
			txtPlayerChips[i].setFont(Font.font(null, FontWeight.BOLD, 24));
			txtPlayerChips[i].setCache(true);
			txtPlayerChips[i].setFill(Color.GOLD);
			txtPlayerChips[i].setFont(Font.font(null, FontWeight.BOLD, 24));
		}
		
		
		
		txtPlayerChips[0].setX(750);
		txtPlayerChips[0].setY(155);
		
		txtPlayerChips[1].setX(940);
		txtPlayerChips[1].setY(220);
		
		txtPlayerChips[2].setX(1015);
		txtPlayerChips[2].setY(400);
		
		txtPlayerChips[3].setX(945);
		txtPlayerChips[3].setY(580);
		
		txtPlayerChips[4].setX(750);
		txtPlayerChips[4].setY(645);
		
		txtPlayerChips[5].setX(420);
		txtPlayerChips[5].setY(645);
		
		txtPlayerChips[6].setX(235);
		txtPlayerChips[6].setY(580);
		
		txtPlayerChips[7].setX(165);
		txtPlayerChips[7].setY(400);
		
		txtPlayerChips[8].setX(235);
		txtPlayerChips[8].setY(220);
		
		txtPlayerChips[9].setX(420);
		txtPlayerChips[9].setY(155);
		
		txtPot.setX(595);
		txtPot.setY(495);
		
		//receiveObjects();

		EventHandler<ActionEvent> eventHandler = e -> {       

			if(time == 17) {
				receiveObjects();
				for (int i = table.player.length; i < 10; i++) {
					txtPlayerChips[i].setVisible(false);
				}
			}
			if(time < 15 && !player.getTurn()) {
				if(table.getHandWinner() >= 0) {
					displayNotification(txtNotify, txtNotify2, "Player " + table.getHandWinner() + " won the last round!\nIt's not your turn");
				} else {
					displayNotification(txtNotify, txtNotify2, "It's not your turn");
				}
			}
			if (time == 10) {
				timer.setFill(Color.ORANGE);
			}
			if (time == 5) {
				timer.setFill(Color.RED);
			}
			timer.setText("" + time);
			if (time == 0) {
				timer.setText("END");
				time = 16;
				timer.setFill(Color.YELLOW);
				System.out.println("is player turn: " + player.getTurn());
				if(player.getTurn()) {
					sendTurn(send);
				}
				receiveObjects();
				//send = new Send(TIMEISUP);
			}
			time--;    
		};

		Timeline animation = new Timeline(
				new KeyFrame(Duration.millis(1000), eventHandler));
		animation.setCycleCount(Timeline.INDEFINITE);
		animation.play();

		btnExit.setText("Exit Game");
		btnExit.setOnAction(e -> exit());

		btnCheck.setText("Check");
		btnCheck.setLayoutX(300);
		btnCheck.setLayoutY(720);
		btnCheck.setOnAction(e -> check());

		btnCall.setText("Call");
		btnCall.setLayoutX(400);
		btnCall.setLayoutY(720);
		btnCall.setOnAction(e -> call());

		btnRaise.setText("Raise");
		btnRaise.setLayoutX(500);
		btnRaise.setLayoutY(720);
		btnRaise.setOnAction(e -> raise());

		inputBetAmount.setPromptText("Bet amount");
		inputBetAmount.setLayoutX(575);
		inputBetAmount.setLayoutY(720);
		//make sure only integers are put in
		inputBetAmount.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(!newValue.matches("^[0-9]*$")) {
					inputBetAmount.setText(oldValue);
				}
			}
		});

		btnFold.setText("Fold");
		btnFold.setLayoutX(800);
		btnFold.setLayoutY(720);
		btnFold.setOnAction(e -> fold());

		btnTest.setText("~TEST BUTTON~");
		btnTest.setLayoutX(900);
		btnTest.setLayoutY(720);
		btnTest.setOnAction(e -> test()); 

		btnExit.setVisible(true);
		btnCheck.setVisible(true);
		btnCall.setVisible(true);
		btnRaise.setVisible(true);
		btnFold.setVisible(true);
		btnTest.setVisible(true); 

		root2.getChildren().add(canvas2);
		root2.getChildren().add(txtNotify);
		root2.getChildren().add(txtNotify2);
		root2.getChildren().add(btnExit);
		root2.getChildren().add(btnCheck);
		root2.getChildren().add(btnCall);
		root2.getChildren().add(btnRaise);
		root2.getChildren().add(btnFold);
		root2.getChildren().add(btnTest);
		root2.getChildren().add(timer);
		root2.getChildren().add(inputBetAmount);
		root2.getChildren().add(txtPot);
			//root.getChildren().add(txtPlayerChips[0]);
			//root.getChildren().add(txtPlayerChips[1]);
		for(int i = 0; i < 10; i++) {
			root2.getChildren().add(txtPlayerChips[i]);
		}
		//root.getChildren().add(timer);

		primaryStage.setScene(scene2);
		primaryStage.show();
		displayNotification(txtNotify, txtNotify2, "");

		renderGameScreen(gc2);
		testGame(gc2);
	}

	public void testGame(GraphicsContext gc) {
		renderGameScreen(gc);
		//renderGameScreen(gc2);
		//table.render(gc2);
		//player.renderHand(gc2);

		/*try {
				 table = (Table) fromServer.readObject(); //Table with blank cards for opponents
				 player.setCard((Card) fromServer.readObject());
				 player.setCard((Card) fromServer.readObject());
				 table = (Table) fromServer.readObject(); //Table with flop + blank cards for opponents
			//table = (Table) fromServer.readObject(); //Table with flop + turn + blank cards for opponents
			//table = (Table) fromServer.readObject(); //Table with flop + turn + river + blank cards for opponents

			table.render(gc);
			player.renderHand(gc);
			//recieveObjects();
		} catch (Exception ex) {
		}*/  
	}

	public void exit() {
		System.exit(1);
	}
}
