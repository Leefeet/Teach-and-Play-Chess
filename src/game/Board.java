package game;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import game.Space.DisplayColor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class Board
{
	//width of square board
	public final static int mSize = 8;
	
	//the players
	private Player mPlayers[];
	
	//index of the player that's in control
	private int mPlayerTurn = 0;
	
	//The width of each space
	public final static int mSpaceSize = 85;
	
	//array of board spaces
	private Space mSpaces[][];
	
	//array of game pieces
	private ArrayList<Piece> mPieces = new ArrayList<Piece>();
	
	//For simplicity, we'll also save both kings
	private King mKings[];
	
	//pane to hold the spaces
	private GridPane mGridPane = new GridPane();
	
	//link to the owning chess game
	private ChessGame mChessGame = null;
	
	//selected Piece and Space for Piece to move from
	private Space mSelectedSpace = null;
	private Piece mSelectedPiece = null;
//	//selected space to move Piece to
//	private Space mDestinationSpace = null;
	
	//selection mode
	private boolean mIsSelectingPiece = false;
	
	//King in Check state
	private boolean mKingIsInCheck = false;
	
	//Possible moves to make while in check
	HashMap<Piece, ArrayList<Space>> mPossibleMovesInCheck = new HashMap<Piece, ArrayList<Space>>();
	
	//stores the previous move
	private Space mPreviousOrigin = null;
	private Space mPreviousDestination = null;
		
	public Board(ChessGame game, Player[] players)
	{		
		mChessGame = game;
		
		//can only have two players
		if (players.length != 2)
		{
			System.out.println("Improper number of players loaded!");
			//TODO: Could turn this into an error (exception?)
		}
		
		//creating players
		mPlayers = players;
		
		mPlayers[0].setBoard(this);
		mPlayers[0].setPieceColor(DisplayColor.Black);
		
		mPlayers[1].setBoard(this);
		mPlayers[1].setPieceColor(DisplayColor.White);
		
		//constructing spaces
		mSpaces = new Space[mSize][mSize];
		
		//flipping boolean for color determination
		boolean isWhite = true;
		
		for (int i = 0; i < mSize; i++) // Rows 1 - 8
		{
			for (int j = 0; j < mSize; j++) // Columns a - h
			{
				if (isWhite)
				{
					mSpaces[j][i]  = new Space(this, i, j, DisplayColor.White, mSpaceSize);
				}
				else
				{
					mSpaces[j][i]  = new Space(this, i, j, DisplayColor.Black, mSpaceSize);
				}
				
				//only flip boolean if not last column
				if (j < mSize - 1)
				{
					isWhite = !isWhite;
				}
			}
		}
		
		//placing the spaces on the board
		for (int i = 0; i < mSize; i ++)
		{
			for (int j = 0; j < mSize; j++)
			{
				//adding to the gridPane
				mGridPane.add(mSpaces[j][i].getPane(), j, i);
			}
		}
		
		//placing pieces
		setUpBoard();
		
//		//getting potential space moves from bishop
//		ArrayList<Space> potMoves = bish.getPossibleMoves();
		
//		//printing moves to console
//		System.out.println("Piece Location: " + getSpaceCode(mSpaces[4][5]) + "\u2654");
//		for (Space s : potMoves)
//		{
//			System.out.println("Possible Location: " + getSpaceCode(s));
//		}
	}
		
	//copy constructor for cloning
	public Board(Board otherBoard)
	{
		//we need to make deep copies of everything. We cannot have references to the original objects
		
		// a lot of copy constructors will want "this" new board to be included
		
		//players
		mPlayers = new Player[2];
		
		mPlayers[0] = new Player(otherBoard.getPlayers()[0], this);
		mPlayers[1] = new Player(otherBoard.getPlayers()[1], this);
		
		//The player's turn
		mPlayerTurn = otherBoard.mPlayerTurn;
		
		//The array of board spaces
		//We need to completely recreate this
		mSpaces = new Space[mSize][mSize];
		
		//The Black King TODO: doesn't save normally
		King blackKing = null;
		
		//going through every space
		for (int i = 0; i < mSize; i++)
		{
			for (int j = 0; j < mSize; j++)
			{
				//copying space data
				mSpaces[j][i] = new Space(otherBoard.getSpaces()[j][i], this);
				
				//if space had a piece, then add to Piece array
				if (mSpaces[j][i].getPiece() != null)
				{
					mPieces.add(mSpaces[j][i].getPiece());
					
					//if the piece is a King, then add to King array
					if (mSpaces[j][i].getPiece() instanceof King)
					{
						//add the king to the array depending on color
						mKings = new King[2];
						
							//Cast Piece as a King in these cases
						if (mSpaces[j][i].getPiece().getColor() == DisplayColor.Black)
						{
							mKings[0] = (King)mSpaces[j][i].getPiece();
							blackKing = mKings[0];
						}
						else //White
						{
							mKings[1] = (King)mSpaces[j][i].getPiece();
						}
					}
				}
			}
		}
		
		//the owning chess game can remain the same
		mChessGame = otherBoard.getChessGame();
		
		//selected booleans can remain the same
		mSelectedSpace = otherBoard.mSelectedSpace;
		mSelectedPiece = otherBoard.mSelectedPiece;
		mIsSelectingPiece = otherBoard.mIsSelectingPiece;
		
		//making a new gridpane, which won't be used
		mGridPane = new GridPane();
		
		mKings[0] = blackKing;
	}
	
	public void setUpBoard()
	{
		//clearing board in case there's already peices on it
		for (int i = 0; i < mSize; i++) // Rows 1 - 8
		{
			for (int j = 0; j < mSize; j++) // Columns a - h
			{
				mSpaces[j][i].removePiece();
			}
		}
		
		//placing starting pieces in appropriate places
		
			// BLACK
		mSpaces[0][0].setPiece(new Rook(this, mSpaces[0][0], DisplayColor.Black));
		mSpaces[1][0].setPiece(new Knight(this, mSpaces[1][0], DisplayColor.Black));
		mSpaces[2][0].setPiece(new Bishop(this, mSpaces[2][0], DisplayColor.Black));
		mSpaces[3][0].setPiece(new Queen(this, mSpaces[3][0], DisplayColor.Black));
		King blackKing = new King(this, mSpaces[4][0], DisplayColor.Black);
		mSpaces[4][0].setPiece(blackKing);
		mSpaces[5][0].setPiece(new Bishop(this, mSpaces[5][0], DisplayColor.Black));
		mSpaces[6][0].setPiece(new Knight(this, mSpaces[6][0], DisplayColor.Black));
		mSpaces[7][0].setPiece(new Rook(this, mSpaces[7][0], DisplayColor.Black));
		
		for (int i = 0; i < mSize; i++)
		{
			mSpaces[i][1].setPiece(new Pawn(this, mSpaces[i][1], DisplayColor.Black));
		}
		
			// WHITE
		// BLACK
		mSpaces[0][7].setPiece(new Rook(this, mSpaces[0][7], DisplayColor.White));
		mSpaces[1][7].setPiece(new Knight(this, mSpaces[1][7], DisplayColor.White));
		mSpaces[2][7].setPiece(new Bishop(this, mSpaces[2][7], DisplayColor.White));
		mSpaces[3][7].setPiece(new Queen(this, mSpaces[3][7], DisplayColor.White));
		King whiteKing = new King(this, mSpaces[4][7], DisplayColor.White);
		mSpaces[4][7].setPiece(whiteKing);
		mSpaces[5][7].setPiece(new Bishop(this, mSpaces[5][7], DisplayColor.White));
		mSpaces[6][7].setPiece(new Knight(this, mSpaces[6][7], DisplayColor.White));
		mSpaces[7][7].setPiece(new Rook(this, mSpaces[7][7], DisplayColor.White));
		
		for (int i = 0; i < mSize; i++)
		{
			mSpaces[i][6].setPiece(new Pawn(this, mSpaces[i][6], DisplayColor.White));
		}
		
		//adding all pieces to array
		for (int i = 0; i < mSize; i++) // Rows 1 - 8
		{
			for (int j = 0; j < mSize; j++) // Columns a - h
			{
				if (mSpaces[j][i].getPiece() != null)
				{
					mPieces.add(mSpaces[j][i].getPiece());
				}
			}
		}
		
		//adding kings to member array
		mKings = new King[2];
		mKings[0] = blackKing;
		mKings[1] = whiteKing;
		
		//printing board to the console
		//printBoardToConsoleWithLines();
		
	}
	
	public void printBoardToConsole()
	{
		//Printing out each space
		for (int i = 0; i < mSize; i++) // Rows 1 - 8
		{
			System.out.println();
			for (int j = 0; j < mSize; j++) // Columns a - h
			{
				if (mSpaces[j][i].getPiece() != null)
				{
					System.out.print(" " + mSpaces[j][i].getPiece().getUnicodeCharacter());
				}
				else
				{
					System.out.print(" " + " ");
				}
			}
		}
		//sending new line
		System.out.println();
	}
	
	public void printBoardToConsoleWithLines()
	{
		//top line
		System.out.print("╔");
		for (int i = 0; i < mSize - 1; i++)
		{
			System.out.print("═══╤");
		}
		System.out.print("═══╗");
		System.out.println();
		
		//adding inside spaces
		for (int i = 0; i < mSize; i++) // Rows 1 - 8
		{
			//starting with left side
			System.out.print("║");
			
			for (int j = 0; j < mSize; j++) // Columns a - h
			{
				if (mSpaces[j][i].getPiece() != null)
				{
					System.out.print(" " + mSpaces[j][i].getPiece().getUnicodeCharacter() + " ");
					if (j < mSize - 1)
					{
						System.out.print("│");
					}
				}
				else
				{
					System.out.print("  " + " ");
					if (j < mSize - 1)
					{
						System.out.print("│");
					}
				}
			}
			//ending with right side
			System.out.print("║");
			
			//sending a new line
			System.out.println();
			
			//printing line between row of spaces
			if (i < mSize - 1) //not last line
			{
				System.out.print("╟");
				for (int j = 0; j < mSize - 1; j++)
				{
					System.out.print("───┼");
				}
				System.out.print("───╢");
				
				//new line
				System.out.println();
			}
			else //last line
			{
				System.out.print("╚");
				for (int j = 0; j < mSize - 1; j++)
				{
					System.out.print("═══╧");
				}
				System.out.print("═══╝");
				
				//new line
				System.out.println();
			}
		}
		//sending new line
		System.out.println();
	}

	public Space[][] getSpaces() { return mSpaces; }
	
	//performs an action based on the space clicked
	public void spaceClicked(Space spaceClicked)
	{
		//if in check, do specialized actions
		if (mKingIsInCheck)
		{
			clickActionsCheck(spaceClicked);
			
			//also act as hover, but only if not the destination space
			if (spaceClicked != mPreviousDestination)
			{
				hoverActionsCheck(spaceClicked);
			}
		}
		else //not in check
		{
			clickActionsNormal(spaceClicked);
			
			//also act as hover if not the destination space
			if (spaceClicked != mPreviousDestination)
			{
				hoverActionsNormal(spaceClicked);
			}
		}		
	}
	
	//performs an action when a space is hovered over
	public void spaceHovered(Space spaceHovered)
	{
		//if in check, do specialized actions
		if (mKingIsInCheck)
		{
			hoverActionsCheck(spaceHovered);
		}
		else //not in check
		{
			hoverActionsNormal(spaceHovered);
		}		
	}
	
	//split function based on whether the game is in a CHECK state
	private void clickActionsNormal(Space spaceClicked)
	{
		System.out.println("Space was clicked: " + getSpaceCode(spaceClicked));
		
		//TODO: Strange issue where if piece is selected, then another piece from the opposite player is selected, the reselecting the previous piece will take two clicks
		//Done: Check and make sure the player's move won't leave the King in check. Cannot display those options, grey them out
		//Done: If the current player's King is NOT in check and has no valid moves, the game ends in a stalemate(?) or tie
		//Done: Check for Promotion and bring out slection window the the pawn piece
		
		//if space is considered a destination, player may be trying to move a piece there
		if (spaceClicked.getIsPotentialMoveDestination() == true)
		{
			selectDestination(spaceClicked);
			
			//printBoardToConsole();
			
			//Changing feedback text
			//if in check, display this message
			if (mKingIsInCheck)
			{
				//Changing feedback text
				mChessGame.getTextFeedback().setText(mPlayers[mPlayerTurn].getName() + ", your King is in CHECK!" + "\n" +
						"The Yellow squares indicate pieces with" + "\n" + "moves that can protect your King.");
			}
			else
			{
				mChessGame.getTextFeedback().setText(mPlayers[mPlayerTurn].getName() + ", it's your turn." + "\n" +
					"Please select a piece to move.");
			}
			
			return;
		}
		
		//if selecting the already selected piece, reset the display
		if (spaceClicked == mSelectedSpace)
		{
			resetSpacesDisplay();
			
			//resetting the selected space and piece
			mSelectedSpace = null;
			mSelectedPiece = null;
			
			//Changing feedback text
			mChessGame.getTextFeedback().setText(mPlayers[mPlayerTurn].getName() + ", it's your turn." + "\n" +
					"Please select a piece to move.");
			
			//highlighting the previos move
			//	first make sure they aren't null
			if (mPreviousDestination != null && mPreviousOrigin != null)
			{
				mPreviousDestination.setDisplayMoved();
				mPreviousOrigin.setDisplayMoved();
			}
			
			return;
		}
		
		//resetting the colors of the spaces
		resetSpacesDisplay();
		
		//highlighting the previos move
		//	first make sure they aren't null
		if (mPreviousDestination != null && mPreviousOrigin != null)
		{
			mPreviousDestination.setDisplayMoved();
			mPreviousOrigin.setDisplayMoved();
		}
				
		//if space has a piece, may be trying to select that piece for movement
		if (spaceClicked.getPiece() != null)
		{
			if (spaceClicked.getPiece().getColor() == mPlayers[mPlayerTurn].getPieceColor())
			{
				selectPiece(spaceClicked);
				
				//changing feedback text
				mChessGame.getTextFeedback().setText(mPlayers[mPlayerTurn].getName() + " selected " + spaceClicked.getPiece().getUnicodeCharacter() + 
						". Now select a destination space." + "\n" + "Click anywhere else to cancel.");
			}
			else //deselect the current piece if selected other player's piece
			{
				mSelectedPiece = null;
				
				//Changing feedback text
				if (mPlayers[mPlayerTurn].getPieceColor() == DisplayColor.White)
				{
					mChessGame.getTextFeedback().setText("That's not your piece " + mPlayers[mPlayerTurn].getName() + ".\n" + 
						"Please select a " + "White" + " piece to move.");
				}
				else //black
				{
					mChessGame.getTextFeedback().setText("That's not your piece " + mPlayers[mPlayerTurn].getName() + ".\n" + 
						"Please select a " + "Black" + " piece to move.");
				}
			}
		}
		//if nothing else, the spaces on the board would have been reset
		else
		{
			deselectPiece(spaceClicked);
			
			//Changing feedback text
			mChessGame.getTextFeedback().setText(mPlayers[mPlayerTurn].getName() + ", it's your turn." + "\n" +
					"Please select a piece to move.");
		}
	}
	private void clickActionsCheck(Space spaceClicked)
	{
		//since we're in check, the player will only have a handful of moves to make
		//	The player must make one of these moves, so the potential moves are different than normal
		
		//resetting the colors of the spaces
		resetSpacesDisplay();
		
		//first, we must highlight all possible, movable pieces with yellow
		for (Piece p : mPossibleMovesInCheck.keySet())
		{
			p.getSpace().setDisplayCheckSave();
		}
		
		//Space clicked
		System.out.println("Space was clicked: " + getSpaceCode(spaceClicked));
				
		//if space is considered a destination, player may be trying to move a piece there
			//first check if selecting a piece to move, then see if location is within the possible moves
		if (!mIsSelectingPiece && mSelectedPiece != null && mPossibleMovesInCheck.get(mSelectedPiece).contains(spaceClicked))
		{
			selectDestination(spaceClicked);
			
			//printBoardToConsole();
			
			//Changing feedback text
			mChessGame.getTextFeedback().setText(mPlayers[mPlayerTurn].getName() + ", it's your turn." + "\n" +
					"Please select a piece to move.");
			
			return;
		}
		
		//if selecting the already selected piece, reset the display
		if (spaceClicked == mSelectedSpace)
		{
			resetSpacesDisplay();
			
			//BUT rehighlight possible moves as yellow
			for (Piece p : mPossibleMovesInCheck.keySet())
			{
				p.getSpace().setDisplayCheckSave();
			}
			
			//resetting the selected space and piece
			mSelectedSpace = null;
			mSelectedPiece = null;
			
			//Changing feedback text
			mChessGame.getTextFeedback().setText(mPlayers[mPlayerTurn].getName() + ", it's your turn." + "\n" +
					"Please select a piece to move.");
			
			return;
		}
						
		//if space has a piece, may be trying to select that piece for movement
		//	We're testing if the possible moves contains the piece
		if (mPossibleMovesInCheck.keySet().contains(spaceClicked.getPiece()))
		{
			if (spaceClicked.getPiece().getColor() == mPlayers[mPlayerTurn].getPieceColor())
			{
				selectPieceCheck(spaceClicked);
				
				//changing feedback text
				mChessGame.getTextFeedback().setText(mPlayers[mPlayerTurn].getName() + " selected " + spaceClicked.getPiece().getUnicodeCharacter() + 
						". Now select a destination space." + "\n" + "Click anywhere else to cancel.");
			}
			else //deselect the current piece if selected other player's piece
			{
				mSelectedPiece = null;
				
				//Changing feedback text
				if (mPlayers[mPlayerTurn].getPieceColor() == DisplayColor.White)
				{
					mChessGame.getTextFeedback().setText("That's not your piece " + mPlayers[mPlayerTurn].getName() + ".\n" + 
						"Please select a " + "White" + " piece to move.");
				}
				else //black
				{
					mChessGame.getTextFeedback().setText("That's not your piece " + mPlayers[mPlayerTurn].getName() + ".\n" + 
						"Please select a " + "Black" + " piece to move.");
				}
			}
		}
		//if nothing else, the spaces on the board would have been reset
		else
		{
			deselectPiece(spaceClicked);
			
			//Changing feedback text
			mChessGame.getTextFeedback().setText(mPlayers[mPlayerTurn].getName() + ", It's your turn." + "\n" +
					"Please select a piece to move.");
		}

	}
	
	private void hoverActionsNormal(Space spaceHovered)
	{		
		//clearing board
		resetSpacesDisplay();
		
		//highlighting the previos move
		//	first make sure they aren't null
		if (mPreviousDestination != null && mPreviousOrigin != null)
		{
			mPreviousDestination.setDisplayMoved();
			mPreviousOrigin.setDisplayMoved();
		}
		
		//if a piece is selected, show its moves
		if (mSelectedPiece != null)
		{
			//coloring its space
			mSelectedSpace.setDisplaySelected();
			
			//coloring all potential move spaces
			ArrayList<Space> potentialMoveSpaces = mSelectedPiece.getPossibleMoves();
			
			//getting list of moves removing the potential King CHECKs
			ArrayList<Space> validMoves = mSelectedPiece.removeMovesThatCreateCheck(potentialMoveSpaces, mPlayerTurn);
			
			for (Space s : potentialMoveSpaces)
			{
				//if the space is not in valid moves, then grey it out and don't make it selectable
				if (!validMoves.contains(s))
				{
					s.setIsPotentialMoveDestination(false);
					
					s.setDisplayDisabled();
				}
				else //make it a valid option
				{
					//setting all spaces as potential destinations
					s.setIsPotentialMoveDestination(true);
					
					s.setDisplayDestination();
					
					//if the space has an enemy on it, make it look different
					if (s.getPiece() != null)
					{
						if (s.getPiece().getColor() != mSelectedPiece.getColor())
						{
							s.setDisplayEnemy();
						}
					}
				}
			}
		}
		
		//if hovering over the selected piece, then skip
		if (spaceHovered == mSelectedSpace)
		{
			return;
		}
		
		//if the hovered space has a piece, then show its moves
		if (spaceHovered.getPiece() != null)
		{
			//	Depending if it's an enemy or ally, display differently
			//	Ally
			if (spaceHovered.getPiece().getColor() == mPlayers[mPlayerTurn].getPieceColor())
			{
				//coloring hovered space for ally
				spaceHovered.setDisplayHoveredAlly();
			}
			//	Enemy
			else
			{
				//coloring hovered space for Enemy
				spaceHovered.setDisplayHoveredEnemy();
			}
			
			//getting piece from space
			Piece p = spaceHovered.getPiece();
			
			//coloring all potential move spaces
			ArrayList<Space> potentialMoveSpaces = p.getPossibleMoves();
			
			//getting list of moves removing the potential King CHECKs
				//if black, then player 0, else 1
			int playerTurn;
			if (p.getColor() == DisplayColor.Black)
			{
				playerTurn = 0;
			}
			else
			{
				playerTurn = 1;
			}
			ArrayList<Space> validMoves = p.removeMovesThatCreateCheck(potentialMoveSpaces, playerTurn);
			
			for (Space s : potentialMoveSpaces)
			{
				//if the space is not in valid moves, then grey it out and don't make it selectable
				if (!validMoves.contains(s))
				{
//					s.setIsPotentialMoveDestination(false);
//					
//					s.setDisplayDisabled();
				}
				else //make it a valid option
				{
					s.setDisplayPotentialMove();
					
					//if the space has an enemy piece, make it a potential kill
					if (s.getPiece() != null)
					{
						s.setDisplayPotentialKill();
					}
				}
			}

		}
	}
	
	private void hoverActionsCheck(Space spaceHovered)
	{
		//clearing board
		resetSpacesDisplay();
		
		//highlighting the previos move
		//	first make sure they aren't null
		if (mPreviousDestination != null && mPreviousOrigin != null)
		{
			mPreviousDestination.setDisplayMoved();
			mPreviousOrigin.setDisplayMoved();
		}
		
		//first, we must highlight all possible, movable pieces with yellow
		for (Piece p : mPossibleMovesInCheck.keySet())
		{
			p.getSpace().setDisplayCheckSave();
		}
		
		//if a piece is selected, show its moves
		if (mSelectedPiece != null)
		{
			//coloring its space
			mSelectedSpace.setDisplaySelected();
			
			//coloring all potential move spaces
			ArrayList<Space> potentialMoveSpaces = mSelectedPiece.getPossibleMoves();
			
			//getting list of moves removing the potential King CHECKs
			ArrayList<Space> validMoves = mSelectedPiece.removeMovesThatCreateCheck(potentialMoveSpaces, mPlayerTurn);
			
			for (Space s : potentialMoveSpaces)
			{
				//if the space is not in valid moves, then grey it out and don't make it selectable
				if (!validMoves.contains(s))
				{
					s.setIsPotentialMoveDestination(false);
					
					s.setDisplayDisabled();
				}
				else //make it a valid option
				{
					//setting all spaces as potential destinations
					s.setIsPotentialMoveDestination(true);
					
					s.setDisplayDestination();
					
					//if the space has an enemy on it, make it look different
					if (s.getPiece() != null)
					{
						if (s.getPiece().getColor() != mSelectedPiece.getColor())
						{
							s.setDisplayEnemy();
						}
					}
				}
			}
		}
		
		//if hovering over the selected piece, then skip
		if (spaceHovered == mSelectedSpace)
		{
			return;
		}
		
		//if the hovered space has a piece, then show its moves
		if (spaceHovered.getPiece() != null)
		{
			//if ally, get *Check* moves
			if (spaceHovered.getPiece().getColor() == mPlayers[mPlayerTurn].getPieceColor())
			{
				//coloring all potential move spaces
				ArrayList<Space> potentialMoveSpaces = mPossibleMovesInCheck.get(spaceHovered.getPiece());
				
				//if there are moves to display (so null since it wouldn't be withon the list)
				if (potentialMoveSpaces != null)
				{
					for (Space s : potentialMoveSpaces)
					{					
						s.setDisplayPotentialMove();;
						
						//if the space has an enemy on it, make it look different
						if (s.getPiece() != null)
						{
							if (s.getPiece().getColor() != spaceHovered.getPiece().getColor())
							{
								s.setDisplayPotentialKill();
							}
						}
					}
				}
			}
			//if enemy, just show all moves as normal
			else
			{
				//coloring hovered space for Enemy
				spaceHovered.setDisplayHoveredEnemy();
				
				//getting piece from space
				Piece p = spaceHovered.getPiece();
				
				//coloring all potential move spaces
				ArrayList<Space> potentialMoveSpaces = p.getPossibleMoves();
				
				for (Space s : potentialMoveSpaces)
				{
					s.setDisplayPotentialMove();
					
					//if the space has an enemy piece, make it a potential kill
					if (s.getPiece() != null)
					{
						s.setDisplayPotentialKill();
					}
				}
			}
		}
	}
	
	public void resetSpacesDisplay()
	{
		// resetting all spaces display
		for (int i = 0; i < mSize; i++) // Rows 1 - 8
		{
			for (int j = 0; j < mSize; j++) // Columns a - h
			{
				mSpaces[j][i].setDisplayNormal();
				mSpaces[j][i].setIsPotentialMoveDestination(false);
			}
		}
	}
	
	//processes piece for selection
	private void selectPiece(Space spaceClicked)
	{
		//setting the selected space and piece
		mSelectedSpace = spaceClicked;
		mSelectedPiece = spaceClicked.getPiece();
		
		//coloring selected space
		mSelectedSpace.setDisplaySelected();
		
		//coloring all potential move spaces
		ArrayList<Space> potentialMoveSpaces = mSelectedPiece.getPossibleMoves();
		
		//getting list of moves removing the potential King CHECKs
		ArrayList<Space> validMoves = mSelectedPiece.removeMovesThatCreateCheck(potentialMoveSpaces, mPlayerTurn);
		
		//if the piece has moves
		for (Space s : potentialMoveSpaces)
		{
			//if the space is not in valid moves, then grey it out and don't make it selectable
			if (!validMoves.contains(s))
			{
				s.setIsPotentialMoveDestination(false);
				
				s.setDisplayDisabled();
			}
			else //make it a valid option
			{
				//setting all spaces as potential destinations
				s.setIsPotentialMoveDestination(true);
				
				s.setDisplayDestination();
				
				//if the space has an enemy on it, make it look different
				if (s.getPiece() != null)
				{
					if (s.getPiece().getColor() != mSelectedPiece.getColor())
					{
						s.setDisplayEnemy();
					}
				}
			}
		}
		
		//flipping bool to off since should be selecting a destination
		mIsSelectingPiece = false;
	}
	private void selectPieceCheck(Space spaceClicked)
	{
		//setting the selected space and piece
		mSelectedSpace = spaceClicked;
		mSelectedPiece = spaceClicked.getPiece();
		
		//coloring selected space
		mSelectedSpace.setDisplaySelected();
		
		//coloring all *typical* moves grey, since most will be disabled likely
		for (Space s : mSelectedPiece.getPossibleMoves())
		{
			s.setDisplayDisabled();
		}
		
		//coloring all potential move spaces
		ArrayList<Space> potentialMoveSpaces = mPossibleMovesInCheck.get(mSelectedPiece);
		
		for (Space s : potentialMoveSpaces)
		{
			//setting all spaces as potential destinations
			s.setIsPotentialMoveDestination(true);
			
			s.setDisplayDestination();
			
			//if the space has an enemy on it, make it look different
			if (s.getPiece() != null)
			{
				if (s.getPiece().getColor() != mSelectedPiece.getColor())
				{
					s.setDisplayEnemy();
				}
			}
		}
		
		//flipping bool to off since should be selecting a destination
		mIsSelectingPiece = false;
	}

	
	private void deselectPiece(Space spaceClicked)
	{
		//resetting the selected space and piece
		mSelectedSpace = null;
		mSelectedPiece = null;
		
		//flipping bool to off since should be selecting a new piece
		mIsSelectingPiece = true;
		
		//highlighting the previos move
		//	first make sure they aren't null
		if (mPreviousDestination != null && mPreviousOrigin != null)
		{
			mPreviousDestination.setDisplayMoved();
			mPreviousOrigin.setDisplayMoved();
		}
	}
	
	private void generatePromotionScreen(Piece pawn)
	{
		System.out.print("PROMOTION!!!!!");
		
		//getting the primary stage
		Stage primaryStage = mChessGame.getPrimaryStage();
		
		//saving variables from pawn
		Space pawnSpace = pawn.getSpace();
		DisplayColor pawnColor = pawn.getColor();
		Board pawnBoard = pawn.getBoard();
		
		//killing the pawn
		pawn.killPiece();
		
		//display elements
		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.setSpacing(10);
		root.setBackground(new Background(new BackgroundFill(Color.BISQUE, null, Insets.EMPTY)));
		
		//Text
		Text textInformation = new Text();
		textInformation.setFont(Font.font("Calibri", 18));
		textInformation.setText("Your Pawn is ready for Promotion!" + "\n" + "Select a piece you wish it to become:");
		root.getChildren().add(textInformation);
		
		//Row of Pieces
		HBox row = new HBox();
		row.setAlignment(Pos.CENTER);
		
		//size of boxes
		final int boxSize = 80;
		
		//Queen
		Pane paneQueen = new Pane();
		paneQueen.setPrefSize(boxSize, boxSize);
		ImageView imageQueen = new ImageView();
		imageQueen.setImage(new Queen(null, null, mPlayers[mPlayerTurn].getPieceColor()).getSprite());
		imageQueen.setPreserveRatio(true);
		imageQueen.setFitWidth(boxSize);
		paneQueen.getChildren().add(imageQueen);
		row.getChildren().add(paneQueen);
		
		//Knight
		Pane paneKnight = new Pane();
		paneKnight.setPrefSize(boxSize, boxSize);
		ImageView imageKnight = new ImageView();
		imageKnight.setImage(new Knight(null, null, mPlayers[mPlayerTurn].getPieceColor()).getSprite());
		imageKnight.setPreserveRatio(true);
		imageKnight.setFitWidth(boxSize);
		paneKnight.getChildren().add(imageKnight);
		row.getChildren().add(paneKnight);

		//Rook
		Pane paneRook = new Pane();
		paneRook.setPrefSize(boxSize, boxSize);
		ImageView imageRook = new ImageView();
		imageRook.setImage(new Rook(null, null, mPlayers[mPlayerTurn].getPieceColor()).getSprite());
		imageRook.setPreserveRatio(true);
		imageRook.setFitWidth(boxSize);
		paneRook.getChildren().add(imageRook);
		row.getChildren().add(paneRook);

		//Bishop
		Pane paneBishop = new Pane();
		paneBishop.setPrefSize(boxSize, boxSize);
		ImageView imageBishop = new ImageView();
		imageBishop.setImage(new Bishop(null, null, mPlayers[mPlayerTurn].getPieceColor()).getSprite());
		imageBishop.setPreserveRatio(true);
		imageBishop.setFitWidth(boxSize);
		paneBishop.getChildren().add(imageBishop);
		row.getChildren().add(paneBishop);

		//adding row to HBOX
		root.getChildren().add(row);
		
		//creating a scene
		Scene scene = new Scene(root, 500, 250);
		
		//creating Modal window
		Stage stage = new Stage();
		stage.initStyle(StageStyle.UTILITY);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(primaryStage);
		
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent e)
            {
    			System.out.print("Closing window. Default to Queen.");
    			
    			//changing the pawn to a Queen
    			Queen proQueen = new Queen(pawnBoard, pawnSpace, pawnColor);
    			
    			//adding the piece to the piece array
    			pawnBoard.addPiece(proQueen);
    			
    			//setting space to display the queen
    			pawnSpace.setPiece(proQueen);
    			
    			//closing stage
    			stage.close();
            }
        });     
        
        //actions for each piece
        //	Queen
		paneQueen.addEventHandler((MouseEvent.MOUSE_CLICKED),(MouseEvent m) ->
		{
			System.out.print("Selected Queen. Creating Queen for player");
			
			//changing the pawn to a Queen
			Queen proQueen = new Queen(pawnBoard, pawnSpace, pawnColor);
			
			//adding the piece to the piece array
			pawnBoard.addPiece(proQueen);
			
			//setting space to display the queen
			pawnSpace.setPiece(proQueen);
			
			//closing stage
			stage.close();
		});
        //	Knight
		paneKnight.addEventHandler((MouseEvent.MOUSE_CLICKED),(MouseEvent m) ->
		{
			System.out.print("Selected Knight. Creating Knight for player");
			
			//changing the pawn to a Queen
			Knight proKnight = new Knight(pawnBoard, pawnSpace, pawnColor);
			
			//adding the piece to the piece array
			pawnBoard.addPiece(proKnight);
			
			//setting space to display the queen
			pawnSpace.setPiece(proKnight);
			
			//closing stage
			stage.close();
		});
        //	Rook
		paneRook.addEventHandler((MouseEvent.MOUSE_CLICKED),(MouseEvent m) ->
		{
			System.out.print("Selected Rook. Creating Rook for player");
			
			//changing the pawn to a Queen
			Rook proRook = new Rook(pawnBoard, pawnSpace, pawnColor);
			
			//adding the piece to the piece array
			pawnBoard.addPiece(proRook);
			
			//setting space to display the queen
			pawnSpace.setPiece(proRook);
			
			//closing stage
			stage.close();
		});
        //	Queen
		paneBishop.addEventHandler((MouseEvent.MOUSE_CLICKED),(MouseEvent m) ->
		{
			System.out.print("Selected Bishop. Creating Bishop for player");
			
			//changing the pawn to a Queen
			Bishop proBishop = new Bishop(pawnBoard, pawnSpace, pawnColor);
			
			//adding the piece to the piece array
			pawnBoard.addPiece(proBishop);
			
			//setting space to display the queen
			pawnSpace.setPiece(proBishop);
			
			//closing stage
			stage.close();
		});

		//displaying stage
		stage.setScene(scene);
		stage.showAndWait();        
	}
	
	private void selectDestination(Space spaceClicked)
	{
		//saving the origin space
		Space originSpace = mSelectedPiece.getSpace();
		
		//move the piece to that spot
		mSelectedPiece.movePiece(spaceClicked);
		
		//reset selection bool
		mIsSelectingPiece = true;
		
		//since we just moved the piece, deselect it
		deselectPiece(spaceClicked);
		
		//cleaning space display
		resetSpacesDisplay();
		
		//setting the origin and destination space blue
		spaceClicked.setDisplayMoved();
		originSpace.setDisplayMoved();
		
		//saving these spaces in member variables
		mPreviousOrigin = originSpace;
		mPreviousDestination = spaceClicked;
		
		
		//Pawn Promotion, determing if pawn needs to be promoted by reaching the edge
		if (spaceClicked.getPiece() instanceof Pawn)
		{
			//if the piece is a pawn, the test if it's at the end
			if (spaceClicked.getRow() == 0 || spaceClicked.getRow() == mSize - 1)
			{
				//the pawn is at the end, present promotion screen
				generatePromotionScreen(spaceClicked.getPiece());
			}
		}
		
		//saving previous player
		int previousPlayer = mPlayerTurn;
		
		//flipping player turn
		if (mPlayerTurn == 0)
		{
			mPlayerTurn = 1;
		}
		else
		{
			mPlayerTurn = 0;
		}
		
		//testing if next player's king is safe (Check)
		if (mKings[mPlayerTurn].determineIfInDanger())
		{
			System.out.println("PLAYER'S KING IS IN CHECK!!!!!!!");
						
			//setting boolean
			mKingIsInCheck = true;
			
			//testing if in checkmate
			mPossibleMovesInCheck.clear();
			mPossibleMovesInCheck = mKings[mPlayerTurn].determineIfCheckMate();
			
			if (mPossibleMovesInCheck == null) //there are no moves to make
			{
				System.out.println("King is in CHECK MATE!" + "\n\n" + "GAME OVER");
				
				//show window
				mChessGame.displayWinner(previousPlayer);
			}
			else //there are some moves the player could make to save the king
			{
				//System.out.println("Player can make the following moves to get out of check:");
				
				//printing out moves to save the King
				
				ArrayList<Piece> playerPieces;
				if (mPlayers[mPlayerTurn].getPieceColor() == DisplayColor.White)
				{
					playerPieces = getPiecesOnBoard(DisplayColor.White);
				}
				else //Black
				{
					playerPieces = getPiecesOnBoard(DisplayColor.Black);
				}
				
				//now that we have all the player's peices, let's go through Map
				for (Piece p : playerPieces)
				{
					//checking if the piece was in the map
					if (mPossibleMovesInCheck.containsKey(p))
					{
						//if inside, print out the piece and its destinations
						for (Space s : mPossibleMovesInCheck.get(p))
						{
//							System.out.println("The Move of " + p.getUnicodeCharacter() + " from space " +
//									getSpaceCode(p.getSpace()) + " to Space " + getSpaceCode(s) +
//									" can protect the King from Check");
						}
					}
				}
				
				//setting all piece spaces to yellow to indicate pieces that can move
				//first, we must highlight all possible, movable pieces with yellow
				for (Piece p : mPossibleMovesInCheck.keySet())
				{
					p.getSpace().setDisplayCheckSave();
				}
			}
		}
		else //if the king isn't in check, we must check if valid moves, or we need to clear the Map and set the boolean (stalemate?)
		{
			//checking if player has any valid moves
			boolean validMove = false;
			for (Piece p : getPiecesOnBoard(mPlayers[mPlayerTurn].getPieceColor()))
			{
				//removing moves that are in check mate
				ArrayList<Space> possibleMoves = p.removeMovesThatCreateCheck(p.getPossibleMoves(), mPlayerTurn);
				
				//if there's a valid move, then set true and end
				if (!possibleMoves.isEmpty())
				{
					validMove = true;
					break;
				}
			}
			
			//if no valid move, the game is at stalemate
			if (!validMove)
			{
				mChessGame.displayWinner(-1);
			}
			else //otherwise, just clear
			{
				mKingIsInCheck = false;
				
				mPossibleMovesInCheck.clear();
			}
		}
			
	}
	
	//return specific space spaces around a certain space
	public ArrayList<Space> getAdjacentSpaces(Space space)
	{
		ArrayList<Space> adjacentSpaces = new ArrayList<Space>();
		
		//determining where "space" is on the board
		int row = space.getRow();
		int column = space.getColumn();		
		
		//gathering spaces
		int a = -1;
		int b = -1;
		for (int i = 0; i < 8; i++) // We do 8 because we skip the middle space, which is where "space" is located
		{
			Space potentialSpace = getSpaceFromOffset(space, a, b);
			
			//if null, there's no space so don't add
			if (potentialSpace != null)
			{
				adjacentSpaces.add(potentialSpace);
			}
			
			//incrementing
			if (b < 1) //before turning 1, increase for move column
			{
				b++;
			}
			else if (b >= 1) //if 1 or higher, return back to -1 and increment a
			{
				b = -1;
				a++;
			}
			
			if (b == 0 && a == 0) //edge case for being 0,0. We don't want the spot of the original space
			{
				b = 1;
			}
		}
		
		//returning the list
		return adjacentSpaces;
	}
	
	public Space getSpaceFromOffset(int spaceRow, int spaceCol, int offRow, int offCol)
	{
		//checking if both offsets are within the bounds of the board
		// Row
		if (spaceRow + offRow < 0 || spaceRow + offRow >= mSize)
		{
			//no space here
			return null;
		}
		// Column
		if (spaceCol + offCol < 0 || spaceCol + offCol >= mSize)
		{
			//no space here
			return null;
		}
		
		//getting the space
		return mSpaces[spaceCol + offCol][spaceRow + offRow];
	}
	public Space getSpaceFromOffset(Space space, int offRow, int offCol)
	{
		//Testing for out of bounds
		//System.out.println("row: " + (space.getRow() + offRow) + " | col: " + (space.getColumn() + offCol) + " | Size: " + mSize);
		
		//checking if both offsets are within the bounds of the board
		// Row
		if (space.getRow() + offRow < 0 || space.getRow() + offRow >= mSize)
		{
			//no space here
			return null;
		}
		// Column
		if (space.getColumn() + offCol < 0 || space.getColumn() + offCol >= mSize)
		{
			//no space here
			return null;
		}
		
		//getting the space
		return mSpaces[space.getColumn() + offCol][space.getRow() + offRow];
	}

	//get the pieces on the board
	//	If a color is given, only get those pieces
	//	If no color, get all
	public ArrayList<Piece> getPiecesOnBoard(DisplayColor color)
	{
		ArrayList<Piece> allPieces = new ArrayList<Piece>();
		
		for (Piece p : mPieces)
		{
			//if not on the board, don't add
			if (p.getSpace() != null)
			{
				//if color, only if that color
				if (p.getColor() == color)
				{
					allPieces.add(p);
				}
			}
		}
		return allPieces;
	}
	public ArrayList<Piece> getPiecesOnBoard()
	{
		ArrayList<Piece> allPieces = new ArrayList<Piece>();
		
		for (Piece p : mPieces)
		{
			//if not on the board, don't add
			if (p.getSpace() != null)
			{
				allPieces.add(p);
			}
		}
		return allPieces;
	}

	
	//returns the code for the space, based off a-h and 8-1
	public String getSpaceCode(int row, int col)
	{
		String spaceCode = "";
		
		//getting letter (Column).  Starts at character 'a', then add number to get appropriate character
		spaceCode += (char)('a' + col);
		
		//getting number (Row)
		spaceCode += 8 - row;
		
		return spaceCode;
	}
	public String getSpaceCode(Space space)
	{
		String spaceCode = "";
		
		int row = space.getRow();
		int col = space.getColumn();
		
		//getting letter (Column).  Starts at character 'a', then add number to get appropriate character
		spaceCode += (char)('a' + col);
		
		//getting number (Row)
		spaceCode += 8 - row;
		
		return spaceCode;
	}
	
	public GridPane getGridPane() { return mGridPane; }
	
	public void setIsSelectingPiece(boolean selectingPiece) { mIsSelectingPiece = selectingPiece; }
	public boolean getIsSelectingPiece() { return mIsSelectingPiece; }
	
	//returns a soft clone used for testing
	public Board getCloneBoardData()
	{
		//this calls the copy constructor
		Board board = new Board(this);
		
		//returning this new board clone
		return board;
	}

	
	public ChessGame getChessGame() { return mChessGame; }
	public Player[] getPlayers() { return mPlayers; }
	public King[] getKings() { return mKings; }
	public ArrayList<Piece> getPieces() { return mPieces; }
	public int getPlayerTurn() { return mPlayerTurn; }
	
	public void addPiece(Piece piece)
	{
		mPieces.add(piece);
	}
}
