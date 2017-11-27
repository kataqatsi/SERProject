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
	private boolean gameOver = false;
	private int time = 20;
	private Send send;
	private Table table;
	private Button btnExit = new Button();
    private Button btnCheck = new Button();
    private Button btnCall = new Button();
    private Button btnRaise = new Button();
    private Button btnFold = new Button();
    private Button btnTest = new Button();
    private Text txtNotify = new Text();
    private Text txtNotify2 = new Text(); 
    private Text timer = new Text();
	
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
        
        btnFold.setText("Fold");
        btnFold.setLayoutX(600);
        btnFold.setLayoutY(720);
        btnFold.setOnAction(e -> fold());
        
        btnTest.setText("Check Score");
        btnTest.setLayoutX(800);
        btnTest.setLayoutY(720);
        btnTest.setOnAction(e -> test());   
       
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
        		table = (Table) fromServer.readObject(); //Table with blank cards for opponents
        		player.setCard((Card) fromServer.readObject());
			player.setCard((Card) fromServer.readObject());
			table = (Table) fromServer.readObject(); //Table with flop + blank cards for opponents
			table = (Table) fromServer.readObject(); //Table with flop + turn + blank cards for opponents
			table = (Table) fromServer.readObject(); //Table with flop + turn + river + blank cards for opponents
	        HandScore score;
			table.render(gc);
            player.renderHand(gc);
	        
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
    
    public static void connectToServer() {
    		try {
    			Socket socket = new Socket(host, 8000);
    			fromServer = new ObjectInputStream(socket.getInputStream());
    			toServer = new ObjectOutputStream(socket.getOutputStream());
    			
    			//Get Seat
    			int seatNum = fromServer.readInt();
    			player = new Player(seatNum);
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
	
	private void check() {
		displayNotification(txtNotify, txtNotify2, "You Check Your Hand");
		send = new Send(CHECK);
	}
	
	private void call() {
		if (myTurn == true) {
			displayNotification(txtNotify, txtNotify2, "You Call the Bet");
			send = new Send(CALL);
		}	else {
			displayNotification(txtNotify, txtNotify2, "It is not your turn yet");
		}
	}
	
	private void test() {
		HandScore score;
		score = new HandScore(player.getCard1(),player.getCard2(), table.flop[0], table.flop[1], table.flop[2], table.turn, table.river);
		player.setScore(score.getScore());
		displayNotification(txtNotify, txtNotify2, Integer.toString(player.getScore()));
	}
	
	private void fold() {
		displayNotification(txtNotify, txtNotify2, "You Have Folded");
        send = new Send(FOLD);
	}
	
	private void raise() {
		displayNotification(txtNotify, txtNotify2, "You Raise the Bet");
		send = new Send(RAISE);
	}
	
	private void exit() {
		System.exit(1);
	}
}