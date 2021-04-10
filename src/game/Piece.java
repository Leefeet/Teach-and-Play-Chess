package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import game.Space.DisplayColor;
import javafx.scene.image.Image;

public abstract class Piece
{
	//image representation of the piece
	private Image mSprite = null;
	
	//link to the owning space
	private Space mSpace = null;
	
	//link to the board
	private Board mBoard = null;
	
	//Color of this piece
	private DisplayColor mColor = null;
	
	//Single and Unicode character representing this piece. Nothing for abstract piece
	private String mPieceCharacter = "";
	private String mUnicodeCharacter = "";
	
	//bool indicating if this piece is a King or not
	private boolean mIsKing = false;
	
	//constructor
	public Piece(Board board, Space space, DisplayColor color)
	{
		setBoard(board);
		setSpace(space);
		setColor(color);
	}
	//copy constructor
	public Piece(Piece otherPiece, Board newBoard, Space newSpace)
	{
		setBoard(newBoard);
		setSpace(newSpace);
		setColor(otherPiece.getColor());
	}

	public ArrayList<Space> removeMovesThatCreateCheck(ArrayList<Space> moveList, int playerTurn)
	{
		ArrayList<Space> possibleMoves = new ArrayList<Space>();
		
		for (Space s : moveList)
		{
			possibleMoves.add(s);
		}
		
		//we need to check every possible move and remove the ones that will lead the player's own King to check
		//	If a move results in check, remove it as an option
		
		//using just the one piece
		//we're checking every possible move on a clone board
		for (Space s : moveList)
		{
			//we're going to make a DEEP copy of the board we can test with
			Board testBoard = mBoard.getCloneBoardData();
			
			//now that we have a separate board we can manipulate, let's test the move
			//	finding the specific p piece on the board
			Piece testPiece = testBoard.getSpaces()[this.getSpace().getColumn()][this.getSpace().getRow()].getPiece();
			
			//moving the testPiece to this location
			testPiece.movePiece(testBoard.getSpaces()[s.getColumn()][s.getRow()]);
			
			//checking all possible enemy moves now that we moved a piece
			//	We need to check if the king is in danger
			boolean kingInCheck = testBoard.getKings()[playerTurn].determineIfInDanger();
			
			//if the king is not in danger, then this move would be valid
			if (!kingInCheck)
			{
				//do nothing, we want this move
			}
			else //The King would be in check, remove this move
			{
				possibleMoves.remove(s);
			}
		}
		
		return possibleMoves;
	}
		
	//function returns list of potential move spaces
	public abstract ArrayList<Space> getPossibleMoves();
	
	//function returns a list of potential moves in one continuous direction
	public ArrayList<Space> getPossibleMovesInLine(Space startingSpace, int rowOffset, int columnOffset)
	{
		ArrayList<Space> possibleMoves = new ArrayList<Space>();
		
		Space potentialSpace = startingSpace; //Start the potential space in the position of the current piece
		while (potentialSpace != null)
		{
			//getting the space one up and to the left (negative goes up)
			potentialSpace = getBoard().getSpaceFromOffset(potentialSpace, rowOffset, columnOffset);
			
			//checking valid uses for space
			if (potentialSpace == null) //not a valid space on the board
			{
				//break from loop since we cannot use this space and no future spaces will work
				break;
			}
			//if there's a piece, check for color
			if (potentialSpace.getPiece() != null)
			{
				if (potentialSpace.getPiece().getColor() == startingSpace.getPiece().getColor()) //if colors match, it's an ally
				{
					//break from loop since we hit an ally
					break;
				}
				else if (potentialSpace.getPiece().getColor() != startingSpace.getPiece().getColor()) //if color doesn't match, then enemy
				{
					//add space to the array
					possibleMoves.add(potentialSpace);
					
					//break since we cannot move farther
					break;
				}
			}
			else //we have an empty space
			{
				//add to array but don't break to continue loop
				possibleMoves.add(potentialSpace);
			}
		}
		
		return possibleMoves;
	}
	
	//function to move the piece (assuming an appropriate space was chosen)
	public void movePiece (Space destination)
	{
		//checking if there's a piece at the destination space
		Piece otherPiece = destination.getPiece();
		
		//if the piece is no null, then we are landing on another piece. kill it then
		if (otherPiece != null)
		{
			//killing the piece
			otherPiece.killPiece();
		}
		
		//moving this piece to that location
		mSpace.removePiece(); //removing this piece from original space
		setSpace(destination); //moving this piece to new space
		destination.setPiece(this);
	}
	
	public void killPiece()
	{
		//removing piece from space
		mSpace.removePiece();
		setSpace(null);
		
		// moving piece to off the board
		//TODO add function to move piece off the board
	}
	
	//looks at enemy moves and determines if the piece could get killed in its current location
	public boolean determineIfInDanger()
	{
		ArrayList<Piece> boardPieces = null;
		
		//getting the moves of all enemy pieces
		if (mColor == DisplayColor.White)
		{
			boardPieces = mBoard.getPiecesOnBoard(DisplayColor.Black);
		}
		else // Black
		{
			boardPieces = mBoard.getPiecesOnBoard(DisplayColor.White);
		}
		
		//checking all pieces and testing if any of them are hitting this piece
		for (Piece p : boardPieces)
		{
			//checking every possible move
			for (Space s : p.getPossibleMoves())
			{
				//if the space is the same as this piece, then we have a hit
				if (s.equals(mSpace))
				{
					return true;
				}
			}
		}
		
		//otherwise, there is not danger
		return false;
	}
	
