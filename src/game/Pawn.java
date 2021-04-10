package game;

import java.io.File;
import java.text.ParsePosition;
import java.util.ArrayList;

import game.Space.DisplayColor;
import javafx.scene.image.Image;
import sun.nio.cs.MS1250;

public class Pawn extends Piece
{

	public Pawn(Board board, Space space, DisplayColor color)
	{
		super(board, space, color);
		
		//setting the piece's character
		setPieceCharacter(""); //The Pawn uses no character
		
		//setting the sprite to the Pawn, depending on color of side
		File spriteFile;
		if (color == DisplayColor.White) //WHITE
		{
			spriteFile = new File("data/pieces/whitePawn.png");
			
			//Setting Unicode character white
			setUnicodeCharacter("\u2659");
		}
		else //BLACK
		{
			spriteFile = new File("data/pieces/blackPawn.png");
			
			//Setting Unicode character black
			setUnicodeCharacter("\u265F");
		}
		String spriteURI = spriteFile.toURI().toString();
		Image spriteImage = new Image(spriteURI);

		setSprite(spriteImage);
	}
	
	// Pawn has few movement options but also some special cases
	@Override
	public ArrayList<Space> getPossibleMoves()
	{
		//array of potential spaces
		ArrayList<Space> possibleMoves = new ArrayList<Space>();
		
		//we're going to check each of the four possible diagonal directions
		//	if we hit an ally piece, don't keep that space and stop checking in that direction
		//	if we hit an enemy piece, it depends
		//	if we hit null, stop checking in that direction
		//	if we hit an open space, keep and and continue in that direction
		
		//ALSO
		//	Pawns can only move straight forward, based on color
		//	We must check if first move, since it could move 2 spaces forward
		//	We must check for LePasant(?)
		
		//TODO: Le Pasant(?). Weird case since I don't believe it takes a turn. Figure out how to implement
		
		//getting potential movement
		ArrayList<Space> adjacentSpaces = new ArrayList<Space>();
		
		// Determining color, which changes the movement
		if (getColor() == DisplayColor.White)
		{
			//white moves up
			adjacentSpaces.addAll(getPawnSpacesBasedOnColor(true));
		}
		else
		{
			//black moves down
			adjacentSpaces.addAll(getPawnSpacesBasedOnColor(false));
		}
				
		possibleMoves = adjacentSpaces;
		
		//System.out.println("possible moves: " + possibleMoves.size());
		//System.out.println("adjacent moves: " + adjacentSpaces.size());
		
//		for (Space s : possibleMoves)
//		{
//			System.out.println("space: " + getBoard().getSpaceCode(s));
//		}
		
		//now that we have all potential moves, return
		return possibleMoves;
	}

	private ArrayList<Space> getPawnSpacesBasedOnColor(boolean movingUp)
	{
		//array of potential spaces
		ArrayList<Space> possibleMoves = new ArrayList<Space>();
		
		//variable for vertical movement
		int vMov = 0;
		
		//depending on up or down, will be positive or negative
		if (movingUp)
		{
			vMov = -1;
		}
		else
		{
			vMov = 1;
		}
		
		// IN FRONT
		//getting the space in front
		Space inFront = getBoard().getSpaceFromOffset(this.getSpace(), vMov, 0);
		
		//if there is no space, skip
		if (inFront != null)
		{
			//if space in front has no piece, we can move there
			if (inFront.getPiece() == null)
			{
				possibleMoves.add(inFront);
			}	
		}
		
		// DOUBLE MOVE
		// if only 1 from first row, then this move can be made
		//		Relates to whether player has moved that pawn yet
		//Must also make sure the first space is valid. Cannot jump over
		//if there is no space, skip
		if (inFront != null)
		{
			if (inFront.getPiece() == null)
			{
				int size = getBoard().mSize;
				int vPos = getSpace().getRow();
				if (movingUp && vPos == 6)
				{
					//getting space
					Space twoInFront = getBoard().getSpaceFromOffset(this.getSpace(), vMov * 2, 0);
					
					//if space in front has no piece, we can move there
					if (twoInFront.getPiece() == null)
					{
						possibleMoves.add(twoInFront);
					}
				}
				else if (!movingUp && vPos == 1)
				{
					//getting space
					Space twoInFront = getBoard().getSpaceFromOffset(this.getSpace(), vMov * 2, 0);
					
					//if space in front has no piece, we can move there
					if (twoInFront.getPiece() == null)
					{
						possibleMoves.add(twoInFront);
					}
				}
			}
		}
		
		//DIAGONAL ATTACK
		//	If there's an enemy 1 forward and 1 side, can move there
		Space frontLeft = getBoard().getSpaceFromOffset(getSpace(), vMov, -1);
		Space frontRight = getBoard().getSpaceFromOffset(getSpace(), vMov, 1);
		
		//checking if a space is there
		if (frontLeft != null)
		{
			//checking if an enemy is there
			if (frontLeft.getPiece() != null)
			{
				if (frontLeft.getPiece().getColor() != this.getColor())
				{
					//add
					possibleMoves.add(frontLeft);
				}
			}
		}
		if (frontRight != null)
		{
			//checking if an enemy is there
			if (frontRight.getPiece() != null)
			{
				if (frontRight.getPiece().getColor() != this.getColor())
				{
					//add
					possibleMoves.add(frontRight);
				}
			}
		}
		
		//returning the possible moves
		return possibleMoves;
	}
	
	
	
	
}
