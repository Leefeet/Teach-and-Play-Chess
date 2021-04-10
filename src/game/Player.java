package game;

import game.Space.DisplayColor;

//Class mostly holds data. Important for the name of the player
public class Player
{
	//name of the player
	private String mName = "";
	
	//color of this player's pieces
	private DisplayColor mPieceColor = null;
	
	//link to the board
	private Board mBoard = null;
	
	//number of moves made by player
	private int mMovesMade = 0;
	
	//default, no parameters
	Player()
	{
		
	}
	
	//added name parameter
	Player(String name)
	{
		setName(name);
	}
	
	//copy constructor
	Player(Player otherPlayer, Board newBoard)
	{
		setName(otherPlayer.getName());
		setPieceColor(otherPlayer.getPieceColor());
		setBoard(newBoard);
		setMovesMade(otherPlayer.getMovesMode());
	}
	
	public void setName(String name) { mName = name; }
	public void setPieceColor(DisplayColor color) { mPieceColor = color; }
	public void setBoard(Board board) { mBoard = board; }
	public void setMovesMade(int movesMade) { mMovesMade = movesMade; }
	
	public String getName() { return mName; }
	public DisplayColor getPieceColor() {return mPieceColor; }
	public Board getBoard() { return mBoard; }
	public int getMovesMode() { return mMovesMade; }
}
