package game;

import java.io.File;
import java.util.ArrayList;

import game.Space.DisplayColor;
import javafx.scene.image.Image;

public class Queen extends Piece
{

	public Queen(Board board, Space space, DisplayColor color)
	{
		super(board, space, color);
		
		//setting the piece's character
		setPieceCharacter("Q");
		
		//setting the sprite to the Queen, depending on color of side
		File spriteFile;
		if (color == DisplayColor.White) //WHITE
		{
			spriteFile = new File("data/pieces/whiteQueen.png");
			
			//Setting Unicode character white
			setUnicodeCharacter("\u2655");
		}
		else //BLACK
		{
			spriteFile = new File("data/pieces/blackQueen.png");
			
			//Setting Unicode character black
			setUnicodeCharacter("\u265B");
		}
		String spriteURI = spriteFile.toURI().toString();
		Image spriteImage = new Image(spriteURI);

		setSprite(spriteImage);
	}
	
	// Queen can move in 8 directions
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
		// Up
		possibleMoves.addAll(getPossibleMovesInLine(getSpace(), -1, 0));
		// Down
		possibleMoves.addAll(getPossibleMovesInLine(getSpace(), 1, 0));
		// Left
		possibleMoves.addAll(getPossibleMovesInLine(getSpace(), 0, -1));
		// Right
		possibleMoves.addAll(getPossibleMovesInLine(getSpace(), 0, 1));
		
		//now that we have all potential moves, return
		return possibleMoves;
	}

	
	
	
	
	
}