	//determiens if there's a possible move to make to save the piece
	public HashMap<Piece, ArrayList<Space>> determineIfCheckMate()
	{
		//creating a map to contain The movable pieces and potential destination spaces
		HashMap<Piece, ArrayList<Space>> piecesAndMoves = new HashMap<Piece, ArrayList<Space>>();
		
		//first check to make sure the piece is actually in check
		if (!determineIfInDanger())
		{
			//return nothing
			return null;
		}
		//if not, continue
		
		ArrayList<Piece> boardPieces = null;
				
		//getting all enemy pieces
		if (mColor == DisplayColor.White)
		{
			boardPieces = mBoard.getPiecesOnBoard(DisplayColor.Black);
		}
		else // Black
		{
			boardPieces = mBoard.getPiecesOnBoard(DisplayColor.White);
		}
		
		/*
		so for this, we need to check all possible moves by each of ally player's pieces and see if they remove check
			Imagine that we move the piece, then recheck the enemy moves
			What we can do is make a new *hypothetical* version of the board and run tests on that
		*/
		
		//boolean determining if in check mate
		boolean isInCheckMate = true;
		
		//going through all of this player's pieces
		for (Piece p : mBoard.getPiecesOnBoard(mColor))
		{
			//refresh the board clone
			//get the space location of a possible move
			//move the piece there
			//test if still in check
				//if not in check anymore, save that move somewhere
			
			//we're checking every possible move on a clone board
			for (Space s : p.getPossibleMoves())
			{
				//we're going to make a DEEP copy of the board we can test with
				Board testBoard = mBoard.getCloneBoardData();
				
				//now that we have a separate board we can manipulate, let's test the move
				//	finding the specific p piece on the board
				Piece testPiece = testBoard.getSpaces()[p.getSpace().getColumn()][p.getSpace().getRow()].getPiece();
				
				//moving the testPiece to this location
				testPiece.movePiece(testBoard.getSpaces()[s.getColumn()][s.getRow()]);
				
				//checking all possible enemy moves now that we moved a piece
				//	We need to check if the king is in danger
				boolean kingInCheck = testBoard.getKings()[testBoard.getPlayerTurn()].determineIfInDanger();
				
				//if the king is not in danger, then this move would be valid
				if (!kingInCheck)
				{
					//king cannot be in checkmate if there's a valid move
					isInCheckMate = false;
					
//					System.out.println("The Move of " + testPiece.getUnicodeCharacter() + " from space " +
//							mBoard.getSpaceCode(p.getSpace()) + " to Space " + testBoard.getSpaceCode(testPiece.getSpace()) +
//							" can protect the King from Check");
					
					//adding this move to the map;
					//	First check if this piece already has data within the hash map
						//	We're using  the s and p variables so it links to the original board
					if (piecesAndMoves.containsKey(p))
					{
						//collecting the array list already inside
						ArrayList<Space> possibleMoves = piecesAndMoves.get(p);
						
						//add to it
						possibleMoves.add(s);
						
						//setting new array within has map
						piecesAndMoves.replace(p, possibleMoves);
					}
					else //not in map yet, so add the move
					{
						ArrayList<Space> possibleMoves = new ArrayList<Space>();
						
						//add to it
						possibleMoves.add(s);
						
						//setting new array within has map
						piecesAndMoves.put(p, possibleMoves);
					}
				}
				
			}

		}
		
//		//checking all pieces and testing if any of them are hitting this piece
//		for (Piece p : boardPieces)
//		{
//			//checking every possible move
//			for (Space s : p.getPossibleMoves())
//			{
//				//if the space is the same as this piece, then we have a hit
//				if (s.equals(mSpace))
//				{
//					return true;
//				}
//			}
//		}
		
		//return the resulting list of possible moves to make
		//	Otherwise, return null if empty
		if (piecesAndMoves.isEmpty())
		{
			return null;
		}
		else
		{
			return piecesAndMoves;
		}
	}
	
	public String getPieceCharacter() { return mPieceCharacter; }
	// sub-classes use this to set the character
	protected void setPieceCharacter(String character) { mPieceCharacter = character; }
	
	public String getUnicodeCharacter() { return mUnicodeCharacter; }
	// sub-classes use this to set the character
	protected void setUnicodeCharacter(String character) { mUnicodeCharacter = character; }
	
	//setters and getters
	public void setSpace(Space space) { mSpace = space; }
	public void setBoard(Board board) { mBoard = board; }
	public void setColor(DisplayColor color) { mColor = color; }
	public void setSprite(Image sprite) { mSprite = sprite; }
	public void setIsKing(boolean isKing) { mIsKing = isKing; }
	
	public Space getSpace() { return mSpace; }
	public Board getBoard() { return mBoard; }
	public DisplayColor getColor() { return mColor; }
	public Image getSprite() { return mSprite; }
	public boolean getIsKing() { return mIsKing; }
}
