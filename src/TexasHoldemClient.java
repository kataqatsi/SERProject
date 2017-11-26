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
	private boolean myTurn = false;
	private Player player;
	private boolean gameOver = false;
	private int time = 20;
	private Send send;
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
        root.getChildren().add(inputBetAmount);
        //root.getChildren().add(timer);
        
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
	   
	public void recieveObjects() {
		boolean objectRecieved = false;
		while(!objectRecieved) {
			try {
				System.out.println("waiting for table");
				table = (Table) fromServer.readObject(); //Table with blank cards for opponents
				System.out.println("table recieved\nwaiting for player");
				player = new Player();
				player.printout();
				player = (Player) fromServer.readObject();//recieve the player info from the server
				System.out.println("player recieved");
				player.printout();
				System.out.println();
				objectRecieved = true;
			} catch (IOException ex) {
				System.out.println("test4");
				objectRecieved = false;
			} catch (Exception ex) {
				System.out.println("test3");
				objectRecieved = false;
			}
		}

		renderGameScreen(gc);
		table.render(gc);
    player.renderHand(gc);
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
    		t1.setX(375);
        t1.setY(75);
        t1.setCache(true);
        t1.setText(text);
        t1.setFill(Color.RED);
        t1.setFont(Font.font(null, FontWeight.BOLD, 56));
        t1.setEffect(new GaussianBlur());
        
        t2.setX(375);
        t2.setY(75);
        t2.setCache(true);
        t2.setText(text);
        t2.setFill(Color.WHITE);
        t2.setFont(Font.font(null, FontWeight.BOLD, 56));       
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
          //player = new Player(seatNum);
    		} catch (Exception ex) {
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
		recieveObjects();
	}
	
	private void call() {
		//if (myTurn == true) {
			displayNotification(txtNotify, txtNotify2, "You Call the Bet");
			//sendTurn(new Send(CALL));
			send = new Send(CALL);
		//}	else {
			//displayNotification(txtNotify, txtNotify2, "It is not your turn yet");
		//}
		recieveObjects();
	}
	
	public void test() {
        if (myTurn == true) {
	        displayNotification(txtNotify, txtNotify2, "It's Your Turn!");
	        myTurn = false;
        } else {
        		displayNotification(txtNotify, txtNotify2, "Wait for your turn...");
        		myTurn = true;
        }	
	}
	
	public void fold() {
		displayNotification(txtNotify, txtNotify2, "You Have Folded");
		//sendTurn(new Send(FOLD));
    send = new Send(FOLD);
		recieveObjects();
	}
	
	public void raise() {
		displayNotification(txtNotify, txtNotify2, "You Raise the Bet");
		if(inputBetAmount.getText().isEmpty()) {
			call();//if you didn't input anything, then just call
		} else {
			//sendTurn(new Send(RAISE, Integer.parseInt(inputBetAmount.getText())));
			send = new Send(RAISE, Integer.parseInt(inputBetAmount.getText()));
		}
		recieveObjects();
	}


	private void sendTurn(Send send) {
		boolean didSend	= false;
		int count = 0;
		while (!didSend && count < 10) {
			try {
				toServer.writeObject(send);
				didSend = true;
				System.out.println("turn sent");
			} catch (Exception ex) {
				System.out.println("failed to send I guess");
			}
			count++;
		}
	}

	private class RefreshGameWindow {
		private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		public void refreshGameWindowMethod() {
			final Runnable refresher = new Runnable() {
				public void run() {
					renderGameScreen(gc);
					table.render(gc);
					player.renderHand(gc);
					System.out.println("screen refreshed"); }
			};
			final ScheduledFuture<?> refreshHandle = scheduler.scheduleAtFixedRate(refresher, 1, 1, SECONDS);//run every second
			scheduler.schedule(new Runnable() {
				public void run() { refreshHandle.cancel(true); }
			}, 60 * 60 * 60, SECONDS); //run for 60 hours
		}
	}
	
	public void joinGame() {
		player = new Player();
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
        
        EventHandler<ActionEvent> eventHandler = e -> {
            if (time == 10) {
            		timer.setFill(Color.ORANGE);
            }
            if (time == 5) {
            		timer.setFill(Color.RED);
            }
            timer.setText("" + time);
            if (time == 0) {
            		timer.setText("END");
            		time = 21;
            		timer.setFill(Color.YELLOW);
            		send = new Send(TIMEISUP);
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
      
        primaryStage.setScene(scene2);
        primaryStage.show();
        displayNotification(txtNotify, txtNotify2, "");
        
        renderGameScreen(gc2);
        testGame(gc2);
	}
	
	public void testGame(GraphicsContext gc) {
		renderGameScreen(gc);
        
        try {
        		table = (Table) fromServer.readObject(); //Table with blank cards for opponents
        		player.setCard((Card) fromServer.readObject());
			player.setCard((Card) fromServer.readObject());
			table = (Table) fromServer.readObject(); //Table with flop + blank cards for opponents
			//table = (Table) fromServer.readObject(); //Table with flop + turn + blank cards for opponents
			//table = (Table) fromServer.readObject(); //Table with flop + turn + river + blank cards for opponents
			
			table.render(gc);
            player.renderHand(gc);
        } catch (Exception ex) {
        	
        }  
	}
	
	public void exit() {
		System.exit(1);
	}
}
