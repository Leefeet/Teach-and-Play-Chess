package game;

import javax.print.attribute.standard.MediaSize.Other;
import javax.swing.GroupLayout.Alignment;

import com.sun.media.jfxmedia.track.SubtitleTrack;

import game.Space.DisplayColor;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class ChessGame extends Application
{
	//the title of the program window
	private String mTitle = "Lee's Chess Game";
	
	//the players in the game
	private Player mPlayers[];
	
	//the stage that's being shown
	private Stage mPrimaryStage = null;
	
	//The scene for the main menu
	private Scene mMainMenu = null;
	
	//The feedback text for the board game
	private Text mTextFeedback = null;
	
	//fonts
	private Font fontTitle = Font.font("Calibri", 35);
	private Font fontSubTitle = Font.font("Calibri", 25);
	private Font fontSmallTitle = Font.font("Calibri", 18);
	private Font fontButton = Font.font("Calibri", 20);
	private Font fontOther = Font.font("Calibri", 13);

	
	public static void main(String args[])
	{
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		//creating 2 players
		mPlayers = new Player[2];
		mPlayers[0] = new Player();
		mPlayers[1] = new Player();
		
		//setting the primary stage
		mPrimaryStage = primaryStage;
		
		//Getting the main menu
		mMainMenu = constructMainMenu();
		
		//setting main menu as the starting scene
		mPrimaryStage.setScene(mMainMenu);
						
		//setting title of mPrimary Stage
		mPrimaryStage.setTitle(mTitle);
		
		primaryStage.show();
		
		//constructAndDisplayGameBoard();
	}
	
	public Scene constructMainMenu()
	{
		Scene mainMenu = null; //will be set later
		
		//we need a pane
		VBox root = new VBox();
		
		//centering elements
		root.setAlignment(Pos.CENTER);
		
		//coloring background
		root.setBackground(new Background(new BackgroundFill(Color.BISQUE, null, Insets.EMPTY)));
		
		//setting default spacing
		root.setSpacing(5);
		
		//creating Title text
		Text textTitle = new Text("Welcome to Lee's Chess!");
		textTitle.setFont(fontTitle);
		root.getChildren().add(textTitle);
		
		//creating inputs for the first and second player
		//	Each one gets a row as well
		
		//Text title for player 1
		Text textPlayer1 = new Text("Player 1 (Black)");
		textPlayer1.setFont(fontSmallTitle);
		root.getChildren().add(textPlayer1);
		
		HBox rowPlayer1 = new HBox();
		rowPlayer1.setAlignment(Pos.CENTER);
		rowPlayer1.setSpacing(10);
		
		Text textPlayer1Name = new Text("Name:");
		textPlayer1Name.setFont(fontOther);
		rowPlayer1.getChildren().add(textPlayer1Name);
		
		TextField fieldPlayer1Name = new TextField();
		fieldPlayer1Name.setPromptText("Enter Name");
		rowPlayer1.getChildren().add(fieldPlayer1Name);
		
		root.getChildren().add(rowPlayer1);

		
		//Text title for player 2
		Text textPlayer2 = new Text("Player 2 (White)");
		textPlayer2.setFont(fontSmallTitle);
		root.getChildren().add(textPlayer2);
		
		HBox rowPlayer2 = new HBox();
		rowPlayer2.setAlignment(Pos.CENTER);
		rowPlayer2.setSpacing(10);
		
		Text textPlayer2Name = new Text("Name:");
		textPlayer2Name.setFont(fontOther);
		rowPlayer2.getChildren().add(textPlayer2Name);
		
		TextField fieldPlayer2Name = new TextField();
		fieldPlayer2Name.setPromptText("Enter Name");
		rowPlayer2.getChildren().add(fieldPlayer2Name);
		
		root.getChildren().add(rowPlayer2);
		
		//adding spacing between this and bottom button
		rowPlayer2.setPadding(new Insets(0, 0, 20, 0));
		
		//extra text to ask to insert names (starts invisible)
		//	Comes after the button
		Text textWarning = new Text();
		textWarning.setFont(fontOther);
		textWarning.setFill(Color.RED);
		textWarning.setText("Please enter names for both players");
		textWarning.setVisible(false);
		
		//final button that starts the game
		Button btnStart = new Button();
		btnStart.setText("Start Game");
		btnStart.setFont(fontButton);
		btnStart.setOnAction((ActionEvent e) -> 
		{
			boolean canStart = true;
			
			//check if both names have been filled in
			if (fieldPlayer1Name.getText().isEmpty())
			{
				//setting prompt text
				fieldPlayer1Name.setPromptText("*Must Enter Name*");
				
				canStart = false;
			}
			if (fieldPlayer2Name.getText().isEmpty())
			{
				//setting promt text
				fieldPlayer2Name.setPromptText("*Must Enter Name*");
				
				canStart = false;
			}
			
			//if both names are present, construct player names and create board
			if (canStart)
			{
				mPlayers[0].setName(fieldPlayer1Name.getText());
				
				mPlayers[1].setName(fieldPlayer2Name.getText());
				
				constructAndDisplayGameBoard();
			}
			else if (!textWarning.isVisible())
			{
				textWarning.setVisible(true);
			}
		});
		root.getChildren().add(btnStart);
		
		//adding warning text below button
		root.getChildren().add(textWarning);
		
		//making scene
		mainMenu = new Scene(root, 400, 300);
		
		return mainMenu;
	}
	
	public void constructAndDisplayGameBoard()
	{
		//Pane to hold the game
		FlowPane pane = new FlowPane();
		pane.setAlignment(Pos.TOP_CENTER);
		pane.setBackground(new Background(new BackgroundFill(Color.BISQUE, null, Insets.EMPTY)));
		
		//creating a board and inserting players
		Board theBoard = new Board(this, mPlayers);
		
		//adding board to pane
		pane.getChildren().add(theBoard.getGridPane());
		
		//Text element to give text feedback to users
		Text textFeedback = new Text();
		mTextFeedback = textFeedback;
		textFeedback.setFont(fontSubTitle);
		textFeedback.setTextAlignment(TextAlignment.CENTER);
		textFeedback.setText(mPlayers[0].getName() + ", you go first. \nClick on a piece you would like to move.");
		pane.getChildren().add(textFeedback);
		
		//creating scene for the game
		Scene scene = new Scene(pane, 800, 800);
				
		//showing scene on the stage
		mPrimaryStage.setScene(scene);
	}
	
	public Text getTextFeedback() { return mTextFeedback; }
	
	//if -1 is passed in, then the game ended in a tie
	public void displayWinner(int winningPlayer)
	{
		//display elements
		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.setSpacing(10);
		root.setBackground(new Background(new BackgroundFill(Color.BISQUE, null, Insets.EMPTY)));
		
		Text message = new Text();
		message.setFont(fontSmallTitle);
		message.setWrappingWidth(280);
		root.getChildren().add(message);
		
		Button btnEnd = new Button();
		btnEnd.setText("End Game");
		btnEnd.setFont(fontButton);
		root.getChildren().add(btnEnd);
		
		//if -1, then it was a tie
		if (winningPlayer == -1)
		{
			message.setText("The game has ended in a draw because the current player has no valid move but also is not in Check");
		}
		else
		{
			message.setText("Check Mate! " + mPlayers[winningPlayer].getName() + " has won the game!");
		}
		
		//creating a scene
		Scene scene = new Scene(root, 300, 250);
		
		//creating Modal window
		Stage stage = new Stage();
		stage.initStyle(StageStyle.UTILITY);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(mPrimaryStage);
		stage.setScene(scene);
		stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent e)
            {
    			//setting main window back to main window
    			mPrimaryStage.setScene(mMainMenu);
    			
    			//closing stage
    			stage.close();
            }
        });        
		
		btnEnd.setOnAction((ActionEvent e) ->
		{
			//setting main window back to main window
			mPrimaryStage.setScene(mMainMenu);
			
			//closing stage
			stage.close();
		});
	}
	
	public Stage getPrimaryStage() { return mPrimaryStage; }
}
