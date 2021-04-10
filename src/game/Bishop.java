package game;

import java.io.File;
import java.util.ArrayList;

import game.Space.DisplayColor;
import javafx.scene.image.Image;

public class Bishop extends Piece
{
	
	public Bishop(Board board, Space space, DisplayColor color)
	{
		super(board, space, color);
		
		//setting the piece's character
		setPieceCharacter("B");
		
		//setting the sprite to the bishop, depending on color of side
		//	Also setting the UNICODE character, since black and white are unique
		File spriteFile;
		if (color == DisplayColor.White) //WHITE
		{
			spriteFile = new File("data/pieces/whiteBishop.png");
			
			//Setting Unicode character white
			setUnicodeCharacter("\u2657");
		}
		else //BLACK
		{
			spriteFile = new File("data/pieces/blackBishop.png");
			
			//Setting Unicode character black
			setUnicodeCharacter("\u265D");
		}
		String spriteURI = spriteFile.toURI().toString();
		Image spriteImage = new Image(spriteURI);

		setSprite(spriteImage);
		
	}

	// Bishop can only move diagonally
	@Override
	public ArrayList<Space> getPossibleMoves()
	{
		//array of potential spaces
		ArrayList<Space> possibleMoves = new ArrayList<Space>();
		
		//we're going to check each of the four possible diagonal directions
		//	if we hit an ally piece, don't keep that space and stop checking in that direction
		//	if we hit an enemy piece, keep that space but stop checking in that direction
		//	if we hit null, stop checking in that direction
		//	if we hit an open space, keep and and continue in that direction
		
		// Up and Left
		possibleMoves.addAll(getPossibleMovesInLine(getSpace(), -1, -1));
		// Up and Right
		possibleMoves.addAll(getPossibleMovesInLine(getSpace(), -1, 1));
		// Down and Left
		possibleMoves.addAll(getPossibleMovesInLine(getSpace(), 1, -1));
		// Down and Right
		possibleMoves.addAll(getPossibleMovesInLine(getSpace(), 1, 1));
		
		//now that we have all potential moves, return
		return possibleMoves;
	}
	
	//Bishop character
	
	
}
