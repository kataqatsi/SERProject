import java.io.IOException;
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
	private boolean waiting = true;
	private boolean myTurn = false;
	private static Player player;
	private static Player otherPlayer;
	private boolean gameOver = false;
	private int time = 20;
	private Send send;
	
	public static void main(String[] args) {
		//Connect to server
		connectToServer();
		//Launch JavaFX Game
		launch(args);
	}
	    public void start(Stage primaryStage) {
	    
	    	
	        primaryStage.setTitle("Texas Hold'em");
	        Canvas canvas = new Canvas(1231,781);
	        GraphicsContext gc = canvas.getGraphicsContext2D();
	        Group root = new Group();
	        Scene scene = new Scene(root, 1231, 781);

	       //Set Buttons 
	        Button btnExit = new Button();
	        Button btnCheck = new Button();
	        Button btnCall = new Button();
	        Button btnRaise = new Button();
	        Button btnFold = new Button();
	        Button btnTest = new Button();
	        Text txtNotify = new Text();
	        Text txtNotify2 = new Text(); 
	        Text timer = new Text();
	       
	        
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
	        btnExit.setOnAction(new EventHandler<ActionEvent>() {
	        		@Override 
	        		public void handle(ActionEvent event) {
	                System.exit(1);
	            }
	        });
	        
	        btnCheck.setText("Check");
	        btnCheck.setLayoutX(300);
	        btnCheck.setLayoutY(720);
	        btnCheck.setOnAction(new EventHandler<ActionEvent>() {
	        		@Override
	            public void handle(ActionEvent event) {
	        			displayNotification(txtNotify, txtNotify2, "You Check Your Hand");
	        			send = new Send(CHECK);
	            }
	        });
	     
	        btnCall.setText("Call");
	        btnCall.setLayoutX(400);
	        btnCall.setLayoutY(720);
	        btnCall.setOnAction(new EventHandler<ActionEvent>() {
	        		@Override
	            public void handle(ActionEvent event) {
	        			if (myTurn == true) {
	        				displayNotification(txtNotify, txtNotify2, "You Call the Bet");
	        				send = new Send(CALL);
	        			}	else {
	        				displayNotification(txtNotify, txtNotify2, "It is not your turn yet");
	        			}
	            }
	        });
	       
	        btnRaise.setText("Raise");
	        btnRaise.setLayoutX(500);
	        btnRaise.setLayoutY(720);
	        btnRaise.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
		            	displayNotification(txtNotify, txtNotify2, "You Raise the Bet");
		            	send = new Send(RAISE);
	            }
	        });
	        
	        btnFold.setText("Fold");
	        btnFold.setLayoutX(600);
	        btnFold.setLayoutY(720);
	        btnFold.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	                displayNotification(txtNotify, txtNotify2, "You Have Folded");
	                send = new Send(FOLD);
	            }
	        });
	        
	        btnTest.setText("~TEST BUTTON~");
	        btnTest.setLayoutX(800);
	        btnTest.setLayoutY(720);
	        btnTest.setOnAction(new EventHandler<ActionEvent>() {
	        		@Override 
	        		public void handle(ActionEvent event) {
	                if (myTurn == true) {
				        displayNotification(txtNotify, txtNotify2, "It's Your Turn!");
	                } else {
	                		displayNotification(txtNotify, txtNotify2, "Wait for your turn...");
	                		myTurn = true;
	                }
	            }
	        });   
	       
	        root.getChildren().add(canvas);
	        root.getChildren().add(txtNotify);
	        root.getChildren().add(txtNotify2);
	        root.getChildren().add(btnExit);
	        root.getChildren().add(btnCheck);
	        root.getChildren().add(btnCall);
	        root.getChildren().add(btnRaise);
	        root.getChildren().add(btnFold);
	        root.getChildren().add(btnTest);
	        root.getChildren().add(timer);
	        
	        primaryStage.setScene(scene);
	        primaryStage.show();
	        
	        renderGameScreen(gc);
	        
	        try {
	        		player.setCard((Card) fromServer.readObject());
				player.setCard((Card) fromServer.readObject());
				otherPlayer.setCard((Card) fromServer.readObject());
				otherPlayer.setCard((Card) fromServer.readObject());
	            player.renderHand(gc);
	            otherPlayer.renderHand(gc);
	        } catch (Exception ex) {
	        	
	        }
	        
	          
	    }
	   
	    public void incrementPlayerCount() {
	    		playerCount++;
	    }
	    
	    public void renderGameScreen(GraphicsContext gc) {
	    		Image table = new Image("poker_table.png");
	    		gc.drawImage(table, 0, 0);
	    }
	    
	    
	    public void displayNotification(Text t1, Text t2, String text) {
	    		t1.setX(350);
	        t1.setY(400);
	        t1.setCache(true);
	        t1.setText(text);
	        t1.setFill(Color.RED);
	        t1.setFont(Font.font(null, FontWeight.BOLD, 56));
	        t1.setEffect(new GaussianBlur());
	        
	        t2.setX(405);
	        t2.setY(395);
	        t2.setCache(true);
	        t2.setText(text);
	        t2.setFill(Color.WHITE);
	        t2.setFont(Font.font(null, FontWeight.BOLD, 56));       
	    }
	    
	    public static void connectToServer() {
	    		try {
	    			Socket socket = new Socket(host, 8000);
	    			fromServer = new ObjectInputStream(socket.getInputStream());
	    			toServer = new ObjectOutputStream(socket.getOutputStream());
	    			
	    			//Get Seat
	    			int seatNum = fromServer.readInt();
	    			player = new Player(seatNum);
	    			
	    			//Get other Player's seat
	    			seatNum = fromServer.readInt();
	    			otherPlayer = new Player(seatNum);
	    		} catch (Exception ex) {
	    			System.err.println(ex);
	    		} 
	    }	
		
		private void waitForPlayerAction() throws InterruptedException {
		    while (waiting) {
		      Thread.sleep(100);
		    }
		    waiting = true;
		}
		
		private void receiveInfoFromServer() throws IOException {
			
		}
		
		private void recieveAction() throws IOException {
			
		}
		
}